package me.rooyrooy.aooniJinrou

import org.bukkit.plugin.java.JavaPlugin


class AooniJinrou : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
    }
    override fun onDisable() {
        // Plugin shutdown logic
    }
}
