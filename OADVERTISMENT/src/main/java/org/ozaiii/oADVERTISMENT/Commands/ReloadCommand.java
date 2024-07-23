package org.ozaiii.oADVERTISMENT.Commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ozaiii.oADVERTISMENT.Managers.ConfigManager;

public class ReloadCommand implements CommandExecutor {
    private final ConfigManager configManager;

    public ReloadCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Komutu yalnızca OP oyuncuların kullanabilmesi
        if (!sender.hasPermission("oadvertisment.reload")) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanma yetkiniz yok.");
            return false;
        }

        // Config'i yeniden yükle
        configManager.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Konfigürasyon dosyası başarıyla yeniden yüklendi.");
        return true;
    }
}
