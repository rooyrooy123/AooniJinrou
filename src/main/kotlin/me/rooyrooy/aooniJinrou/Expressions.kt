package me.rooyrooy.aooniJinrou

import com.comphenix.protocol.PacketType.Play
import me.rooyrooy.aooniJinrou.chest.ChestExtractLocations
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class Expressions (){
    private val config = AooniJinrou.instance.config

    fun getChestLocations() : Map<String, List<List<Int>>>{
        val chestLocations: Map<String, List<List<Int>>> = ChestExtractLocations().getLocations(config)
        return chestLocations
    }
    fun getSpawnLocationFromConfig(): Location {
        // 設定ファイルからデータ取得
        val worldName = config.getString("AooniJinrou.Location.lobby.world")
        val x = config.getDouble("AooniJinrou.Location.lobby.x")
        val y = config.getDouble("AooniJinrou.Location.lobby.y")
        val z = config.getDouble("AooniJinrou.Location.lobby.z")
        val yaw = config.getDouble("AooniJinrou.Location.lobby.yaw").toFloat()
        val pitch = config.getDouble("AooniJinrou.Location.lobby.pitch").toFloat()

        // ワールドを取得
        val world = worldName?.let { Bukkit.getWorld(it) }

        // Location を作成して返す
        return Location(world, x, y, z, yaw, pitch)
    }
    fun getLocationFromCoordinate(path: String): Location {
        val coordinateList = config.getList(path)
        val doubleList: List<Double>? = coordinateList?.mapNotNull {
            when (it) {
                is Number -> it.toDouble()
                is String -> it.toDoubleOrNull()
                else -> null
            }
        }
        if (doubleList != null && doubleList.size >= 3) {
            val location = Location(gameWorld, doubleList[0], doubleList[1], doubleList[2])
            return location
        }else{
            return Location(gameWorld, 0.0, 64.0, 0.0)
        }

    }
    fun teleportWithPassenger(player: Player,location:Location) {
        val passengers = player.passengers
        for (passenger in passengers) {
            passenger.eject()
            passenger.teleport(location)
        }
        player.teleport(location)
        for (passenger in passengers) {
            player.addPassenger(passenger)
        }




    }
    fun getJoinedPlayers() : MutableList<Player>{
        val joinPlayers: MutableList<Player> = gameJoinGame.filter { it.value }.keys.toList().toMutableList()
        return joinPlayers
    }
    fun getSurvivingPlayers() : MutableList<Player>{
        val joinPlayers = getJoinedPlayers()
        joinPlayers.filter {
            it.gameMode != GameMode.SPECTATOR
        }
        return  joinPlayers

    }
    fun getDeadPlayers() : MutableList<Player>{
        val joinPlayers = getJoinedPlayers()
        joinPlayers.filter {
            it.gameMode == GameMode.SPECTATOR
        }
        return joinPlayers
    }
    fun getSpectatePlayers() : MutableList<Player>{
        val allPlayers = Bukkit.getOnlinePlayers() as MutableList<Player>
        allPlayers.filter {
            it.gameMode == GameMode.SPECTATOR
        }
        return allPlayers

    }
}