package me.rooyrooy.aooniJinrou.key

import me.rooyrooy.aooniJinrou.Items
import me.rooyrooy.aooniJinrou.gameKeyTopNeed
import me.rooyrooy.aooniJinrou.gameKeyUnderNeed
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player


class Key{
    fun get(player: Player){
        if (getAmount(player,Material.OAK_BUTTON) >= gameKeyUnderNeed){
            val consoleSender: ConsoleCommandSender = Bukkit.getServer().consoleSender
            Bukkit.getServer().dispatchCommand(consoleSender, "give ${player.name} oak_pressure_plate{CanPlaceOn:[\"minecraft:obsidian\"],display:{Name:'{\"text\":\"地下室の鍵\",\"color\":\"yellow\",\"bold\":true,\"italic\":false,\"underlined\":true}',Lore:['{\"text\":\"地下室解放可能時刻を過ぎると、設置可能に\",\"color\":\"gray\",\"italic\":false}']}} 1")

            player.inventory.remove(Material.OAK_BUTTON)
            player.sendMessage("§e§l§n地下室への鍵を獲得しました！")
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1.0f,2.0f)

        }
        if (getAmount(player,Material.BLUE_CARPET) >= gameKeyTopNeed){

            val consoleSender: ConsoleCommandSender = Bukkit.getServer().consoleSender
            Bukkit.getServer().dispatchCommand(consoleSender, "give ${player.name} blue_wool{CanPlaceOn:[\"minecraft:gold_block\"],display:{Name:'{\"text\":\"最上階への鍵\",\"color\":\"dark_blue\",\"bold\":true,\"italic\":false,\"underlined\":true}',Lore:['{\"text\":\"最上階解放可能時刻を過ぎると、設置可能に\",\"color\":\"gray\",\"italic\":false}']}} 1")


            player.sendMessage("§e§l§n最上階への鍵を獲得しました！")
            //player.inventory.addItem(key)
            player.inventory.remove(Material.BLUE_CARPET)
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1.0f,2.0f)
        }
    }
    private fun getAmount(player: Player, material: Material) : Int{
        var amount = 0
        for (item in player.inventory.contents){
            if (item?.type == material){
                amount ++
            }
        }
        return  amount
    }

}