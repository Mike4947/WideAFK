package com.exemple.wideAFK;

import com.exemple.wideAFK.listeners.AFKListener;
import com.exemple.wideAFK.tasks.AFKRewardTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WideAFK extends JavaPlugin {

    private static WideAFK instance;
    private static Economy econ;
    private AFKRewardTask rewardTask;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new AFKListener(), this);

        rewardTask = new AFKRewardTask();
        rewardTask.runTaskTimer(this, 20L, 20L * getConfig().getInt("pay-interval"));

        getCommand("wideafk").setExecutor((sender, command, label, args) -> {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("wideafk.reload")) {
                    sender.sendMessage("§cYou do not have permission.");
                    return true;
                }
                reloadConfig();
                sender.sendMessage("§aWideAFK config reloaded!");
                return true;
            }
            sender.sendMessage("§eUsage: /wideafk reload");
            return true;
        });

        getLogger().info("WideAFK enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (rewardTask != null) rewardTask.cancel();
        getLogger().info("WideAFK disabled.");
    }

    public static WideAFK getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }
}
