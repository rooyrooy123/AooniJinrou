package me.rooyrooy.aooniJinrou.Chest

import me.rooyrooy.aooniJinrou.chestOpened
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class ChestOpen : Listener {
    @EventHandler
    fun onEnderChestOpen(event: InventoryOpenEvent){
        val inventory = event.inventory
        if (inventory.type != InventoryType.ENDER_CHEST) return
        val player = event.player
        val location = player.enderChest.location ?: return
        player.enderChest.clear()

        val blockEnderChest = location.world.getBlockAt(location)
        val blockloc = blockEnderChest.location
        //ちぇすとのいちのアーマースタンドの情報取得
        val world: World = blockloc.world
        val nearbyEntities = world.entities
        var floor = 0
        for (entity in nearbyEntities) {
            if (entity is ArmorStand && entity.location.distance(blockloc) <= 2) {
                val name = entity.name
                player.sendMessage(name)
                floor = name.replace("Floor","").toInt()//ちぇすとの階層
            }
        }
        if (chestOpened.contains("${player}.${blockloc.x}.${blockloc.y}.${blockloc.z}")){
            val chestItem = ItemStack(Material.STRUCTURE_VOID)
            val chestItemMeta = chestItem.itemMeta
            chestItemMeta.displayName(Component.text("§4§l※§cここの§e鍵の欠片§cは取得済みです"))
            chestItem.itemMeta = chestItemMeta
            player.enderChest.setItem(13, chestItem)

            //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
        }else{
            if(floor in 1..4) { //1~4階層

                val chestItem = ItemStack(Material.OAK_BUTTON)  //これは1~4階のチェストだった場合
                val chestItemMeta = chestItem.itemMeta
                chestItemMeta.displayName(Component.text("§e§l地下の鍵の欠片"))
                val currentLore = chestItemMeta.lore ?: mutableListOf()
                // 新しい lore を追加
                currentLore.add("§7本館1階～4階のチェストの鍵の欠片を")
                currentLore.add("§7すべて集めると、§9地下室の鍵§e（木の感圧版）§7に！")
                chestItemMeta.lore = currentLore
                chestItem.itemMeta = chestItemMeta
                player.enderChest.setItem(13, chestItem)
                //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
            }else if(floor == -1){
                val chestItem = ItemStack(Material.BLUE_CARPET)  //これは1~4階のチェストだった場合
                val chestItemMeta = chestItem.itemMeta
                chestItemMeta.displayName(Component.text("§9§l5階の鍵の欠片"))
                val currentLore = chestItemMeta.lore ?: mutableListOf()
                // 新しい lore を追加
                currentLore.add("§7本館地下1階のチェストの鍵の欠片を")
                currentLore.add("§7すべて集めると、§95階への鍵§e（青羊毛）§7に！")
                chestItemMeta.lore = currentLore
                chestItem.itemMeta = chestItemMeta
                player.enderChest.setItem(13, chestItem)
                //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
            }
        }



    }
    @EventHandler
    fun onEnderChestClick(event: InventoryClickEvent){

        // プレイヤーがエンダーチェストを開いているかと、エンダーチェストの中をクリックしたかの確認
        val inventory = event.inventory
        if (inventory.type != InventoryType.ENDER_CHEST) return
        // エンダーチェストの内のアイテムをクリックしたか確認
        if (event.clickedInventory!!.type != InventoryType.ENDER_CHEST) return
        val clickedItem = event.currentItem ?: return
        if (clickedItem.type == Material.AIR) return

        val clicker = event.whoClicked as? Player ?: return
        // location = enderchest
        val location = clicker.openInventory.topInventory.location ?: return
        val blockEnderChest = location.world.getBlockAt(location)
        val blockloc = blockEnderChest.location
        event.isCancelled = true
        if (clickedItem.type == Material.OAK_BUTTON){ //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
            chestOpened.add("${clicker}.${blockloc.x}.${blockloc.y}.${blockloc.z}")

        }else if(clickedItem.type == Material.DIAMOND_BOOTS
            || clickedItem.type == Material.DIAMOND_LEGGINGS
            || clickedItem.type == Material.DIAMOND_CHESTPLATE
            || clickedItem.type == Material.DIAMOND_HELMET){
            if (aooniCanGet(blockloc) == false){
                clicker.sendMessage("§c現在、このチェストにはモヤモヤが発生しているため、装備を回収できません。")
                return
            }

        }else{
            return
        }

        clicker.inventory.addItem(clickedItem)
        clicker.enderChest.clear()

        // 同じエンダーチェストを開いているすべてのプレイヤーを取得
        val players = Bukkit.getOnlinePlayers().filter { player ->
            player.openInventory.topInventory.location == location
        }

        // 条件を満たした場合、全員のエンダーチェストをクリア
        players.forEach { player ->
            player.sendMessage("アイテムを回収しました！")
        }

        blockloc.y += 1
        ChestParticle(blockloc).start()

    }
    fun aooniCanGet(location: Location) : Boolean { //チェストの状態が青鬼が装備を獲得できる状態なのか
        val world: World = location.world
        val nearbyEntities = world.entities
        // 半径内のエンティティをチェック
        for (entity in nearbyEntities) {
            if (entity is AreaEffectCloud && entity.location.distance(location) <= 2) {
                return false
            }
        }
        return true
    }
}