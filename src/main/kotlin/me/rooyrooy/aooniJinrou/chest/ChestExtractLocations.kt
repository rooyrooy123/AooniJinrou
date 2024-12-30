package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.configuration.file.FileConfiguration
class ChestExtractLocations {
    fun getLocations(config: FileConfiguration): Map<String, List<List<Int>>> {
        val chestLocations = mutableMapOf<String, List<List<Int>>>()
        val chestSection = config.getConfigurationSection("AooniJinrou.Location.${gameWorld!!.name}.Chest")
        chestSection?.getKeys(false)?.forEach { floor ->
            val coordinatesList = config.getList("AooniJinrou.Location.${gameWorld!!.name}.Chest.$floor") as List<*>
            val parsedCoordinates = coordinatesList
                .filterIsInstance<List<*>>()
                .map { it.filterIsInstance<Int>() }
                .filter { it.size == 3 }
            chestLocations[floor] = parsedCoordinates
        }
        return chestLocations
    }
}