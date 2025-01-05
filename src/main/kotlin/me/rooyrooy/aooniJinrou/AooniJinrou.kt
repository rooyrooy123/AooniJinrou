package me.rooyrooy.aooniJinrou

import me.rooyrooy.aooniJinrou.chest.Chest
import me.rooyrooy.aooniJinrou.chest.ChestEvent
import me.rooyrooy.aooniJinrou.chest.ChestExtractLocations
import me.rooyrooy.aooniJinrou.game.Damage
import me.rooyrooy.aooniJinrou.game.Event
import me.rooyrooy.aooniJinrou.game.Gate
import me.rooyrooy.aooniJinrou.game.Timer
import me.rooyrooy.aooniJinrou.job.JobGive
import me.rooyrooy.aooniJinrou.job.aooni.AooniStick
import me.rooyrooy.aooniJinrou.key.Key
import me.rooyrooy.aooniJinrou.setting.ChestSetting
import me.rooyrooy.aooniJinrou.setting.GuiSetting
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


//初期設定
val jobList = arrayListOf("AOONI","HUNTER","HIROSHI","MIKA","TAKESHI","TAKUROU") //mika 狂人 //takeshi 妖狐 // takurou てるてる
var gameStart : Boolean = true
var gamePlayerChestOpened : ArrayList<String> = arrayListOf()
var gameChestEquipment : MutableMap<String, Material> = mutableMapOf()
var gameChestIDCount = 0
var gameChestID : MutableMap<Int,Block> = mutableMapOf()
var gameJobList : MutableMap<Player,String> = mutableMapOf()
var gameKeyUnderNeed = 0
var gameKeyTopNeed = 0
var gameChestCount : MutableMap<Int,Int> = mutableMapOf()
//var gameChestFloor : MutableMap<BlockState,String> = mutableMapOf()
var gameWorld = Bukkit.getWorld("world")
var gameTime = 0
var gameKeyPlateSilver = Location(gameWorld,0.0,0.0,0.0)
var gameAooniKillLimit : MutableMap<Player,Int> = mutableMapOf()
var gameGateUnderFloor = Location(gameWorld , 0.0,0.0,0.0)
var gameGateTopFloor = Location(gameWorld , 0.0,0.0,0.0)

class AooniJinrou : JavaPlugin() {
    private lateinit var chestLocations: Map<String, List<List<Int>>>
    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        server.pluginManager.registerEvents(ChestEvent(), this)
        server.pluginManager.registerEvents(Event(), this)
        server.pluginManager.registerEvents(Damage(), this)
        server.pluginManager.registerEvents(AooniStick(), this)
        server.pluginManager.registerEvents(Key(), this)

        //リセ時の処理
        val worldString = config.getString("AooniJinrou.Setting.Game.World") ?: "world"
        gameWorld = Bukkit.getWorld(worldString)
        gameTime = config.getInt("AooniJinrou.Setting.Game.Time")

        // ちぇすとのplaceLocationデータを抽出
        chestLocations = ChestExtractLocations().getLocations(config)
        gameGateUnderFloor =  getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Gate.UnderFloor")
        gameGateTopFloor =  getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Gate.TopFloor")
        gameKeyPlateSilver = getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Plate.SilverKey")
        Gate().setup()
        //すたーとじのしょり
        Timer().start(gameTime)
        Chest().placeAll(chestLocations)

    }
    private fun getLocationFromCoordinate(path: String): Location {
        val coordinateList = config.getList(path)
        val doubleList: List<Double>? = coordinateList?.mapNotNull {
            when (it) {
                is Number -> it.toDouble()
                is String -> it.toDoubleOrNull()
                else -> null
            }
        }
        if (doubleList != null && doubleList.size >= 3) {
            val location = Location(gameWorld, doubleList[0], doubleList[1], doubleList[2])
            return location
        }else{
            return Location(gameWorld, 0.0, 64.0, 0.0)
        }
    }
    // リスト形式の座標を取得

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String> ): Boolean {
        if (cmd.name.equals("aoonijinrou-chest-place-all", ignoreCase = true)){ // #/shop items
            //ChestPlace(config).placeAll()
            Chest().placeAll(chestLocations)
            return true
        }
   /*     if (cmd.name.equals("aoonijinrou-chest-place-floor", ignoreCase = true)){ // #/shop items
            if (args.size != 1) return false
            //ChestPlace(config).place(args[0].toInt())
            Chest().place(chestLocations,args[0].toInt())
            return true
        }*/

        if (cmd.name.equals("aoonijinrou-setting", ignoreCase = true)){ // #/shop items
            val player = Bukkit.getPlayer(sender.name) ?: return false
            GuiSetting(config).open(player)
            return true
        }
        if (cmd.name.equals("aoonijinrou-job-give", ignoreCase = true)){
            JobGive()
            return true
        }
        if (cmd.name.equals("aoonijinrou-job-set", ignoreCase = true)){
            if (args.size != 2) return false
            val player = Bukkit.getPlayer(args[0]) ?: return false
            JobGive().set(player,args[1])
            return true
        }
        if (cmd.name.equals("aoonijinrou-setting-chest-place", ignoreCase = true)){
            if (args.size != 1) return false
            val player = Bukkit.getPlayer(sender.name) ?: return false
            ChestSetting(player.location).place(args[0].toInt())
            return true
        }
        if (cmd.name.equals("aoonijinrou-setting-chest-check", ignoreCase = true)){
            if (args.size != 1) return false
            val player = Bukkit.getPlayer(sender.name) ?: return false
            ChestSetting(player.location).check(args[0].toBoolean())
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
            "aoonijinrou-job-set" -> commandTabCompleteJobList(args)
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
    private fun commandTabCompleteJobList(args: Array<out String>): MutableList<String> {
        val suggestions = mutableListOf<String>()
        if (args.size == 1) {
            val options =  getOnlinePlayers().map {it.name}
            suggestions.addAll(options.filter { it.startsWith(args[0], ignoreCase = true) })
        }
        if (args.size == 2) {
            val options = mutableListOf("reset")
            options.addAll(jobList)

            suggestions.addAll(options.filter { it.startsWith(args[1], ignoreCase = true) })
        }
        return suggestions
    }


    override fun onDisable() {
        // Plugin shutdown logic
        //ChestBreak(config).breakAll()
        Chest().breakALL(chestLocations)
    }



}
