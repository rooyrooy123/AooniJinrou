package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.gameJobList
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class FoodLevel(val player:Player) : Listener{
    fun aooni(){
        player.foodLevel = 20
    }
    fun notAooni(){
        if (gameJobList[player] == "HUNTER"){
            if (!hasArrow(player)){
                player.foodLevel = 1
                return
            }
            player.foodLevel = 20
            return
        }
        player.foodLevel = 1
    }
    fun waitGame(){
        player.foodLevel = 20
    }
    private fun hasArrow(player: Player): Boolean { //矢をもってない(発射後のクールタイム時にだっしゅふか)
        val inventory = player.inventory
        for (item in inventory.contents) {
            if (item != null && item.type == Material.ARROW) {
                return true // 矢have
            }
        }
        return false // 矢not have
    }
}