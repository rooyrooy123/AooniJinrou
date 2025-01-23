package me.rooyrooy.aooniJinrou.job

import me.rooyrooy.aooniJinrou.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class JobGive{
    private val plugin: JavaPlugin = JavaPlugin.getPlugin(AooniJinrou::class.java) //CONFIG読み込み、書き込み用
    private val config = AooniJinrou.instance.config
    init {

        jobList.forEach { job -> // job == aooni,hiroshi
            val amount = plugin.config.getInt("AooniJinrou.Setting.Job.${job}")
            gameJobCount[job] = amount
            //Bukkit.broadcastMessage("${job}${amount}")
        }
    }
    fun random(){
        val players = Expressions().getJoinedPlayers()
        val jobAmountMap = gameJobCount
        if (jobAmountMap["HUNTER"]!! < 1){
            Bukkit.broadcast(Component.text("§f[§4エラー§f]§c「狩人(HUNTER)」は1人以上必要です"))
            return
        }
        if (jobAmountMap["AOONI"]!! < 1){
            Bukkit.broadcast(Component.text("§f[§4エラー§f]§c「青鬼(AOONI)」は1人以上必要です"))
            return
        }
        players.shuffled()
        for (job in jobList){
            val jobAmount = jobAmountMap[job] ?: 0
            var counter = 0
            while (counter < jobAmount){
                val player = players.random()
                JobGive().set(player,job)
                players.remove(player)
                counter ++
            }
            if (players.isEmpty()){
                Bukkit.broadcast(Component.text("§f[§4エラー§f]§c「ひろし」を配布する人数が足りていません！"))
                return
            }
        }
        players.forEach {
            JobGive().set(it,"HIROSHI")
        }
        Bukkit.getOnlinePlayers().forEach {
            Bukkit.broadcast(Component.text("${it.name} ${gameJobList[it]}"))
        }
    }

    fun set(player: Player,job: String){
        if (jobList.contains(job)){
            gameJobList[player] = job
            //gameJobPlayerList[job]?.add(player)
            gameJobPlayerList.getOrPut(job) { mutableListOf() }.add(player)
            if (job == "HUNTER"){
                Bukkit.broadcast(Component.text("§1[§9青鬼§1]§e§l§n${player.name}§bが§c§l狩人§bに選ばれました！"))
                Bukkit.broadcast(Component.text("§c狩人は§b§n基本無敵§bで、手持ちの弓でプレイヤーを§c一撃で撃破できます！"))
                Bukkit.broadcast(Component.text("§b青鬼っぽい人がいたら、狩人に報告しよう！"))
            }

            giveJobInventory(player)
            player.sendTitle("${JobInfo.valueOf(job).color}あなたは${JobInfo.valueOf(job).displayName}です！","${JobInfo.valueOf(job).color}${JobInfo.valueOf(job).descriptionTitle}")
            player.sendMessage("§bあなたの役職は${JobInfo.valueOf(job).displayName}§bになりました！")
            player.sendMessage(JobInfo.valueOf(job).description)



        }else if (job == "reset"){
            gameJobList.remove(player)
        }
    }
    private fun giveJobInventory(player:Player){
        val job = gameJobList[player] ?: return
        if (job == "AOONI" || job ==  "MIKA"){
            setHideBall(player)
            player.inventory.setItem(9,Items.KEY_UNDERFLOOR_PARTS.toItemStack())
            player.inventory.setItem(10,Items.KEY_UNDERFLOOR.toItemStack())
            player.inventory.setItem(11,Items.KEY_TOPFLOOR_PARTS.toItemStack())
            player.inventory.setItem(12,Items.KEY_TOPFLOOR.toItemStack())
            player.inventory.setItem(13,Items.KEY_GOLD.toItemStack())
            player.inventory.setItem(14,Items.KEY_SILVER.toItemStack())
            player.inventory.setItem(15,Items.KEY_AOONI.toItemStack())
        }else if (job == "HIROSHI" ||
            job == "TAKESHI"||
            job == "TAKUROU"){
            setHideBall(player)
        }else if (job == "HUNTER"){
            player.inventory.addItem(Items.BOW.toItemStack())
            player.inventory.addItem(Items.ARROW_STACK.toItemStack())
        }



    }
    private fun setHideBall(player:Player){
        val countHideBall = config.getInt("AooniJinrou.Setting.HideBall.${gameJobList[player]}")
        gameHideBallCount[player] = countHideBall

        giveItemToNonMainHandSlot(player,Items.HIDEBALL.toItemStack())

    }
}
fun giveItemToNonMainHandSlot(player: Player, item: ItemStack) {
    val inventory = player.inventory
    val mainHandSlot = inventory.heldItemSlot
    for (slot in 0..35) {
        if (slot == mainHandSlot) continue

        if (inventory.getItem(slot) == null || inventory.getItem(slot)?.type == Material.AIR) {
            inventory.setItem(slot, item)
            return
        }
    }
}
