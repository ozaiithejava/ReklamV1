package org.ozaiii.oADVERTISMENT;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ozaiii.oADVERTISMENT.Commands.ReklamCommand;
import org.ozaiii.oADVERTISMENT.Commands.ReloadCommand;
import org.ozaiii.oADVERTISMENT.Managers.ConfigManager;
import org.ozaiii.oADVERTISMENT.Managers.CooldownManager;
import org.ozaiii.oADVERTISMENT.utils.DiscordEmbedSender;

@Getter
public final class OADVERTISMENT extends JavaPlugin {

    private static OADVERTISMENT INSTANCE;
    private ConfigManager configManager;
    private DiscordEmbedSender discordEmbedSender;
    private CooldownManager cooldownManager;
    private static Economy econ = null;
    private ReklamCommand reklamCommand;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // ConfigManager'ı başlat
        configManager = new ConfigManager(this);
        configManager.setupConfig();

        // Ekonomi API'yi başlat
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Vault Yok Diye Devre Dışıyım!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Webhook URL'lerini ve renk kodlarını configManager'dan al
        String webhookURL = configManager.getString("webhooks.giveways");
        String webhookIMGURL = configManager.getString("webhooks.imageurl");
        String webhookColorCode = configManager.getString("webhooks.embedColor");

        // DiscordEmbedSender'ı başlat
        discordEmbedSender = new DiscordEmbedSender();
        discordEmbedSender.setWebhookURL(webhookURL);

        // CooldownManager'ı başlat
        cooldownManager = new CooldownManager(configManager);

        // ReklamCommand'ı başlat
        reklamCommand = new ReklamCommand(this,cooldownManager, configManager, econ);
        this.getCommand("reklam").setExecutor(reklamCommand);
        this.getCommand("rreload").setExecutor(new ReloadCommand(configManager));


        getLogger().info("OADVERTISMENT başarıyla başlatıldı!");
    }

    @Override
    public void onDisable() {
        if (configManager != null) {
            configManager.saveConfig();
        }
    }

    public void sendWebhook(String title, String description) {
        String webhookIMGURL = configManager.getString("webhooks.imageurl");
        String webhookColorCode = configManager.getString("webhooks.embedColor");

        if (webhookIMGURL != null) {
            discordEmbedSender.sendEmbed(title, description, webhookColorCode, true, true, webhookIMGURL);
        } else {
            discordEmbedSender.sendEmbed(title, description, webhookColorCode, true, false, null);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
