package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.gameChestFloor
import me.rooyrooy.aooniJinrou.gameWorld
import org.bukkit.Location
import org.bukkit.Material

class Chest {

    private fun placeChest(location: Location, floor: String){
        placeChestBlock(location) // チェストを設置
        registerChestData(location, floor) // データを登録
    }
    private fun breakChest(location: Location){
        breakChestBlock(location)
    }
    // チェストの設置を担当
    private fun placeChestBlock(location: Location) {
        val world = location.world ?: return
        val block = world.getBlockAt(location)
        block.type = Material.ENDER_CHEST
    }
    private fun breakChestBlock(location: Location){
        val world = location.world ?: return
        val block = world.getBlockAt(location)
        block.type = Material.AIR
    }
    // データの登録を担当
    private fun registerChestData(location: Location, floor: String) {
        val blockState = location.world?.getBlockAt(location)?.state ?: return
        gameChestFloor[blockState] = floor
    }

    fun breakALL(chestLocations: Map<String, List<List<Int>>>) {
        chestLocations.forEach { (_, coordinatesList) ->
            coordinatesList.forEach { coordinate ->
                val location = Location(
                    gameWorld,
                    coordinate[0].toDouble(),
                    coordinate[1].toDouble(),
                    coordinate[2].toDouble()
                )
                breakChest(location)
            }
        }
    }
    fun placeAll(chestLocations: Map<String, List<List<Int>>>) {
        chestLocations.forEach { (floor, coordinatesList) ->
            coordinatesList.forEach { coordinate ->
                val location = Location(
                    gameWorld,
                    coordinate[0].toDouble(),
                    coordinate[1].toDouble(),
                    coordinate[2].toDouble()
                )
                placeChest(location, floor)
            }
        }
    }
/*
    fun place(chestLocations: Map<String, List<List<Int>>>, selectFloor: Int) {
        val floorKey = "Floor$selectFloor"
        val coordinatesList = chestLocations[floorKey]
        coordinatesList?.forEach { coordinate ->
            val location = Location(
                gameWorld,
                coordinate[0].toDouble(),
                coordinate[1].toDouble(),
                coordinate[2].toDouble()
            )
            placeChest(location, floorKey)
        }
    }
    fun all(){

    }*/
}