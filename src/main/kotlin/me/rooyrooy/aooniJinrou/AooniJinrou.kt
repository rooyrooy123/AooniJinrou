package me.rooyrooy.aooniJinrou

import me.rooyrooy.aooniJinrou.Chest.ChestOpen
import me.rooyrooy.aooniJinrou.Chest.ChestPlace
import me.rooyrooy.aooniJinrou.Job.JobGive
import me.rooyrooy.aooniJinrou.Setting.SettingChest
import me.rooyrooy.aooniJinrou.Setting.SettingGui
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

//初期設定
val joblist = arrayListOf("aooni","hunter","hiroshi","mika","takeshi","takurou") //mika 狂人 //takeshi 妖狐 // takurou てるてる

val jobName = mapOf("aooni" to ChatColor.translateAlternateColorCodes('&',"§1青鬼"),
    "hunter" to ChatColor.translateAlternateColorCodes('&',"§2狩人"),
    "hiroshi" to ChatColor.translateAlternateColorCodes('&',"§dひろし"),

    "mika" to ChatColor.translateAlternateColorCodes('&',"§5美香(狂人)"),
    "takeshi" to ChatColor.translateAlternateColorCodes('&',"§eたけし"),
    "takurou" to ChatColor.translateAlternateColorCodes('&',"§cてるてる"))

val jobitem = mapOf("aooni" to Material.BLUE_WOOL,
    "hunter" to Material.GREEN_WOOL,
    "mika" to Material.PURPLE_WOOL,
    "takeshi" to Material.YELLOW_WOOL,
    "takurou" to Material.PINK_WOOL)

var chestOpened : ArrayList<String> = arrayListOf()
var gameJobList : MutableMap<Player,String> = mutableMapOf()

class AooniJinrou : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        server.pluginManager.registerEvents(ChestOpen(), this)
    }
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String> ): Boolean {
        if (cmd.name.equals("aoonijinrou-chest-place-all", ignoreCase = true)){ // #/shop items
            ChestPlace().placeAll()
            return true
        }
        if (cmd.name.equals("aoonijinrou-setting", ignoreCase = true)){ // #/shop items
            val player = Bukkit.getPlayer(sender.name) ?: return false
            SettingGui(config).open(player)
            return true
        }
        if (cmd.name.equals("aoonijinrou-job-give", ignoreCase = true)){
            JobGive()
            return true
        }
        if (cmd.name.equals("aoonijinrou-setting-chest-place", ignoreCase = true)){
            if (args.size != 1) return false
            val player = Bukkit.getPlayer(sender.name) ?: return false
            SettingChest(player.location).place(args[0].toInt())
            return true
        }
        if (cmd.name.equals("aoonijinrou-setting-chest-check", ignoreCase = true)){
            if (args.size != 1) return false
            val player = Bukkit.getPlayer(sender.name) ?: return false
            SettingChest(player.location).check(args[0].toBoolean())
            return true
        }
        return false
    }
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return when (command.name.lowercase()) {
            "aoonijinrou-setting-chest-check" -> commandTabCompleteBoolean1(args)
            else -> null
        }
    }

    private fun commandTabCompleteBoolean1(args: Array<out String>): MutableList<String> {
        val suggestions = mutableListOf<String>()
        if (args.size == 1) {
            val options = listOf("true" ,"false")
            suggestions.addAll(options.filter { it.startsWith(args[0], ignoreCase = true) })
        }
        return suggestions
    }


    override fun onDisable() {
        // Plugin shutdown logic
    }



}
