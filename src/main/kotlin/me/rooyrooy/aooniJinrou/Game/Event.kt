package me.rooyrooy.aooniJinrou.Game


import me.rooyrooy.aooniJinrou.gameJobList
import me.rooyrooy.aooniJinrou.gameStart
import me.rooyrooy.aooniJinrou.jobName
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent

class Event() : Listener{
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
                if (damager is Arrow) {
                    val attacker = damager.shooter
                    if (attacker is Player) { // attacker victimが人間かつ弓で攻撃された場合
                        if (gameJobList[attacker] == "hunter"){ //狩人かの確認
                            victim.health = 0.0
                            return
                        }
                    }
                }else if (damager is Player){
                    val attacker = damager
                    if (gameJobList[attacker] == "aooni"){ // 青鬼の攻撃だった場合
                        if (attacker.equipment.helmet.type != Material.AIR ){
                            victim.health = 0.0
                            return
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
            val attackerJob = gameJobList[attacker]
            val victimJob = gameJobList[victim]
            if (gameJobList[attacker] == "hunter"){ //ハンターが殺した
                Bukkit.broadcastMessage(
                    "${jobName[victimJob]}${victim.name}§bは${jobName[attackerJob]}${attacker.name}§bに射抜かれた。")
            }else if (gameJobList[attacker] == "aooni"){ //青鬼が殺した
                Bukkit.broadcastMessage(
                    "§c${victim.name}§bは§9§l青鬼§bに食べられた。")


            }
        }
    }
}