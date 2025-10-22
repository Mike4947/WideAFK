package com.exemple.wideAFK.tasks;

import com.exemple.wideAFK.WideAFK;
import com.exemple.wideAFK.listeners.AFKListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AFKRewardTask extends BukkitRunnable {

    @Override
    public void run() {
        double amount = WideAFK.getInstance().getConfig().getDouble("earn-amount");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (AFKListener.isAFK(player) && player.hasPermission("wideafk.earn")) {
                WideAFK.getEconomy().depositPlayer(player, amount);
                player.sendActionBar("§a+ $" + amount + " §7(for being AFK)");
            }
        }
    }
}
