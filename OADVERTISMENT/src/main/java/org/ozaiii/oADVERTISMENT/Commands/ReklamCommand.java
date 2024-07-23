package org.ozaiii.oADVERTISMENT.Commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.ozaiii.oADVERTISMENT.Managers.ConfigManager;
import org.ozaiii.oADVERTISMENT.Managers.CooldownManager;
import org.ozaiii.oADVERTISMENT.OADVERTISMENT;

import java.util.HashMap;
import java.util.Map;

public class ReklamCommand implements CommandExecutor {
    private final OADVERTISMENT oadvertisment;
    private final CooldownManager cooldownManager;
    private final ConfigManager configManager;
    private final Economy economyAPI;

    // Son reklam zamanını saklamak için bir HashMap
    private final Map<Player, Long> playerLastAdvertisementTimes = new HashMap<>();
    private long lastGlobalAdvertisementTime = 0;

    public ReklamCommand(OADVERTISMENT oadvertisment, CooldownManager cooldownManager, ConfigManager configManager, Economy economyAPI) {
        this.oadvertisment = oadvertisment;
        this.cooldownManager = cooldownManager;
        this.configManager = configManager;
        this.economyAPI = economyAPI;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Komutu sadece oyuncular kullanabilir
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getString("messages.only_players")));
            return false;
        }

        Player player = (Player) commandSender;

        // Global cooldown kontrolü
        long currentTime = System.currentTimeMillis();
        long globalCooldown = configManager.getLong("global_cooldown", 120); // Varsayılan 2 dakika (120 saniye)

        if (currentTime - lastGlobalAdvertisementTime < globalCooldown * 1000) {
            long remainingTime = (globalCooldown * 1000 - (currentTime - lastGlobalAdvertisementTime)) / 1000;
            player.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&',
                    configManager.getString("messages.global_cooldown")
                            .replace("{time}", String.valueOf(remainingTime))));
            return false;
        }

        // Cooldown kontrolü
        if (cooldownManager.isOnCooldown(player)) {
            long remainingTime = (cooldownManager.getCooldownTime() - (System.currentTimeMillis() - cooldownManager.getCooldownStartTime(player))) / 1000;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getString("messages.cooldown")
                    .replace("{time}", String.valueOf(remainingTime))));
            return false;
        }

        // Ekonomi API kontrolü
        double requiredAmount = getRequiredAmount();
        if (economyAPI.getBalance(player) < requiredAmount) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getString("messages.insufficient_funds")
                    .replace("{amount}", String.valueOf(requiredAmount))));
            return false;
        }

        // Mesaj uzunluğu kontrolü
        int maxMessageLength = configManager.getInt("max_message_length");
        if (args.length == 0 || String.join(" ", args).length() > maxMessageLength) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getString("messages.invalid_message")
                    .replace("{max_length}", String.valueOf(maxMessageLength))));
            return false;
        }

        // Mesajı renkli olarak gönder
        String message = String.join(" ", args);
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Mesajı yayınla
        player.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', configManager.getString("messages.advertisement_prefix") + message));

        // Ekonomik işlemi gerçekleştirin (örneğin, ücreti düşürme)
        economyAPI.withdrawPlayer(player, requiredAmount);

        // Cooldown süresini ayarla
        cooldownManager.setCooldown(player);

        // Global reklam zamanını güncelle
        lastGlobalAdvertisementTime = currentTime;

        // Başarı mesajı gönder
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getString("messages.success")));

        // Ses çal
        String soundName = configManager.getString("advertisment.sound", "ENTITY_PLAYER_LEVELUP");
        float volume = (float) configManager.getDouble("advertisment.sound_volume", 1.0);
        float pitch = (float) configManager.getDouble("advertisment.sound_pitch", 1.0);
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Ses yapılandırması geçersiz: " + soundName);
        }

        // Ekran mesajını göster
        String title = ChatColor.translateAlternateColorCodes('&', message); // Title: Reklam metni
        String subtitle = ChatColor.translateAlternateColorCodes('&', configManager.getString("advertisment.subtitle", "Reklam veren: %player%").replace("%player%", player.getName())); // Subtitle: Reklam veren oyuncunun ismi

        player.sendTitle(title, subtitle, 10, 70, 20);

        // Discord webhook'u gönder
        if(configManager.getBoolean("webhooks.sendwebhooks")){
            oadvertisment.sendWebhook(configManager.getString("messages.discordEmbedTitle"), configManager.getString("messages.discordEmbedDesc"));
        }
        return true;
    }


    /**
     * Ekonomi API'si ile gereken miktarı döner.
     *
     * @return Gerekli miktar
     */
    private double getRequiredAmount() {
        // ConfigManager'dan gerekli miktarı alır, varsayılan olarak 500 döner
        return configManager.getDouble("advertisment.amount", 500);
    }
}
