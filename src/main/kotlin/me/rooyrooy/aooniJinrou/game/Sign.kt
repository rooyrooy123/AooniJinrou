package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Sign
import org.bukkit.entity.Player

class Sign(val Blocklocation: Location) {
    fun setSignText( lines: List<String>) {
        val world = Blocklocation.world ?: return

        val block = world.getBlockAt(Blocklocation)
        // 看板かどうかチェック
        if (block.type == Material.OAK_SIGN
            ||block.type == Material.OAK_WALL_SIGN) {
            val sign = block.state as Sign
            for ((index, line) in lines.withIndex()) {
                if (index < 4) {

                    sign.line(index, Component.text(line))
                }
            }
            sign.update()

            /*sign.line(1, Component.text("§b看板右クリックで"))
            sign.line(2, Component.text("§b館の外に出る。"))
            sign.line(3, Component.text("§9脱出には青鬼の鍵必須"))
            sign.line(4, Component.text("§c§l§n(残り時間120秒から)"))*/
        }
    }
    fun entranceTeleport(player: Player){

        if (Blocklocation == gameSignEntrance){
            if (gameTime > 120){
                player.sendMessage("§4まだ、残り時間120秒を切っていないので、外に出れません。")
                return
            }
            if (player.inventory.containsAtLeast(Items.KEY_AOONI.toItemStack(),1)){
                val yaw = player.location.yaw
                val pitch = player.location.pitch
                // YawとPitchをset
                val teleport = gameSignEntranceTeleport.clone().apply {
                    this.yaw = yaw
                    this.pitch = pitch
                }
                // プレイヤーをテレポート
                player.teleport(teleport)
                player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT,1.0f,1.0f)
                //player.teleport(gameSignEntranceTeleport)
            }
            else{
                player.sendMessage("§1青鬼の鍵§4を所持していません。")
            }

        }
    }
    fun entranceReturn(player: Player){
        if (Blocklocation == gameSignEntranceReturn){
            val yaw = player.location.yaw
            val pitch = player.location.pitch
            // YawとPitchをset
            val teleport = gameSignEntrance.clone().apply {
                this.yaw = yaw
                this.pitch = pitch
            }
            // プレイヤーをテレポート
            player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT,1.0f,1.0f)
            player.teleport(teleport)
            //player.teleport(gameSignEntranceTeleport)
        }
    }
    fun escape(player: Player){
        if (Blocklocation == gameSignEscape){
            if (gameTime > 120){
                player.sendMessage("§4まだ、残り時間120秒を切っていないので、外に出れません。")
                return
            }
            if (!player.inventory.containsAtLeast(Items.KEY_AOONI.toItemStack(),1)){
                player.sendMessage("§1青鬼の鍵§4を所持していません。")
                readln()
            }
            Bukkit.broadcast(Component.text("§e§l§n${player.name}§b§lが脱出に成功しました！"))
        }
    }
}