package me.rooyrooy.aooniJinrou.chat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.github.justadeni.standapi.PacketStand
import com.google.common.collect.Lists
import me.rooyrooy.aooniJinrou.chest.PluginInstance.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent
import java.lang.reflect.InvocationTargetException
import java.util.*


class OnChat () : Listener {
    @EventHandler
    fun onChat(event: PlayerChatEvent) {
        val message = event.message
        val player = event.player
        Bukkit.broadcastMessage(message)
        spawnEntityForPlayer(player,player.location,EntityType.ARMOR_STAND)
       // spawnArmorStandForPlayer(player,player.location)
        //spawnEntityForPlayer(player,player.location,EntityType.ARMOR_STAND)
    }
}
private fun spawnArmorStandForPlayer(player: Player,location: Location){
    ProtocolLibrary.getProtocolManager()
    lateinit var packetStand: PacketStand

    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
        packetStand = PacketStand(location)
    }, 20L)
    Bukkit.broadcastMessage("start")
    packetStand = PacketStand(location)
    Bukkit.broadcastMessage("a")
    packetStand.setVisible(false)
    Bukkit.broadcastMessage("a2")
    packetStand.setGlowingEffect(true)
    Bukkit.broadcastMessage("a3")
    packetStand.toRealStand()
    Bukkit.broadcastMessage("finished")


}
private fun spawnEntityForPlayer(player: Player, location: Location, type: EntityType) {
    val entityId = (1..100000).random()
    val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
    packet.integers.write(0, entityId)  // だしたPacketのエンティティのIDの設定
    packet.uuiDs.write(0, UUID.randomUUID())  // UUID
    packet.entityTypeModifier.write(0, type)  // エンティティのたいぷをせってい
    // location x,y,z
    packet.doubles.write(0, location.x)
    packet.doubles.write(1, location.y)
    packet.doubles.write(2, location.z)
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)

    // メタデータパケット作成
    //val metaPacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
    val pm = ProtocolLibrary.getProtocolManager()
    val metadataPacket = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA)
    metadataPacket.integers.write(0, entityId) //EntityIDで対象を決める。

    val watcher = WrappedDataWatcher()
    val serializer = WrappedDataWatcher.Registry.get(Byte::class.javaObjectType)
//0x20がSMALL 0x40 とうめい 0x01 が発光状態
    val metadataValue: Byte = (0x20 or 0x40 or 0x01).toByte()
    // オブジェクトをつｋる
    val metaIndex = WrappedDataWatcherObject(0, serializer)
    watcher.setObject(metaIndex, metadataValue)
     // パケットにwatcherをてきようさせっる
    metadataPacket.watchableCollectionModifier.write(0, watcher.watchableObjects)
    pm.sendServerPacket(player, metadataPacket) //メタデータをpacketで召喚したarmorstandに付与
    Bukkit.broadcastMessage("Finished")

}

private fun hideEntityForPlayer(player: Player, entityId: Int) {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)

    // Writing the entity ID to be destroyed
    packet.intLists.write(0, listOf(entityId))

    // Send the packet to the specific player to hide the entity
    try {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    }

}


