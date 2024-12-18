package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.TaskHandler
import me.rooyrooy.aooniJinrou.gameChestEquipment
import me.rooyrooy.aooniJinrou.gameChestOpened
import me.rooyrooy.aooniJinrou.gameJobList
import me.rooyrooy.aooniJinrou.key.Key
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

class ChestEvent : Listener {
    private fun enderChestOpen(player: Player){
        val location = player.enderChest.location ?: return
        player.enderChest.clear()

        val blockEnderChest = location.world.getBlockAt(location)
        val blockLoc = blockEnderChest.location
        //ちぇすとのいちのアーマースタンドの情報取得
        val world: World = blockLoc.world
        val nearbyEntities = world.entities
        var floor = 0
        for (entity in nearbyEntities) if (entity is ArmorStand && entity.location.distance(blockLoc) <= 2) {
            val name = entity.name
            floor = name.replace("Floor","").toInt()//ちぇすとの階層
        }
        if (gameChestOpened.contains("${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")){
            val chestItem = ItemStack(Material.STRUCTURE_VOID)
            val chestItemMeta = chestItem.itemMeta
            chestItemMeta.displayName(Component.text("§4§l※§cここの§e鍵の欠片§cは取得済みです"))
            chestItem.itemMeta = chestItemMeta
            player.enderChest.setItem(13, chestItem)

            //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
        }else{
            if(gameJobList[player] != "aooni"){
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
                }else if(floor == -1) {
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
            }else{
                if (aooniCanGet(blockLoc)) {
                    if (!gameChestEquipment.containsKey("${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")){
                        val equipments = arrayOf(
                            Material.DIAMOND_HELMET,
                            Material.DIAMOND_CHESTPLATE,
                            Material.DIAMOND_LEGGINGS,
                            Material.DIAMOND_BOOTS
                        )
                        gameChestEquipment["${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}"] = equipments.random()
                    }
                    val chestItem = ItemStack(gameChestEquipment["${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}"]!!)
                    val chestItemMeta = chestItem.itemMeta
                    chestItemMeta.displayName(Component.text("§9§l§n青鬼装備の欠片"))
                    val currentLore = chestItemMeta.lore ?: mutableListOf()
                    // 新しい lore を追加
                    currentLore.add("§7装備を取得すると、エンダーチェストが赤く変化します！")
                    currentLore.add("§7頭・鎧・ズボン・靴の4種類を集めると、青鬼の杖を獲得(青鬼に一定時間変身)！")
                    chestItemMeta.lore = currentLore
                    chestItem.itemMeta = chestItemMeta
                    player.enderChest.setItem(13, chestItem)
                }else{
                    val chestItem = ItemStack(Material.STRUCTURE_VOID)
                    val chestItemMeta = chestItem.itemMeta
                    chestItemMeta.displayName(Component.text("§4§l装備取得不可"))
                    val currentLore = chestItemMeta.lore ?: mutableListOf()
                    // 新しい lore を追加
                    currentLore.add("§7もやもやはある間は装備の取得不可")
                    chestItemMeta.lore = currentLore
                    chestItem.itemMeta = chestItemMeta
                    player.enderChest.setItem(13, chestItem)
                }
            }
        }
    }
    @EventHandler
    fun onEnderChestOpen(event: InventoryOpenEvent){
        val inventory = event.inventory
        if (inventory.type != InventoryType.ENDER_CHEST) return
        val player = event.player as Player
        enderChestOpen(player)



    }
    @EventHandler
    fun onEnderChestClick(event: InventoryClickEvent){

        // プレイヤーがエンダーチェストを開いているかと、エンダーチェストの中をクリックしたかの確認
        val inventory = event.inventory
        if (inventory.type != InventoryType.ENDER_CHEST) return
        event.isCancelled = true

        // エンダーチェストの内のアイテムをクリックしたか確認
        if (event.clickedInventory!!.type != InventoryType.ENDER_CHEST) return
        val clickedItem = event.currentItem ?: return
        if (clickedItem.type == Material.AIR) return

        val clicker = event.whoClicked as? Player ?: return
        // location = enderChest
        val location = clicker.openInventory.topInventory.location ?: return
        val blockEnderChest = location.world.getBlockAt(location)
        val blockLoc = blockEnderChest.location
        if (clickedItem.type == Material.OAK_BUTTON){ //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
            gameChestOpened.add("${clicker}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")

        }else if(clickedItem.type == Material.DIAMOND_BOOTS
            || clickedItem.type == Material.DIAMOND_LEGGINGS
            || clickedItem.type == Material.DIAMOND_CHESTPLATE
            || clickedItem.type == Material.DIAMOND_HELMET){
            if (!aooniCanGet(blockLoc)){ // もやもや中かどうか
                clicker.sendMessage("§c現在、このチェストにはモヤモヤが発生しているため、装備を回収できません。")
                return
            }

        }else{
            return
        }

        clicker.inventory.addItem(clickedItem)
        clicker.enderChest.clear()


        if (gameJobList[clicker] == "aooni") { //青鬼が装備とったら、もやもや発生
            blockLoc.y += 1
            ChestParticle(blockLoc).start()
        }
        // 同じエンダーチェストを開いているすべてのプレイヤーを取得
        val players = Bukkit.getOnlinePlayers().filter { player ->
            player.openInventory.topInventory.location == location
        }

        //エンダーチェスト開いてる人一覧 青鬼ならちぇすとを更新させる
        val taskHandler = TaskHandler() // aooniCanGetの処理遅れで2個取れてしまうため、1TICKずらして実行
        taskHandler.executeWithDelay({
            players.forEach { openPlayer ->
                if (gameJobList[openPlayer] == "aooni"){
                    if (gameJobList[clicker] == "aooni"){
                        openPlayer.openInventory.topInventory
                        enderChestOpen(openPlayer)
                    }
                }
            }

            enderChestOpen(clicker)
        }, 1L) // 1ティック遅らせる
        Key().get(clicker)
    }

    private fun aooniCanGet(location: Location) : Boolean { //チェストの状態が青鬼が装備を獲得できる状態なのか
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