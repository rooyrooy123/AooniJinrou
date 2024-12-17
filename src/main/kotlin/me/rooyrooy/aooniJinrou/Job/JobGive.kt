package me.rooyrooy.aooniJinrou.Job

import me.rooyrooy.aooniJinrou.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class JobGive(){
    private val plugin: JavaPlugin = JavaPlugin.getPlugin(AooniJinrou::class.java) //CONFIG読み込み、書き込み用
    init {
        val jobAmount :MutableMap<String,Int> = mutableMapOf()
        joblist.forEach { job -> // job == aooni,hiroshi
            val amount = plugin.config.getInt("AooniJinrou.Setting.Job.${job}")
            jobAmount[job] = amount
            //Bukkit.broadcastMessage("${job}${amount}")
        }
    }
    fun random(){

    }
    fun set(player: Player,job: String){
        player.sendMessage(job)
        if (joblist.contains(job)){

            gameJobList[player] = job
            player.sendMessage("§bあなたの役職は" + jobName[job]!! + "§bになりました！")
            player.sendMessage(jobinfo[job]!!)
        }else if (job == "reset"){
            gameJobList.remove(player)
        }
    }
}
