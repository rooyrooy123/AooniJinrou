package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.gameGateTopFloor
import me.rooyrooy.aooniJinrou.gameGateUnderFloor
import org.bukkit.*

class Gate {
    private val locationUnder : Location = gameGateUnderFloor.block.location
    private val locationTop : Location= gameGateTopFloor.block.location
    val world : World= locationUnder.world

    fun setup(){

        var locationPlus = locationUnder
        world.getBlockAt(locationUnder).type = Material.OBSIDIAN
        locationPlus.y += 1
        world.getBlockAt(locationUnder).type = Material.BLUE_STAINED_GLASS
        locationPlus.y += 1
        world.getBlockAt(locationUnder).type = Material.BLUE_STAINED_GLASS


        world.getBlockAt(locationTop).type = Material.GOLD_BLOCK
        locationPlus = locationTop
        locationPlus.y += 1
        world.getBlockAt(locationTop).type = Material.YELLOW_STAINED_GLASS
        locationPlus.y += 1
        world.getBlockAt(locationTop).type = Material.YELLOW_STAINED_GLASS
    }
    fun openUnder(){ //共通化しないでおく
        val locationPlus = locationUnder
        world.getBlockAt(locationUnder).type = Material.OBSIDIAN

        locationPlus.y += 1
        destroyGlassBlock(locationUnder)
        //world.getBlockAt(locationUnder).type = Material.AIR
        locationPlus.y += 1
        destroyGlassBlock(locationUnder)
        //world.getBlockAt(locationUnder).type = Material.AIR

    }
    fun openTOP(){
        val locationPlus = locationTop
        world.getBlockAt(locationUnder).type = Material.GOLD_BLOCK
        locationPlus.y += 1
        //world.getBlockAt(locationTop).type = Material.AIR
        destroyGlassBlock(locationTop)
        locationPlus.y += 1
//        world.getBlockAt(locationTop).type = Material.AIR
        destroyGlassBlock(locationTop)
    }
    private fun destroyGlassBlock(location: Location) {
        val block = location.block
        // ブロックの破壊パーティクルを生成

       world.spawnParticle(
            Particle.BLOCK_CRACK, // パーティクルの種類
            block.location.add(0.5, 0.5, 0.5), // パーティクルを中心に表示
            30, // パーティクルの数
            0.2, 0.2, 0.2, // パーティクルの範囲
            block.blockData // ブロックデータを指定（破壊したブロックの見た目）
        )
        world.playSound(
            block.location,
            Sound.BLOCK_GLASS_BREAK, // 適切な音を選択
            1.0F, // 音量
            1.0F  // 音の高さ
        )

        // ブロックを削除
        block.type = Material.AIR
    }

}