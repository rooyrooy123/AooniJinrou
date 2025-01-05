package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.PluginProvider.plugin
import me.rooyrooy.aooniJinrou.gameStart
import me.rooyrooy.aooniJinrou.gameTime
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
                Bukkit.broadcastMessage("§b§l§n地下室が開放可能になりました！")
                Bukkit.getOnlinePlayers().forEach {
                    player -> player.playSound(player.location,"block.piston.contract",1.0f,2.0f)
                }
                Gate().openUnder()
            }else if (gameTime == 600){
                Bukkit.broadcastMessage("§e§l§n最上階が開放可能になりました！")
                Bukkit.getOnlinePlayers().forEach {
                        player -> player.playSound(player.location,"block.piston.extend",1.0f,2.0f)
                }
                Gate().openTOP()
            }
        },0L,20L)
    }


}