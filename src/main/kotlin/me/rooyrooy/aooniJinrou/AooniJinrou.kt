package me.rooyrooy.aooniJinrou

import me.rooyrooy.aooniJinrou.chest.ChestBreak
import me.rooyrooy.aooniJinrou.chest.ChestEvent
import me.rooyrooy.aooniJinrou.chest.ChestPlace
import me.rooyrooy.aooniJinrou.game.Event
import me.rooyrooy.aooniJinrou.job.JobGive
import me.rooyrooy.aooniJinrou.setting.ChestSetting
import me.rooyrooy.aooniJinrou.setting.GuiSetting
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin



enum class JobInfo(val displayName: String, val description: String,val item : Material) {
    AOONI(
        displayName = "§1青鬼",
        description = "§b青鬼はチェストから装備を回収し、一式そろうと、§1青鬼の杖§bを獲得。" +
                "杖を右クリックで青鬼に変身し、狩人以外を襲えます。なお、§c§nチェストから装備を取ると、もやもやが発生するため、注意。" +
                "§c§n青鬼陣営・卓郎(第三陣営)§c§l§nのみ走れる。",
        item = Material.BLUE_WOOL
    ),
    HIROSHI(
        displayName = "§dひろし",
        description = "§bひろしは、チェストから鍵の欠片などを回収しまくり、地下室や5階の鍵をつくろう！" +
                "5階で獲得できる§f§n銀の鍵§bと、5階解放時に§b§n1~4階§bのどこかのチェストに生成される§n金の鍵を用意することで、館の鍵を獲得可能。" +
                "脱出を目指そう！",
        item = Material.PINK_WOOL
    ),
    HUNTER(
        displayName = "§2狩人",
        description = "§b狩人は§b§n無敵§bで、§c§n手持ちの弓でプレイヤーを一撃で撃破できます§c(※クールタイムあり)。" +
                "§a§n弓のクールタイム中以外は走ることができます！§bひろしを誤射すると、一定時間、青鬼以外に盲目が付与されます。",
        item = Material.GREEN_WOOL
    ),
    MIKA(
        displayName = "§5美香青鬼",
        description = "§b美香青鬼は§1青鬼の仲間で、§bダミーアイテムを所持。チェストから防具を獲得できるが、" +
                "§c§n杖にはならないので青鬼に渡しましょう。§4青鬼と美香はお互い正体を知りません。" +
                "§b5階解放まで、チェストの近くだと隠れ玉のタイマーの減りが遅くなります。",
        item = Material.PURPLE_WOOL
    ),
    TAKESHI(
        displayName = "§eたけし",
        description = "§eたけしは§e§l§n第三陣営§bで青鬼、ひろしの勝利時に180秒間、" +
                "§e§l§nタンス(館全体のタンス、クローゼット)の上に滞在していたまま生存していると勝利。",
        item = Material.YELLOW_WOOL

    ),
    TAKUROU(
        displayName = "§c卓郎",
        description = "§c卓郎は§c§l§n第三陣営。§c§l青鬼に1回殴られ、なおかつ狩人に射抜かれると勝利。" +
                "§b2回目以降青鬼に殴られると死亡。一人目の鬼は２回目の攻撃をしても殺せない。",
        item = Material.RED_WOOL
    );
}


//初期設定
val jobList = arrayListOf("AOONI","HUNTER","HIROSHI","MIKA","TAKESHI","TAKUROU") //mika 狂人 //takeshi 妖狐 // takurou てるてる
var gameStart : Boolean = true
var gameChestOpened : ArrayList<String> = arrayListOf()
var gameChestEquipment : MutableMap<String, Material> = mutableMapOf()
var gameJobList : MutableMap<Player,String> = mutableMapOf()
var gameKeyUnderNeed = 0
var gameKeyTopNeed = 0
var gameChestCount : MutableMap<Int,Int> = mutableMapOf()
var gameChestFloor : MutableMap<BlockState,String> = mutableMapOf()
var gameWorld = Bukkit.getWorld("world")

class AooniJinrou : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        server.pluginManager.registerEvents(ChestEvent(), this)
        server.pluginManager.registerEvents(Event(), this)
        val worldString = config.getString("AooniJinrou.Setting.Game.World") ?: "world"
        gameWorld = Bukkit.getWorld(worldString)
    }
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String> ): Boolean {
        if (cmd.name.equals("aoonijinrou-chest-place-all", ignoreCase = true)){ // #/shop items
            ChestPlace(config).placeAll()
            return true
        }
        if (cmd.name.equals("aoonijinrou-chest-place-floor", ignoreCase = true)){ // #/shop items
            if (args.size != 1) return false
            ChestPlace(config).place(args[0].toInt())
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
        ChestBreak(config).breakAll()
    }



}
