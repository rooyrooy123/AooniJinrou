package me.rooyrooy.aooniJinrou.game


import me.rooyrooy.aooniJinrou.*
import me.rooyrooy.aooniJinrou.chest.Chest
import me.rooyrooy.aooniJinrou.job.hunter.Bow
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

class Event : Listener{
    @EventHandler
    fun onPlatePlace(event:BlockPlaceEvent){
        val player = event.player
        val block = event.block
        val locationUnder = block.location
        locationUnder.y -= 1
        if (locationUnder == gameGateUnderFloor){
            Bukkit.broadcastMessage("§e§l${player.name}§bが地下室を開放しました！")
            Bukkit.getOnlinePlayers().forEach { it ->
                it.playSound(
                    it.location,
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1.0f,
                    1.0f
                )
            }
        }else if (locationUnder == gameGateTopFloor){
            Bukkit.broadcastMessage("§e§l${player.name}§bが最上階を開放しました！")
            Bukkit.getOnlinePlayers().forEach {
                it.playSound(
                    it.location,
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1.0f,
                    1.0f
                )
            }
            Chest().registerGoldKey()
        }
    }
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity
        if (projectile is Arrow) {
            // ブロックに刺さったかどうかを確認
            if (event.hitBlock != null) {
                projectile.remove()
            }
        }
    }
    @EventHandler
    fun onShoot(event:EntityShootBowEvent) {
        val shooter = event.entity as Player
        val bow = shooter.inventory.itemInMainHand
        val arrow = event.projectile as Arrow
        Bow(shooter).hunterCoolTime(bow,arrow)

    }

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
    @EventHandler
    fun foodChange(event:PlayerMoveEvent){
        val player = event.player
        if (!gameStart){
            FoodLevel(player).waitGame()
            return
        }
        if (gameJobList[player] == "AOONI"){
            FoodLevel(player).aooni()
        }else{
            FoodLevel(player).notAooni()
        }
    }

    private fun hasKey(key:ItemStack){

    }
    @EventHandler //青鬼の杖
    fun aooniStickGet(event: PlayerMoveEvent) {
        if (gameStart) {
            val player = event.player
            // 防具が揃っているか確認
            if (hasFullDiamondArmor(player)) {
                // 防具を1つずつ削除
                removeDiamondArmor(player)
                val aooniStick = ItemStack(Material.BLAZE_ROD)
                val aooniStickMeta = aooniStick.itemMeta
                aooniStickMeta.displayName(Component.text("§1§l§n青鬼の杖§b(右クリックで変身)"))
                val currentLore = aooniStickMeta.lore ?: mutableListOf()
                currentLore.add("§7青鬼の姿に変身！")
                currentLore.add("§c§n1回の変身につき、2人まで§7ひろしを食べることができます。")
                currentLore.add("§4§n30秒経つか、杖をもう一度右クリックすると、変身が解けます。")
                aooniStickMeta.lore = currentLore

                aooniStick.itemMeta = aooniStickMeta
                player.inventory.addItem(aooniStick)
                player.sendMessage("§1§l§n青鬼の杖を手に入れました！(右クリックで発動)")
                player.sendMessage("§c§n1回の変身につき、2人まで§7ひろしを食べることができます。")
                player.sendMessage("§4§n30秒経つか、杖をもう一度右クリックすると、変身が解けます。")
            }
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