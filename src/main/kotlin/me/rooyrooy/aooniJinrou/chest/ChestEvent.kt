package me.rooyrooy.aooniJinrou.chest

import me.rooyrooy.aooniJinrou.*
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

        val floorName = gameChestFloor[blockEnderChest.state] ?: return //Example: Floor4
        val blockLoc = blockEnderChest.location
        val floor = floorName.replace("Floor","").toInt()//ちぇすとの階層 INT

        if (gameChestOpened.contains("${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")){
            val chestItem = ItemStack(Material.STRUCTURE_VOID)
            chestItem.editMeta { meta ->
                meta.displayName(Component.text("§4§l※§cここの§e鍵の欠片§cは取得済みです"))
                meta.lore(listOf(
                    Component.text("§7未開封のチェストに鍵の欠片があります"),
                    Component.text("")
                )) // L
            }
            player.enderChest.setItem(13, chestItem)

            //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
        }else {
            if (gameJobList[player] != "aooni") {
                if (floor in 1..4) { //1~4階層

                    val chestItem = ItemStack(Material.OAK_BUTTON)  //これは1~4階のチェストだった場合
                    chestItem.editMeta { meta ->
                        meta.displayName(Component.text("§e§l地下の鍵の欠片")) // 名前を設定
                        meta.lore(listOf(
                            Component.text("§7本館1階～4階のチェストの鍵の欠片を"),
                            Component.text("§7すべて集めると、§9地下室の鍵§e（木の感圧版）§7に！"))
                        )
                    }
                    player.enderChest.setItem(13, chestItem)
                    //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
                } else if (floor == -1) {
                    val chestItem = ItemStack(Material.BLUE_CARPET)  //これは1~4階のチェストだった場合
                    chestItem.editMeta { meta ->
                        meta.displayName(Component.text("§9§l最上階の鍵の欠片")) // 名前を設定
                        meta.lore(listOf(
                            Component.text("§7本館地下1階のチェストの鍵の欠片を"),
                            Component.text("§7すべて集めると、§95階への鍵§e（青羊毛）§7に！")
                        ))
                    }
                    player.enderChest.setItem(13, chestItem)
                    //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
                }
            } else {
                if (aooniCanGet(blockLoc)) {
                    if (!gameChestEquipment.containsKey("${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}")) {
                        val equipments = arrayOf(
                            Material.DIAMOND_HELMET,
                            Material.DIAMOND_CHESTPLATE,
                            Material.DIAMOND_LEGGINGS,
                            Material.DIAMOND_BOOTS
                        )
                        gameChestEquipment["${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}"] = equipments.random()
                    }
                    val chestItem =
                        ItemStack(gameChestEquipment["${player}.${blockLoc.x}.${blockLoc.y}.${blockLoc.z}"]!!)
                    chestItem.editMeta { meta ->
                        meta.displayName(Component.text("§9§l§n青鬼装備の欠片")) // 名前を設定
                        meta.lore(listOf(
                            Component.text("§7装備を取得すると、エンダーチェストが赤く変化します！"),
                            Component.text("§7頭・鎧・ズボン・靴の4種類を集めると、青鬼の杖を獲得(青鬼に一定時間変身)！")
                        ))
                    }
                    player.enderChest.setItem(13, chestItem)
                } else {
                    val chestItem = ItemStack(Material.STRUCTURE_VOID)
                    chestItem.editMeta { meta ->
                        meta.displayName(Component.text("§4§l装備取得不可")) // 名前を設定
                        meta.lore(listOf(
                            Component.text("§7もやもやはある間は装備の取得不可")
                        ))
                    }
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
        if (clickedItem.type == Material.OAK_BUTTON
            || clickedItem.type == Material.BLUE_CARPET){ //ここで青鬼だった場合とそれ以外だった時の条件分岐をいれよう
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
            ChestParticle(blockLoc).startTimer()
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
        if (gameJobList[clicker] != "aooni"){
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