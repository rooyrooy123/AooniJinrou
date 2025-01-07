package me.rooyrooy.aooniJinrou.chest

import kotlinx.coroutines.*
import me.rooyrooy.aooniJinrou.gameAreaEffectCloudDurationSpeed
import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.math.roundToInt

object PluginInstance {
    lateinit var plugin: JavaPlugin
}

class ChestParticle() {
    // パーティクルの表示を開始するメソッド
    private var durationRate: Double = 1.0 //乗数 multiplier これが0.5なら時間半減できる。
    private var areaEffectCloud: AreaEffectCloud? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    fun durationToHalf(){ //誤射時にチェストのスピードUP
        val entities = gameWorld!!.entities
        val areaEffectClouds = entities.filterIsInstance<AreaEffectCloud>()
        for (cloud in areaEffectClouds) {
            gameAreaEffectCloudDurationSpeed[cloud.uniqueId] = 2
        }
    }
    fun startTimer(location: Location) {
        areaEffectCloud = location.world?.spawn(location, AreaEffectCloud::class.java)?.apply {
            color = Color.RED
            radius = 0.5f
            duration = (3600 * durationRate).toInt() // 長めの効果時間
            applyEffect()
        }
        gameAreaEffectCloudDurationSpeed[areaEffectCloud!!.uniqueId] = 1
        var counter = 0
        scope.launch {
            while(!areaEffectCloud!!.isDead) {
                val speed = gameAreaEffectCloudDurationSpeed[areaEffectCloud!!.uniqueId] ?: 1 //通常は1 ただ、狩人誤射時に2になる。
                when (counter) {
                    in 0..30 -> areaEffectCloud?.color = Color.RED // 赤色
                    in 31..90 -> areaEffectCloud?.color = Color.YELLOW // 黄色
                    in 91..180 -> areaEffectCloud?.color = Color.BLUE // 青色
                    else -> {
                        areaEffectCloud!!.remove()
                        scope.cancel()
                    }
                }
                delay(1 * 1000L)
                counter += speed
            }

        }
    }


    // パーティクルの再適用
    private fun applyEffect() {
        areaEffectCloud?.let {
            it.radius = 2.0f
            it.duration = (3600 * durationRate).toInt()
        }
    }


}
        /*val particle = true
        var counter = 0

        scope.launch {
            // メインスレッドでじっこうしなきゃあかんから
            while(particle) {
                if (counter == 0) {
                    Bukkit.getScheduler().runTask(PluginInstance.plugin, Runnable {
                        spawnParticle(16720932, 600)
                    })
                }else if (counter == 30) {
                    Bukkit.getScheduler().runTask(PluginInstance.plugin, Runnable {
                        spawnParticle(16771420, 1200)
                    })
                }else if (counter == 90){
                    Bukkit.getScheduler().runTask(PluginInstance.plugin, Runnable {
                        spawnParticle(2818303, 1800)
                    })
                }
                delay(1 * 1000L)
                counter ++
            }
        }

        val plugin = Bukkit.getPluginManager().getPlugin("AooniJinrou") ?: return
        val startTime = System.currentTimeMillis()

        object : BukkitRunnable() {
            override fun run() {
                val elapsedTime = (System.currentTimeMillis() - startTime) / 1000 // 経過時間（秒単位）
                when (elapsedTime) {
                    0L -> spawnParticle(16720932, 600)
                    30L -> spawnParticle(16771420, 1200)
                    90L -> spawnParticle(2818303, 1800)
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒ごとに実行


    // パーティクルを表示する関数
    private fun spawnParticle(color: Int,duration: Int) {
        val areaEffectCloud =
            location.world?.spawnEntity(location, EntityType.AREA_EFFECT_CLOUD) as? AreaEffectCloud ?: return
        // NBTを設定
        val nbtEntity = NBTEntity(areaEffectCloud)

        nbtEntity.setString("Particle", "dust_color_transition")
        nbtEntity.setBoolean("CustomNameVisible", false)
        nbtEntity.setFloat("Radius", 0.5f)
        nbtEntity.setInteger("Duration", duration)
        nbtEntity.setInteger("Color", color)
    }
*/