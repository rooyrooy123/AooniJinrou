package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.EnderChest
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType

class ChestPlace(){
    fun placeAll(){
        // ワールド内のエンティティを走査
        for (entity in gameWorld!!.entities) {
            // ArmorStandかつ名前に "Floor" を含む場合に処理
            if (entity.type == EntityType.ARMOR_STAND && entity is ArmorStand) {
                if (entity.customName?.contains("Floor") == true) {
                    val location = entity.location

                    // アーマースタンドの向きを取得 ぶろっくのむきをアーマースタンドにあわせる
                    val rotation = entity.bodyYaw
                    val blockLocation = location.clone().add(0.0, 0.0, 0.0)
                    blockLocation.block.type = Material.ENDER_CHEST

                    val block = blockLocation.block
                    val blockData = block.blockData as EnderChest
                    // エンダーチェストの向きを設定
                    blockData.facing = getBlockFaceFromYaw(rotation)
                    // エンダーチェストを設定
                    block.setBlockData(blockData)
                }
            }

        }
    }
    fun place(selectFloor:Int){
        // ワールド内のエンティティを走査
        for (entity in gameWorld!!.entities) {
            // ArmorStandかつ名前に "Floor" を含む場合に処理
            if (entity.type == EntityType.ARMOR_STAND && entity is ArmorStand) {
                if (entity.customName?.contains("Floor") == true) {
                    val name = entity.name

                    if (name.replace("Floor","").toInt() == selectFloor) {//ちぇすとの階層
                        val location = entity.location
                        // アーマースタンドの向きを取得
                        val rotation = entity.bodyYaw
                        val blockLocation = location.clone().add(0.0, 0.0, 0.0)
                        blockLocation.block.type = Material.ENDER_CHEST

                        val block = blockLocation.block
                        val blockData = block.blockData as EnderChest

                        // エンダーチェストの向きを設定
                        blockData.facing = getBlockFaceFromYaw(rotation)

                        // エンダーチェストを設定
                        block.setBlockData(blockData)
                    }

                }
            }

        }
    }
    private fun getBlockFaceFromYaw(yaw: Float): BlockFace {
        return when {
            yaw >= -45f && yaw < 45f -> BlockFace.SOUTH
            yaw >= 45f && yaw < 135f -> BlockFace.WEST
            yaw >= 135f || yaw < -135f -> BlockFace.NORTH
            yaw >= -135f && yaw < -45f -> BlockFace.EAST
            else -> BlockFace.SOUTH
        }
    }
}