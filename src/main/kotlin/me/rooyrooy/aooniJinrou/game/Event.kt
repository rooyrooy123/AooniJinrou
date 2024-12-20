package me.rooyrooy.aooniJinrou.game


import me.rooyrooy.aooniJinrou.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Event : Listener{
    @EventHandler
    fun onDrop(event: PlayerDropItemEvent){
        val item = event.itemDrop.itemStack
        if (gameStart){//ゲーム開始していた場合
            if (item.type == Material.BLUE_CARPET
                || item.type == Material.OAK_BUTTON
                || item.type == Material.ARROW
                || item.type == Material.BOW
                ) {
                // イベントをキャンセル
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent){
        val damager = event.damager
        val victim = event.entity

        if (gameStart){
            if (victim is Player) {
                if (gameJobList[victim] != "hunter") {
                    if (damager is Arrow) {
                        val attacker = damager.shooter
                        if (attacker is Player) { // attacker victimが人間かつ弓で攻撃された場合
                            if (gameJobList[attacker] == "hunter") { //狩人かの確認
                                victim.health = 0.0
                                return
                            }
                        }
                    } else if (damager is Player) {
                        if (gameJobList[damager] == "aooni") { // 青鬼の攻撃だった場合
                            if (damager.equipment.helmet.type != Material.AIR) {
                                victim.health = 0.0
                                return
                            }
                        }
                    }
                }
            }//上記の条件を満たさないダメージは無効
            event.isCancelled = true
            return
        }
    }
    @EventHandler
    fun onDeath(event: PlayerDeathEvent){
        val victim = event.entity
        val attacker = victim.killer
        if (attacker is Player){
            event.isCancelled = true
            victim.gameMode = GameMode.SPECTATOR
            val attackerJob = gameJobList[attacker] ?:return
            val victimJob = gameJobList[victim] ?: return
            if (gameJobList[attacker] == "hunter"){ //ハンターが殺した
                Bukkit.broadcastMessage(
                    "${JobInfo.valueOf(victimJob).displayName}：${victim.name}§bは${JobInfo.valueOf(attackerJob).displayName}：${attacker.name}§bに射抜かれた。")
                if (gameJobList[victim] == "hiroshi"){
                    val players = Bukkit.getOnlinePlayers()
                    Bukkit.broadcastMessage("§2狩人§bが§dひろし§bを誤射したので、§4青鬼以外に盲目を付与")
                    for (loopPlayer in players){
                        if (loopPlayer.gameMode != GameMode.SPECTATOR){
                            if (loopPlayer.world == gameWorld){
                                if (gameJobList[loopPlayer] != "aooni") {
                                    // Blindnessを200TICK付与
                                    val blindnessEffect = PotionEffect(PotionEffectType.BLINDNESS, 200, 0)
                                    blindnessEffect.withParticles(false) // パーティクルを非表示にする
                                    // プレイヤーに効果を付与
                                    loopPlayer.addPotionEffect(blindnessEffect)
                                }

                            }
                        }
                    }
                }
            }else if (gameJobList[attacker] == "aooni"){ //青鬼が殺した
                Bukkit.broadcastMessage(
                    "§e「§c${victim.name}§e」§bは§9§l青鬼§bに食べられた。")


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
                val aooniStick = ItemStack(Material.DIAMOND)
                val aooniStickMeta = aooniStick.itemMeta
                aooniStickMeta.displayName(Component.text("§1§l§n青鬼の杖§b(右クリックで変身)"))
                val currentLore = aooniStickMeta.lore ?: mutableListOf()
                // 新しい lore を追加
                currentLore.add("§7青鬼の姿に変身！")
                currentLore.add("§c§n1回の変身につき、3人まで§7ひろしを食べることができます。")
                currentLore.add("§4§n30秒経つか、杖をもう一度右クリックすると、変身が解けます。")
                aooniStickMeta.lore = currentLore

                aooniStick.itemMeta = aooniStickMeta
                player.inventory.addItem(aooniStick)

                // プレイヤーに通知
                player.sendMessage("§1§l§n青鬼の杖を手に入れました！(右クリックで発動)")
                player.sendMessage("§c§n1回の変身につき、3人まで§7ひろしを食べることができます。")
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
        // プレイヤーの装備スロット（ヘルメット、チェストプレート、レギンス、ブーツ）
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