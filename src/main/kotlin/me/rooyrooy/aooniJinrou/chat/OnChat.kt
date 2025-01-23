package me.rooyrooy.aooniJinrou.chat


import me.rooyrooy.aooniJinrou.gameStart
import me.rooyrooy.aooniJinrou.gameWorld
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
import net.minecraft.network.syncher.DataWatcher
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EnumItemSlot
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.abs

class OnChat () : Listener {
    private val radius = 6.5 //　チャット半径

    private fun killEntitiesWithTagsOutOfRadius(player: Player,excludedPlayers: List<Player>) {
       excludedPlayers.forEach { loopPlayer ->

            loopPlayer.passengers.forEach { entity ->
                if (entity.scoreboardTags.containsAll(listOf("AooniJinrou","ChatArmorStand", "CanSee:${player.name}", "Ride:${loopPlayer.name}"))) {
                    entity.remove()
                }
            }
        }
    }
    private fun chatRadiusPlayers(player: Player): List<org.bukkit.entity.Entity> {
        val nearbyEntities = player.world.getNearbyEntities(player.location, radius, radius, radius)
        val nearbyPlayers = nearbyEntities.filter { it is Player && it != player && abs(it.location.y - player.location.y) <= 4.0 && it.gameMode == GameMode.ADVENTURE}

        return nearbyPlayers
    }
    @EventHandler
    fun onQuitKillChatStand(event:PlayerQuitEvent){


        for (entity in gameWorld?.entities!!) {
            if (entity.type == EntityType.ARMOR_STAND) {
                val tags = entity.scoreboardTags
                val hasChatTags = tags.containsAll(
                    listOf(
                        "AooniJinrou",
                        "ChatArmorStand",
                        "Ride:${event.player.name}"
                    )
                )
                if (hasChatTags) {
                    entity.remove()
                }
            }
        }
    }
    @EventHandler
    fun onChat(event: PlayerChatEvent){
        val player = event.player
        val message = event.message
        if (!gameStart) return

    }
    @EventHandler
    fun onChatCheck(event: PlayerMoveEvent) {
        if (!gameStart) return

        val player = event.player
        player.location
        val allPlayers = Bukkit.getOnlinePlayers()
        val nearbyPlayers = chatRadiusPlayers(player)
        val excludedPlayers = allPlayers.filterNot { nearbyPlayers.contains(it) }
        killEntitiesWithTagsOutOfRadius(player,excludedPlayers)
        if (player.gameMode != GameMode.ADVENTURE) return
        for (loopPlayer in nearbyPlayers) {
            var summonEntity = false
            val passengers = loopPlayer.passengers
            if (passengers.isNotEmpty()) { //passengerがいるかどうか
                passengers.forEach {
                    val tags = it.scoreboardTags
                    val hasChatTags = tags.containsAll(listOf("AooniJinrou","ChatArmorStand", "CanSee:${player.name}", "Ride:${loopPlayer.name}"))
                    if (!hasChatTags) { // 上記のTAGをもっていないなら、新規で召喚
                        summonEntity = true
                    }
                }
            }else{ //passengerがいないなら新規召喚
                summonEntity = true
            }
            if (summonEntity){
                val armorStandEntity = loopPlayer.world.spawn(Location(gameWorld,0.0,0.0,0.0), ArmorStand::class.java)
                val skullItem = ItemStack(org.bukkit.Material.PLAYER_HEAD)
                val meta = skullItem.itemMeta as? org.bukkit.inventory.meta.SkullMeta
                meta?.owningPlayer = Bukkit.getOfflinePlayer(loopPlayer.name) // オフラインプレイヤーでも動作
                skullItem.itemMeta = meta
                displayArmorStand(player, armorStandEntity, loopPlayer.location, "§e${loopPlayer.name}§f[§b§lチャット可能§f]") {
                    armorStandEntity.equipment.helmet = skullItem
                    armorStandEntity.isGlowing = true
                    armorStandEntity.isMarker = true
                    armorStandEntity.addScoreboardTag("AooniJinrou")
                    armorStandEntity.addScoreboardTag("ChatArmorStand")
                    armorStandEntity.addScoreboardTag("CanSee:${player.name}")
                    armorStandEntity.addScoreboardTag("Ride:${loopPlayer.name}")
                    loopPlayer.addPassenger(armorStandEntity)
                }
            }
        }
    }
}



private fun displayArmorStand(p: Player, armorStandEntity: ArmorStand, loc: Location, msg: String, func: (ArmorStand) -> Unit) {
    // アーマースタンドの設定
    armorStandEntity.customName = msg
    armorStandEntity.isCustomNameVisible = true
    armorStandEntity.isInvisible = true
    armorStandEntity.isMarker = false
    armorStandEntity.setGravity(false)
    armorStandEntity.isSmall = true
    armorStandEntity.setBasePlate(false)
    armorStandEntity.teleport(loc.add(0.0, 0.0, 0.0))

    // エンティティIDの取得
    val id = armorStandEntity.getEntityId()

    // NMSエンティティを取得
    val nmsEntity = (armorStandEntity as CraftArmorStand).handle

    // パケットの作成
    val spawnPacket = PacketPlayOutSpawnEntity(nmsEntity)
    val dataWatcher = Entity::class.java.getDeclaredField("am").apply { isAccessible = true }.get(nmsEntity) as DataWatcher
    val metadataPacket = PacketPlayOutEntityMetadata(id, dataWatcher.c())

    // 装備の設定（必要な場合）
    val equipmentList = ArrayList<com.mojang.datafixers.util.Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>()
    equipmentList.add(com.mojang.datafixers.util.Pair(EnumItemSlot.a, CraftItemStack.asNMSCopy(armorStandEntity.equipment.itemInMainHand)))
    equipmentList.add(com.mojang.datafixers.util.Pair(EnumItemSlot.b, CraftItemStack.asNMSCopy(armorStandEntity.equipment.itemInOffHand)))
    equipmentList.add(com.mojang.datafixers.util.Pair(EnumItemSlot.f, CraftItemStack.asNMSCopy(armorStandEntity.equipment.helmet)))
    equipmentList.add(com.mojang.datafixers.util.Pair(EnumItemSlot.e, CraftItemStack.asNMSCopy(armorStandEntity.equipment.chestplate)))
    equipmentList.add(com.mojang.datafixers.util.Pair(EnumItemSlot.d, CraftItemStack.asNMSCopy(armorStandEntity.equipment.leggings)))
    equipmentList.add(com.mojang.datafixers.util.Pair(EnumItemSlot.c, CraftItemStack.asNMSCopy(armorStandEntity.equipment.boots)))

    val equipmentPacket = PacketPlayOutEntityEquipment(id, equipmentList)

    // アーマースタンドを全プレイヤーに送信しないようにする
    Bukkit.getOnlinePlayers().forEach { player ->
        if (player != p) {
            // 他のプレイヤーには表示しない（非表示）
            val despawnPacket = PacketPlayOutEntityDestroy(id)
            (player as CraftPlayer).handle.b.a(despawnPacket)
        }
    }

    // 指定されたプレイヤーにのみ表示
    val craftPlayer = p as CraftPlayer
    craftPlayer.handle.b.a(spawnPacket)      // アーマースタンド生成パケット
    craftPlayer.handle.b.a(metadataPacket)  // メタデータパケット
    craftPlayer.handle.b.a(equipmentPacket) // 装備の設定パケット

    func.invoke(armorStandEntity)

}












