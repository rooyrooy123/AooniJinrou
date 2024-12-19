package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.EnderChest
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitTask

class ChestPlace(
    private val config: FileConfiguration) {
    fun placeChest(location: Location){
        val world = location.world
        val block = world.getBlockAt(location)
        block.type = Material.ENDER_CHEST
    }
    fun placeAll() {
        val floorList = config.getConfigurationSection("AooniJinrou.Location.${gameWorld}.Chest")!!.getKeys(false)

        for (floor in floorList) {
            val coordinatesList = config.getList("AooniJinrou.Location.${gameWorld}.Chest.Floor${floor}") as List<*>
            // coordinatesList の中のリストが List<Int> であるかをチェック
            for (coordinate in coordinatesList) {
                // 各座標が List<Int> であることを確認
                if (coordinate is List<*>) { // ${floor} のちぇすとたちの１つの座標
                    val coordinateList = coordinate.filterIsInstance<Int>()
                    if (coordinateList.size == 3) {
                        val x = coordinateList[0]
                        val y = coordinateList[1]
                        val z = coordinateList[2]
                        val location = Location(
                            gameWorld,
                            x.toDouble(),
                            y.toDouble(),
                            z.toDouble()
                        )
                        placeChest(location)
                    }
                }
            }
        }
    }
    fun place(selectFloor: Int) {
        // ワールド内のエンティティを走査
        for (entity in gameWorld!!.entities) {
            // ArmorStandかつ名前に "Floor" を含む場合に処理
            if (entity.type == EntityType.ARMOR_STAND && entity is ArmorStand) {
                if (entity.customName?.contains("Floor") == true) {
                    val name = entity.name

                    if (name.replace("Floor", "").toInt() == selectFloor) {//ちぇすとの階層
                        val location = entity.location
                        // アーマースタンドの向きを取得
                        val rotation = entity.bodyYaw
                        val blockLocation = location.clone().add(0.0, 0.0, 0.0)
                        blockLocation.block.type = Material.ENDER_CHEST

                        val block = blockLocation.block
                        val blockData = block.blockData as EnderChest

                        // エンダーチェストの向きを設定
                        //     blockData.facing = getBlockFaceFromYaw(rotation)

                        // エンダーチェストを設定
                        block.blockData = blockData
                    }

                }
            }

        }
    }
}
