package org.ozaiii.oADVERTISMENT.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private final ConfigManager configManager; // ConfigManager örneği
    private final Map<Player, Long> cooldowns = new HashMap<>();

    /**
     * CooldownManager constructor'ı.
     *
     * @param configManager ConfigManager örneği
     */
    public CooldownManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Oyuncunun eylem yapıp yapamayacağını kontrol eder.
     *
     * @param player Oyuncu
     * @return true eğer oyuncu cooldown süresindeyse, false eğer cooldown süresi dolmuşsa
     */
    public boolean isOnCooldown(Player player) {
        if (cooldowns.containsKey(player)) {
            long lastActionTime = cooldowns.get(player);
            return System.currentTimeMillis() - lastActionTime < getCooldownTime();
        }
        return false;
    }

    /**
     * Oyuncunun eylem yapma zamanını günceller.
     *
     * @param player Oyuncu
     */
    public void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }

    /**
     * Oyuncunun cooldown süresini kaldırır.
     *
     * @param player Oyuncu
     */
    public void removeCooldown(Player player) {
        cooldowns.remove(player);
    }

    /**
     * ConfigManager'dan cooldown süresini alır.
     *
     * @return Cooldown süresi milisaniye cinsinden
     */
    public long getCooldownTime() {
        // ConfigManager'dan süreyi al
        String cooldownString = configManager.getString("cooldown_time");
        try {
            // Süreyi milisaniye cinsinden hesapla
            long cooldownMillis = Long.parseLong(cooldownString) * 1000;
            return cooldownMillis;
        } catch (NumberFormatException e) {
            // Hatalı konfigürasyon durumu için varsayılan süre döner
            e.printStackTrace();
            return 6 * 60 * 60 * 1000; // Varsayılan 6 saat
        }
    }

    /**
     * Oyuncunun cooldown başlangıç zamanını alır.
     *
     * @param player Oyuncu
     * @return Cooldown başlangıç zamanı
     */
    public long getCooldownStartTime(Player player) {
        return cooldowns.getOrDefault(player, 0L);
    }
}
