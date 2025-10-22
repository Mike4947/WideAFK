package com.exemple.wideAFK.listeners;

import com.exemple.wideAFK.WideAFK;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class AFKListener implements Listener {

    private static final HashMap<UUID, Long> lastMovement = new HashMap<>();
    private static final HashMap<UUID, Boolean> afkStatus = new HashMap<>();

    public AFKListener() {
        // Start task to check AFK status
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                int timeout = WideAFK.getInstance().getConfig().getInt("afk-timeout") * 1000;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("wideafk.earn")) continue;
                    long lastMove = lastMovement.getOrDefault(player.getUniqueId(), now);
                    boolean isAfk = afkStatus.getOrDefault(player.getUniqueId(), false);

                    if (!isAfk && now - lastMove >= timeout) {
                        afkStatus.put(player.getUniqueId(), true);
                        if (WideAFK.getInstance().getConfig().getBoolean("show-title")) {
                            player.sendTitle(
                                    WideAFK.getInstance().getConfig().getString("afk-title").replace("&", "§"),
                                    WideAFK.getInstance().getConfig().getString("afk-subtitle").replace("&", "§"),
                                    10, 999999, 10
                            );
                        }
                    } else if (isAfk && now - lastMove < timeout) {
                        afkStatus.put(player.getUniqueId(), false);
                        player.resetTitle();
                    }
                }
            }
        }.runTaskTimer(WideAFK.getInstance(), 20L, 20L);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        lastMovement.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static boolean isAFK(Player player) {
        return afkStatus.getOrDefault(player.getUniqueId(), false);
    }
}
