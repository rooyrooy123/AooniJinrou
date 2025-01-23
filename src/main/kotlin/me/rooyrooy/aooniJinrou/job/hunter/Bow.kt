package me.rooyrooy.aooniJinrou.job.hunter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rooyrooy.aooniJinrou.Items
import me.rooyrooy.aooniJinrou.gameJobList
import me.rooyrooy.aooniJinrou.gameStart
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Bow (private val shooter: Player){
    fun hunterCoolTime(bow: ItemStack,arrow: Arrow){
        if (!gameStart) return
        if (gameJobList[shooter] != "HUNTER") return
        shooter.setCooldown(bow.type, 5 * 20)
        bow.durability = 0
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            while (!arrow.isDead) { //こｔちは非同期処理で矢にパーティクルを生成
                val location = arrow.location
                location.world?.spawnParticle(Particle.LAVA, location, 3, 0.0, 0.0, 0.0, 0.0)
                delay(50L)
            }
            delay(5 * 1000L) //こっちは同期処理で5びょうごに矢を配布する
            shooter.inventory.addItem(Items.ARROW.toItemStack())
        }
    }
    fun aooniShoot(arrow: Arrow){
        if (!gameStart) return
        if (gameJobList[shooter] != "AOONI") return
        shooter.inventory.removeItem(shooter.inventory.itemInMainHand)
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            while (!arrow.isDead) { //こｔちは非同期処理で矢にパーティクルを生成
                val location = arrow.location
                location.world?.spawnParticle(Particle.FLAME, location, 3, 0.0, 0.0, 0.0, 0.0)
                delay(50L)
            }
        }

    }

}
           /*
                      Bukkit.getScheduler().runTaskLater(plugin, Runnable {
               // 実行する処理
               shooter.inventory.addItem(ItemStack(Material.ARROW,1))
           }, 5 * 20L) // 5秒（1秒 = 20ティック）
           Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
               // 矢が飛んでいる間だけパーティクルを表示
               if (!arrow.isDead) {
                   val location = arrow.location
                   location.world?.spawnParticle(Particle.LAVA, location, 3, 1.0, 1.0, 1.0, 0.1)
               }else{
                   Bukkit.getScheduler().cancelTasks(plugin)
               }
           }, 0L, 1L) // 毎ティック（0.05秒）でパーティクルを表示*/



