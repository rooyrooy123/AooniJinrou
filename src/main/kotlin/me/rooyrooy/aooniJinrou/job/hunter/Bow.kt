package me.rooyrooy.aooniJinrou.job.hunter

import me.rooyrooy.aooniJinrou.PluginProvider.plugin
import me.rooyrooy.aooniJinrou.gameJobList
import me.rooyrooy.aooniJinrou.gameStart
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Bow (private val shooter: Player){
    fun hunterCoolTime(bow: ItemStack,arrow: Arrow){
       if (!gameStart) return
       if (gameJobList[shooter] == "HUNTER"){
           shooter.setCooldown(bow.type, 5 * 20)
           bow.durability = 0
           Bukkit.getScheduler().runTaskLater(plugin, Runnable {
               // 実行する処理
               shooter.inventory.addItem(ItemStack(Material.ARROW,1))
           }, 5 * 20L) // 5秒（1秒 = 20ティック）
           Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
               // 矢が飛んでいる間だけパーティクルを表示
               if (!arrow.isDead) {
                   val location = arrow.location
                   location.world?.spawnParticle(Particle.LAVA, location, 3, 1.0, 1.0, 1.0, 0.1)
               }
           }, 0L, 1L) // 毎ティック（0.05秒）でパーティクルを表示
       }
    }
}