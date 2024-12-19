package me.rooyrooy.aooniJinrou.chest

import de.tr7zw.nbtapi.NBTEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable

class ChestParticle(private val location: Location) {

    private var startTime: Long = System.currentTimeMillis() // 実行開始時刻
    private var task: BukkitRunnable? = null

    // パーティクルの表示を開始するメソッド

    fun start() {
        task = object : BukkitRunnable() {
            override fun run() {
                val elapsedTime = (System.currentTimeMillis() - startTime) / 1000 // 経過時間（秒単位）
                when (elapsedTime) {
                    0.toLong() -> {
                        spawnParticle(16720932,600)
                    }
                    30.toLong() -> {
                        spawnParticle(16771420,1200)
                    }
                    90.toLong() -> {
                        spawnParticle(2818303,1800)
                    }
                }
            }
        }
        Bukkit.getPluginManager().getPlugin("AooniJinrou")?.let { task?.runTaskTimer(it, 0L, 20L) } // 1秒ごとに実行
    }

    // パーティクルを表示する関数
    private fun spawnParticle(color: Int,duration: Int) {
        val areaEffectCloud = location.world?.spawnEntity(location, EntityType.AREA_EFFECT_CLOUD) as? AreaEffectCloud ?: return
        // NBTを設定
        val nbtEntity = NBTEntity(areaEffectCloud)
        nbtEntity.setString("Particle","dust_color_transition")
        nbtEntity.setBoolean("CustomNameVisible", false)
        nbtEntity.setFloat("Radius",0.5f)
        nbtEntity.setInteger("Duration",duration)
        nbtEntity.setInteger("Color",color)
    }


}
