package me.rooyrooy.aooniJinrou

import org.bukkit.Bukkit

class Delay {
    fun executeWithDelay(task: () -> Unit, delay: Long = 1L) {
        Bukkit.getScheduler().runTaskLater(PluginProvider.plugin, Runnable {
            task()
        }, delay)
    }
}

object PluginProvider {
    val plugin: org.bukkit.plugin.Plugin
        get() = Bukkit.getPluginManager().getPlugin("AooniJinrou")
            ?: throw IllegalStateException("Plugin としてにんしきされなかった!")
}