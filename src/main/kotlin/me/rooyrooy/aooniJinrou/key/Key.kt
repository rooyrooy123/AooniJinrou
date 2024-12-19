package me.rooyrooy.aooniJinrou.key

import me.rooyrooy.aooniJinrou.gameKeyTopNeed
import me.rooyrooy.aooniJinrou.gameKeyUnderNeed
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Key{
    fun get(player: Player){
        if (getAmount(player,Material.OAK_BUTTON) >= gameKeyUnderNeed){
            // 名前とLORE付きのきのんあつばｎ
            val key = ItemStack(Material.OAK_PRESSURE_PLATE, 1)
            val meta = key.itemMeta
            meta?.let {
                it.setDisplayName("§e§l§n地下室の鍵")
                it.lore = listOf("§7地下室解放可能時刻を過ぎると、設置可能")
                key.itemMeta = it
            }
            player.inventory.remove(Material.OAK_BUTTON)
            player.inventory.addItem(key)

        }else if (getAmount(player,Material.BLUE_CARPET) >= gameKeyTopNeed){
            val key = ItemStack(Material.BLUE_WOOL, 1)
            val meta = key.itemMeta
            meta?.let {
                it.setDisplayName("§1§l§n最上階への鍵")
                it.lore = listOf("§7最上階解放可能時刻を過ぎると、設置可能に")
                key.itemMeta = it
            }

            player.inventory.addItem(key)
            player.inventory.remove(Material.BLUE_CARPET)
        }
    }


    private fun getAmount(player: Player, material: Material) : Int{
        var amount = 0
        for (item in player.inventory.contents){
            if (item?.type == material){
                amount ++
            }
        }
        return amount
    }
}