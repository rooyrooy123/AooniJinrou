package me.rooyrooy.aooniJinrou.key

import me.rooyrooy.aooniJinrou.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack


class Key : Listener{
    @EventHandler//青鬼の館の鍵
    fun aooniKey(event: PlayerMoveEvent){
        if (!gameStart) return
        val player = event.player
        val silverKey = Items.KEY_SILVER.toItemStack()
        val goldKey = Items.KEY_GOLD.toItemStack()
        if (player.inventory.containsAtLeast(silverKey,1)) {
            if (player.inventory.containsAtLeast(goldKey, 1)) {
                player.inventory.remove(Items.KEY_GOLD.toItemStack())
                player.inventory.remove(Items.KEY_SILVER.toItemStack())
                player.inventory.addItem(Items.KEY_AOONI.toItemStack())
                player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP,1.0f,2.0f)
                player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP,1.0f,2.0f)
                player.sendMessage("§1§l§n青鬼の鍵§eの合成に成功しました！")
                player.sendMessage("§b残り時間120秒から脱出が可能になります！")
            }
        }

    }
    @EventHandler //鍵取得プレート
    fun onPlateKey(event: PlayerInteractEvent) {
        val player = event.player
        // 圧力板を踏んだかどうかを確認
        if (event.clickedBlock?.type == Material.STONE_PRESSURE_PLATE ||
            event.clickedBlock?.type == Material.LIGHT_WEIGHTED_PRESSURE_PLATE ||
            event.clickedBlock?.type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE
        ) {
            if (event.clickedBlock?.location != gameKeyPlateSilver) return
            if (player.inventory.contents.filterNotNull().filter { it.type == Material.IRON_INGOT }.sumOf { it.amount } > 0) return
            player.inventory.addItem(Items.KEY_SILVER.toItemStack())
            player.sendMessage("§7§l§n銀の鍵を獲得しました！§e脱出には§7§l§n銀の鍵§eと§6§l§n金の鍵§eを合成して、§1§l§n青鬼の鍵§eを作る必要があります。")
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP,1.0f,2.0f)
            player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP,1.0f,2.0f)

        }
    }
    fun get(player: Player){
        if ( player.inventory.containsAtLeast(Items.KEY_UNDERFLOOR_PARTS.toItemStack(), gameKeyUnderNeed)){
            val consoleSender: ConsoleCommandSender = Bukkit.getServer().consoleSender
            Bukkit.getServer().dispatchCommand(consoleSender, "give ${player.name} oak_pressure_plate{CanPlaceOn:[\"minecraft:obsidian\"],display:{Name:'{\"text\":\"地下室の鍵\",\"color\":\"yellow\",\"bold\":true,\"italic\":false,\"underlined\":true}',Lore:['{\"text\":\"地下室解放可能時刻を過ぎると、設置可能に\",\"color\":\"gray\",\"italic\":false}']}} 1")

            player.inventory.remove(Material.OAK_BUTTON)
            player.sendMessage("§e§l§n地下室への鍵を獲得しました！")
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP,1.0f,2.0f)

        }
        if (player.inventory.containsAtLeast(Items.KEY_TOPFLOOR_PARTS.toItemStack(), gameKeyTopNeed)){

            val consoleSender: ConsoleCommandSender = Bukkit.getServer().consoleSender
            Bukkit.getServer().dispatchCommand(consoleSender, "give ${player.name} blue_wool{CanPlaceOn:[\"minecraft:gold_block\"],display:{Name:'{\"text\":\"最上階への鍵\",\"color\":\"blue\",\"bold\":true,\"italic\":false,\"underlined\":true}',Lore:['{\"text\":\"最上階解放可能時刻を過ぎると、設置可能に\",\"color\":\"gray\",\"italic\":false}']}} 1")


            player.sendMessage("§e§l§n最上階への鍵を獲得しました！")
            //player.inventory.addItem(key)
            player.inventory.remove(Material.BLUE_CARPET)
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP,1.0f,2.0f)
        }
    }


}