package me.rooyrooy.aooniJinrou

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.github.justadeni.standapi.PacketStand
import com.github.justadeni.standapi.PacketStand.Companion.fromRealStand
import com.github.shynixn.mccoroutine.bukkit.SuspendingCommandExecutor
import com.github.ucchyocean.lc3.japanize.IMEConverter
import com.github.ucchyocean.lc3.japanize.YukiKanaConverter
import me.rooyrooy.aooniJinrou.chat.OnChat

import me.rooyrooy.aooniJinrou.chest.Chest
import me.rooyrooy.aooniJinrou.chest.ChestEvent
import me.rooyrooy.aooniJinrou.chest.ChestExtractLocations
import me.rooyrooy.aooniJinrou.chest.PluginInstance
import me.rooyrooy.aooniJinrou.game.*
import me.rooyrooy.aooniJinrou.game.Timer
import me.rooyrooy.aooniJinrou.job.JobGive
import me.rooyrooy.aooniJinrou.job.aooni.AooniStick
import me.rooyrooy.aooniJinrou.key.Key
import me.rooyrooy.aooniJinrou.setting.ChestSetting
import me.rooyrooy.aooniJinrou.setting.GuiSetting
import org.bukkit.Bukkit
import org.bukkit.Bukkit.broadcastMessage
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


//初期設定
val jobList = arrayListOf("AOONI","HUNTER","HIROSHI","MIKA","TAKESHI","TAKUROU") //mika 狂人 //takeshi 妖狐 // takurou てるてる
var gameStart : Boolean = true
var gamePlayerChestOpened : ArrayList<String> = arrayListOf()
var gameChestEquipment : MutableMap<String, Material> = mutableMapOf()
var gameChestIDCount = 0
var gameChestID : MutableMap<Int,Block> = mutableMapOf()
val gameAreaEffectCloudDurationSpeed : MutableMap<UUID,Int> = mutableMapOf()
var gameJobList : MutableMap<Player,String> = mutableMapOf()
var gameKeyUnderNeed = 0
var gameKeyTopNeed = 0
var gameChestCount : MutableMap<Int,Int> = mutableMapOf()
//var gameChestFloor : MutableMap<BlockState,String> = mutableMapOf()
var gameWorld = Bukkit.getWorld("world")
var gameTime = 0
var gameKeyPlateSilver = Location(gameWorld,0.0,0.0,0.0)
var gameAooniKillCount : MutableMap<Player,Int> = mutableMapOf()
var gameAooniKillLimit : MutableMap<Player,Int> = mutableMapOf()
var gameGateUnderFloor = Location(gameWorld , 0.0,0.0,0.0)
var gameGateTopFloor = Location(gameWorld , 0.0,0.0,0.0)
var gameSignEntrance = Location(gameWorld,0.0,0.0,0.0) //玄関看板
var gameSignEntranceTeleport = Location(gameWorld,0.0,0.0,0.0) //玄関看板TP先
var gameSignEscape = Location(gameWorld,0.0,0.0,0.0) //脱出看板
var gameSignEntranceReturn = Location(gameWorld,0.0,0.0,0.0)
var gameHideBallCount : MutableMap<Player,Int> = mutableMapOf()
class AooniJinrou : JavaPlugin() {
    private lateinit var chestLocations: Map<String, List<List<Int>>>
    private lateinit var protocolManager: ProtocolManager
    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        server.pluginManager.registerEvents(ChestEvent(), this)
        server.pluginManager.registerEvents(Event(), this)
        server.pluginManager.registerEvents(Damage(), this)
        server.pluginManager.registerEvents(AooniStick(), this)
        server.pluginManager.registerEvents(Key(), this)
        server.pluginManager.registerEvents(HideBall(), this)
        server.pluginManager.registerEvents(OnChat(),this)
        protocolManager = ProtocolLibrary.getProtocolManager()

        PluginInstance.plugin = this


        //リセ時の処理
        val worldString = config.getString("AooniJinrou.Setting.Game.World") ?: "world"
        gameWorld = Bukkit.getWorld(worldString)
        gameTime = config.getInt("AooniJinrou.Setting.Game.Time")

        // ちぇすとのplaceLocationデータを抽出
        chestLocations = ChestExtractLocations().getLocations(config)
        gameGateUnderFloor =  getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Gate.UnderFloor")
        gameGateTopFloor =  getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Gate.TopFloor")
        gameKeyPlateSilver = getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Plate.SilverKey")
        gameSignEntrance = getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Entrance.Block")
        gameSignEntranceTeleport = getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Entrance.Teleport")
        gameSignEscape = getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Escape")
        gameSignEntranceReturn = getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Entrance.Return")
        Sign(gameSignEntrance).setSignText(arrayListOf(
            "§b§l看板右クリックで",
            "§b§l館の外に出る。",
            "§9§l§n脱出には青鬼の鍵必須",
            "§c§l§n(残り時間120秒から)"))
        Sign(gameSignEscape).setSignText(arrayListOf(
            "§b§l看板右クリックで",
            "§b§l§n脱出する！",
            "§4§l§n脱出には",
            "§1§l§n青鬼の鍵§4§lの所持必須！"))
        Sign(gameSignEntranceReturn).setSignText(arrayListOf(
            "",
            "§b§l看板右クリックで",
            "§b§玄関に戻る",
            ""
        ))
        //すたーとじのしょり
        Timer().start(gameTime)
        Chest().placeAll(chestLocations)
        Bukkit.getOnlinePlayers().forEach {
            gameJobList[it] = "HIROSHI"
            val countHideBall = config.getInt("AooniJinrou.Setting.HideBall.${gameJobList[it]}")
            gameHideBallCount[it] = countHideBall
            it.inventory.addItem(Items.HIDEBALL.toItemStack())
        }
        ActionBar().start()
        val romajiText = "riro-dogakannryou"
        val jp = YukiKanaConverter.conv(romajiText)
        broadcastMessage(jp)
        val kanji = IMEConverter.convByGoogleIME(jp)
        broadcastMessage(kanji)



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

        if (cmd.name.equals("armorstand", ignoreCase = true)) { // #/shop items
            var testStand: PacketStand? = null
            var realStand: ArmorStand? = null
            var serializedString: String = ""
            val player = Bukkit.getPlayer(sender.name) ?: return false
            val location = player.location
            sender.sendMessage("stand spawned!")
            testStand = PacketStand(location , "StandAPI_test")
            sender.sendMessage("stand metadata sent")
            //testStand!!.setGlowingEffect(true)
            //testStand!!.setArms(true)
            testStand.setSmall(true)
            testStand.setBaseplate(false)
            testStand.setCustomNameVisible(true)
            testStand.setCustomName("test name")
            testStand.setVisible(false)
            testStand.toRealStand()

        }
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
        if (cmd.name.equals("aoonijinrou-start", ignoreCase = true)){ //
            Start(gameWorld!!)
            Bukkit.broadcastMessage("start")
            return true
        }
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
        gameStart = false
        Chest().breakALL(chestLocations)
    }



}
