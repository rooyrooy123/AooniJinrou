package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.gameChestFloor
import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class ChestPlace(
    private val config: FileConfiguration) {
    private fun placeChest(location: Location,floor: String){
        val world = location.world
        val block = world.getBlockAt(location)
        block.type = Material.ENDER_CHEST
        gameChestFloor[block.state] = floor
    }
    fun placeAll() {
        val floorList = config.getConfigurationSection("AooniJinrou.Location.${gameWorld!!.name}.Chest")!!.getKeys(false)
        for (floor in floorList) {
            val coordinatesList = config.getList("AooniJinrou.Location.${gameWorld!!.name}.Chest.${floor}") as List<*>
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
                        placeChest(location,floor)
                    }
                }
            }
        }
    }
    fun place(selectFloor: Int) {
        val coordinatesList = config.getList("AooniJinrou.Location.${gameWorld!!.name}.Chest.Floor${selectFloor}") as List<*>
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
                    placeChest(location, "Floor${selectFloor}")
                }
            }
        }
    }
}
