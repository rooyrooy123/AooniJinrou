package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.PluginProvider.plugin
import me.rooyrooy.aooniJinrou.gameStart
import me.rooyrooy.aooniJinrou.gameTime
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

class Timer{
    fun start(timeVal :Int){
        var taskId = Random().nextInt()
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{

            gameTime --
            if (!gameStart) {
                Bukkit.getScheduler().cancelTask(taskId)
            }
            if (gameTime == 900){
                //Bukkit.broadcastMessage("§b§l§n地下室が開放可能になりました！")
                Bukkit.broadcast(Component.text("§b§l§n地下室が開放可能になりました！"))
                Bukkit.broadcast(Component.text("§b地下室への鍵は、1階から最上階までのチェストに入っている鍵の欠片をすべて集める必要があります。"))
                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location,"block.piston.contract",1.0f,2.0f)
                    it.sendTitle("§b§l✝♢✝ §9§l地下室開放可能 §b§l✝♢✝","§9最上階への鍵を持つと、開放可能になります。")
                }
                Gate().openUnder()
            }else if (gameTime == 600){
                //Bukkit.broadcastMessage("§e§l§n最上階が開放可能になりました！")
                Bukkit.broadcast(Component.text("§e§l§n最上階が開放可能になりました！"))
                Bukkit.broadcast(Component.text("§b最上階への鍵は、地下室のチェストに入っている鍵の欠片をすべて集める必要があります。"))
                Bukkit.getOnlinePlayers().forEach {
                        it.playSound(it.location,"block.piston.extend",1.0f,2.0f)
                        it.sendTitle("§b§l✝♢✝ §9§l最上階開放可能 §b§l✝♢✝","§9最上階への鍵を持つと、開放可能になります。")
                }
                Gate().openTOP()
            }else if (gameTime == 120){
                Bukkit.broadcast(Component.text("§9脱出可能になりました！§1青鬼の鍵を持つと玄関から脱出可能！"))
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle("§b§l✝♢✝ §9§l脱出可能 §b§l✝♢✝","§1青鬼の鍵を所持者は玄関から脱出可能！")
                }
            }
        },0L,20L)
    }


}