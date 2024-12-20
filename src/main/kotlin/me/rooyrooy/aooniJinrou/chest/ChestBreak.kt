package me.rooyrooy.aooniJinrou.chest


import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class ChestBreak(private val config: FileConfiguration) {
    private fun breakBlock(location: Location) {
        val world = location.world
        val block = world.getBlockAt(location)
        block.type = Material.AIR
    }
    fun breakAll() {
        val floorList =
            config.getConfigurationSection("AooniJinrou.Location.${gameWorld!!.name}.Chest")!!.getKeys(false)
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
                        breakBlock(location)

                    }
                }
            }
        }
    }
}