package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.*
import me.rooyrooy.aooniJinrou.PluginProvider.plugin
import me.rooyrooy.aooniJinrou.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.AreaEffectCloud
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

        //val floorName = gameChestFloor[blockEnderChest.state] ?: return //Example: Floor4
        val blockLoc = blockEnderChest.location
        //val floor = floorName.replace("Floor","").toInt()//ちぇすとの階層 INT


        val floor = BlockMetaData(plugin).getMetadata(blockEnderChest,"Floor")?.toInt()
        val playerIDGoldKey =BlockMetaData(plugin).getMetadata(blockEnderChest,"${player.uniqueId}")
        val chestID = BlockMetaData(plugin).getMetadata(blockEnderChest,"ID")

        if (gamePlayerChestOpened.contains("${player.uniqueId}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")){
            val chestItem = Items.KEY_WARNING_ALREADY.toItemStack()
            player.enderChest.setItem(13, chestItem)
            //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
        }else {
            if (gameJobList[player] != "AOONI") {
                if (floor != null) {
                    if (floor >= 1) { //1~4階層

                        val chestItem = Items.KEY_UNDERFLOOR_PARTS.toItemStack()  //これは1~4階のチェストだった場合
                        player.enderChest.setItem(13, chestItem)
                        //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
                    } else if (floor <= -1) {
                        val chestItem = Items.KEY_TOPFLOOR_PARTS.toItemStack()  //これは地下1階のチェストだった場合
                        player.enderChest.setItem(13, chestItem)
                        //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
                    }
                }
            } else {
                if (aooniCanGet(blockLoc)) {
                    if (!gameChestEquipment.containsKey("${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")) { //ランダム装備を選ぶ
                        val equipments = arrayOf(
                            Material.DIAMOND_HELMET,
                            Material.DIAMOND_CHESTPLATE,
                            Material.DIAMOND_LEGGINGS,
                            Material.DIAMOND_BOOTS
                        )
                        gameChestEquipment["${blockLoc.x}.${blockLoc.y}.${blockLoc.z}"] = equipments.random()
                    }
                    val chestItem =
                        ItemStack(gameChestEquipment["${blockLoc.x}.${blockLoc.y}.${blockLoc.z}"]!!)
                    chestItem.editMeta { meta ->
                        meta.displayName(Component.text("§9§l§n青鬼装備の欠片")) // 名前を設定
                        meta.lore(listOf(
                            Component.text("§7装備を取得すると、チェストが赤く変化！"),
                            Component.text("§7頭・鎧・ズボン・靴の4種類を集めると、"),
                            Component.text(("青鬼に一定時間変身！"))
                        ))
                    }
                    player.enderChest.setItem(13, chestItem)
                } else { //もやもやAreaEffectCloudがあるとき、アイテム取得不可
                    val chestItem = Items.EQUIPMENT_WARNING.toItemStack()
                    player.enderChest.setItem(13, chestItem)
                }
            }
        }
        if (gameJobList[player] != "AOONI"){
            if (playerIDGoldKey == chestID) {
                player.enderChest.setItem(13, Items.KEY_GOLD.toItemStack())
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
        if (clickedItem.type == Material.GOLD_INGOT){
            clicker.playSound(clicker.location, Sound.ENTITY_ITEM_PICKUP,1.0f,1.0f)
            clicker.playSound(clicker.location, Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f)
            clicker.sendMessage("§6§l§n金の鍵§bを獲得した！§7§l§n銀の鍵§bを持つと、§1l館の鍵§bを獲得できます！")
            clicker.inventory.remove(Material.GOLD_INGOT)
            clicker.inventory.addItem(clickedItem)
        }
        if (clickedItem.type == Material.OAK_BUTTON
            || clickedItem.type == Material.BLUE_CARPET){ //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
            gamePlayerChestOpened.add("${clicker.uniqueId}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")

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
        clicker.playSound(clicker.location,Sound.ENTITY_ITEM_PICKUP,1.0f,0.5f)
        clicker.enderChest.clear()
        if (gameJobList[clicker] == "AOONI") { //青鬼が装備とったら、もやもや発生
            blockLoc.y += 1
            ChestParticle().startTimer(blockLoc)
        }
        // 同じエンダーチェストを開いているすべてのプレイヤーを取得
        val players = Bukkit.getOnlinePlayers().filter { player ->
            player.openInventory.topInventory.location == location
        }

        //エンダーチェスト開いてる人一覧 青鬼ならちぇすとを更新させる
        val taskHandler = Delay() // aooniCanGetの処理遅れで2個取れてしまうため、1TICKずらして実行
        taskHandler.executeWithDelay({
            players.forEach { openPlayer ->
                if (gameJobList[openPlayer] == "AOONI"){
                    if (gameJobList[clicker] == "AOONI"){
                        openPlayer.openInventory.topInventory
                        enderChestOpen(openPlayer)
                    }
                }
            }
            enderChestOpen(clicker)
        }, 1L) // 1ティック遅らせる
        if (gameJobList[clicker] != "AOONI"){
            Key().get(clicker)
        }
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
