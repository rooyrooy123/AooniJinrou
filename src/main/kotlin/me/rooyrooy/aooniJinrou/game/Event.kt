package me.rooyrooy.aooniJinrou.game


import me.rooyrooy.aooniJinrou.Items
import me.rooyrooy.aooniJinrou.gameKeyPlateSilver
import me.rooyrooy.aooniJinrou.gameStart
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

class Event : Listener{
    @EventHandler
    fun onDrop(event: PlayerDropItemEvent){
        val item = event.itemDrop.itemStack
        if (gameStart){//ゲーム開始していた場合
            if (item.type == Material.BLUE_CARPET
                || item.type == Material.OAK_BUTTON
                || item.type == Material.ARROW
                || item.type == Material.BOW
                || item.type == Material.IRON_INGOT
                || item.type == Material.GOLD_INGOT
                || item.type == Material.DIAMOND
                ) {
                // イベントをキャンセル
                event.isCancelled = true
            }
        }
    }
    @EventHandler //青鬼の杖
    fun onPlayerInteract(event: PlayerMoveEvent) {
        if (gameStart) {
            val player = event.player
            // 防具が揃っているか確認
            if (hasFullDiamondArmor(player)) {
                // 防具を1つずつ削除
                removeDiamondArmor(player)

                // ダイヤモンドを1個与える
                val aooniStick = ItemStack(Material.BLAZE_ROD)
                val aooniStickMeta = aooniStick.itemMeta
                aooniStickMeta.displayName(Component.text("§1§l§n青鬼の杖§b(右クリックで変身)"))
                val currentLore = aooniStickMeta.lore ?: mutableListOf()
                // 新しい lore を追加
                currentLore.add("§7青鬼の姿に変身！")
                currentLore.add("§c§n1回の変身につき、2人まで§7ひろしを食べることができます。")
                currentLore.add("§4§n30秒経つか、杖をもう一度右クリックすると、変身が解けます。")
                aooniStickMeta.lore = currentLore

                aooniStick.itemMeta = aooniStickMeta
                player.inventory.addItem(aooniStick)

                // プレイヤーに通知
                player.sendMessage("§1§l§n青鬼の杖を手に入れました！(右クリックで発動)")
                player.sendMessage("§c§n1回の変身につき、2人まで§7ひろしを食べることができます。")
                player.sendMessage("§4§n30秒経つか、杖をもう一度右クリックすると、変身が解けます。")
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

        }
    }
    private fun hasFullDiamondArmor(player: Player): Boolean {
        val inventory = player.inventory
        val requiredArmor = listOf(
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
        )

        // 防具が全て揃っているかを確認
        return requiredArmor.all { armorType ->
            inventory.any { item ->
                item != null && item.type == armorType
            }
        }
    }

    private fun removeDiamondArmor(player: Player) {
        val inventory = player.inventory
        val requiredArmor = listOf(
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
        )

        // 防具を1つずつ削除
        requiredArmor.forEach { armorType ->
            val itemToRemove = inventory.firstOrNull { item ->
                item != null && item.type == armorType
            }
            if (itemToRemove != null) {
                itemToRemove.amount -= 1 // スタックを減らす
                if (itemToRemove.amount <= 0) {
                    inventory.remove(itemToRemove) // スタックが0以下になったら削除
                }
            }
        }
    }

    //こっからダイヤモンドの防具を装備できないようにする
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (gameStart) {
            val player = event.player
            val item = event.item

            // nullチェック
            if (item == null || item.type == Material.AIR) return

            // ダイヤモンド防具かどうかをチェック
            if (isDiamondArmor(item.type)) {
                // イベントをキャンセルして装備を防ぐ
                event.isCancelled = true
                player.sendMessage("§cこの防具は装備できません！§1青鬼の杖でのみ、変身可能！")
            }
        }
    }
    @EventHandler
    fun noTakeOff(event: InventoryClickEvent){
        if (event.whoClicked.gameMode == GameMode.CREATIVE) return
        if (event.clickedInventory?.type == InventoryType.PLAYER){
            if (event.slotType == InventoryType.SlotType.ARMOR){
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (gameStart) {
            var clickedItem = event.cursor // プレイヤーが持っているアイテム（クリック中のアイテム）
            if (event.isShiftClick) {
                clickedItem = event.currentItem
                if (isDiamondArmor(clickedItem!!.type)) {
                    event.isCancelled = true
                    event.whoClicked.sendMessage("§cこの防具は装備できません！")
                    return
                }
            }
            // nullチェック
            if (clickedItem == null || clickedItem.type == Material.AIR) return

            // 防具スロットかどうかをチェック
            if (isArmorSlot(event)) {
                // ダイヤモンド防具を装備できないようにする
                if (isDiamondArmor(clickedItem.type)) {
                    event.isCancelled = true
                    event.whoClicked.sendMessage("§cこの防具は装備できません！")
                }
            }
        }
    }

    private fun isArmorSlot(event: InventoryClickEvent): Boolean {
        return event.slotType == InventoryType.SlotType.ARMOR
    }
    private fun isDiamondArmor(material: Material): Boolean {
        // ダイヤモンド防具の種類をチェック
        return material == Material.DIAMOND_HELMET ||
                material == Material.DIAMOND_CHESTPLATE ||
                material == Material.DIAMOND_LEGGINGS ||
                material == Material.DIAMOND_BOOTS
    }

}