package me.rooyrooy.aooniJinrou.game

import com.saicone.rtag.util.SkullTexture
import com.zorbeytorunoglu.kLib.extensions.name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rooyrooy.aooniJinrou.*
import me.rooyrooy.aooniJinrou.PluginProvider.plugin
import me.rooyrooy.aooniJinrou.chest.Chest
import me.rooyrooy.aooniJinrou.job.JobGive
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * 間違ってダッシュするのを防ぐため、開始TP時に盲目と鈍足をつける
 *
 */
class Start() : Listener {
    private val joinItem: ItemStack = SkullTexture.getTexturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTk2MGQ2ZmZhZjQ0ZThhZmNiZGY4YjI5YTc3ZDg0Y2UyMmM3MWQwMGM2NGJmZDk5YWYzNDBhNjk1MzViZmQ3In19fQ==")
    private val quitItem: ItemStack =  SkullTexture.getTexturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg5ODg1MjNmMjYzMWRlNWNiMDFmZGVhMzg3MDViNjRlYjkwNjY2N2Q4ZDk5YmNiODU5YTBhMTZkYjU5MWE3OCJ9fX0=")
    private val joinHelmet = ItemStack(Material.LIME_BANNER)
    private val quitHelmet = ItemStack(Material.RED_BANNER)
    init { //初期化
        joinItem.editMeta {
            it.displayName(Component.text("§f§L[§e§l現在§8§l:§4§l不参加予定§f§l]§a§l§Nクリックで次試合に参加する"))
        }
        quitItem.editMeta {
            it.displayName(Component.text("§f§L[§e§l現在§8§l:§b§l参加予定§f§l]§c§l§Nクリックで不参加へ"))
        }
        joinHelmet.editMeta {
            it.addEnchant(Enchantment.BINDING_CURSE, 1, true) //束縛の呪い、脱がれたくないから
        }
        quitHelmet.editMeta {
            it.addEnchant(Enchantment.BINDING_CURSE, 1, true)
        }
    }
    private val scope = CoroutineScope(Dispatchers.Default)
    fun start(){
        var taskId = Random().nextInt()
        var counter = 5
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{

            if (counter <= 0){

                val locationRedStone = Location(Bukkit.getWorld("world"),246.0 ,80.0 ,116.0 )
                Reset().reset {
                    val block = locationRedStone.block
                    block.type = Material.REDSTONE_BLOCK
                }

                val players = Expressions().getJoinedPlayers()

                Bukkit.getOnlinePlayers().forEach {
                    Expressions().teleportWithPassenger(it, gameStartLocation)
                }
                Bukkit.getOnlinePlayers().forEach {
                    it.gameMode = GameMode.SPECTATOR
                }

                val config = AooniJinrou.instance.config
                players.forEach {
                    it.gameMode = GameMode.ADVENTURE
                    it.inventory.clear()
                    it.playSound(it.location,Sound.ENTITY_ENDER_DRAGON_GROWL,1.0f,2.0f)

                }
                JobGive().random()
                val aooniList : MutableList<String> = mutableListOf()

                gameJobPlayerList["AOONI"]?.forEach {
                    for (player in gameJobPlayerList["AOONI"]!!){
                        aooniList.add(player.name)
                    }

                }
                gameJobPlayerList["AOONI"]?.forEach {
                    it.sendMessage("§9§l§n青鬼一覧 ➤ §b§l${aooniList}")
                }
                gameStart = true
                Timer().start(gameTime)
                ActionBar().start()
                Chest().placeAll(Expressions().getChestLocations())
                Bukkit.getScheduler().cancelTask(taskId)
            }else {
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle("§e>> §b§l${counter} §e<<", "")
                    it.playSound(it.location,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,0.5f)
                }
            }
            counter --
        },0L,20L)







            //setupChestCount()

    }
    fun lobbyItem(player:Player){
        if (!gameStart) return
        player.inventory.clear()
        if (gameJoinGame[player] == true) {
            player.inventory.setItem(4, quitItem)
        }else{
            player.inventory.setItem(4,joinItem)
        }

    }

    @EventHandler
    fun onPlayerJoin(event:PlayerJoinEvent){
        val player = event.player
        gameQuit(player,false)
        if (!gameStart){
            player.teleport(gameLobby)
            lobbyItem(player)
            Bukkit.getScheduler().scheduleSyncDelayedTask (plugin,{
                player.gameMode = GameMode.ADVENTURE
            }, 1L)
        }else{
            player.teleport(gameStartLocation)
            Bukkit.getScheduler().scheduleSyncDelayedTask (plugin,{
                player.gameMode = GameMode.SPECTATOR
            }, 1L)
        }
    }
    @EventHandler
    fun onPlayerQuit(event:PlayerQuitEvent){
        val player = event.player
        if (gameStart) {
            gameQuit(player, false)
        }else{
            gameQuit(player,true)
        }
    }

    @EventHandler
    fun onPlayerSwitchJoin(event: PlayerInteractEvent) {
        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_AIR || event.action == Action.RIGHT_CLICK_AIR){
            val player = event.player
            if (player.hasCooldown(Material.PLAYER_HEAD)) return

            val item = player.inventory.itemInMainHand
            if (item.type== Material.STONE){
                player.inventory.addItem(joinItem)
            }
            if (item.type == joinItem.type && item.itemMeta.displayName() == joinItem.itemMeta.displayName()) {
                event.isCancelled = true
                player.inventory.setItemInMainHand(quitItem)
                player.inventory.helmet = joinHelmet
                gameJoin(player,true)
            }
            else if (item.type == quitItem.type && item.itemMeta.displayName() == quitItem.itemMeta.displayName()) {
                event.isCancelled = true
                player.inventory.setItemInMainHand(joinItem)
                player.inventory.helmet = quitHelmet
                gameQuit(player,true)

            }else{
                return
            }
            player.setCooldown(Material.PLAYER_HEAD,60)
            return
        }
    }
    fun gameJoin(player:Player,message:Boolean){

        playSoundToNearPlayers(player.location,Sound.ENTITY_ITEM_PICKUP,8.0,1.0f,0.5f)
        gameJoinGame[player] = true

        if (message){
            Bukkit.getOnlinePlayers().forEach {
                it.spawnParticle(Particle.END_ROD, player.location, 10, 0.5, 0.5, 0.5, 0.05)
            }
            val players = Expressions().getJoinedPlayers()
            Bukkit.broadcast(Component.text("§f§l>>§e§n>${player.name}§bが試合に参加しました！§8§l[§e${players.size}§8§7/§f${Bukkit.getOnlinePlayers().size}§8§l]"))
        }

    }
    private fun gameQuit(player: Player,message:Boolean){
        gameJoinGame[player] = false
        playSoundToNearPlayers(player.location,Sound.ENTITY_ITEM_PICKUP,8.0,1.0f,0.5f)

        if (message) {
            Bukkit.getOnlinePlayers().forEach {
                it.spawnParticle(Particle.DAMAGE_INDICATOR, player.location, 10, 0.5, 0.5, 0.5, 0.05)
            }
            val players = Expressions().getJoinedPlayers()
            Bukkit.broadcast(Component.text("§f§l>>§e§n>${player.name}§cが試合への参加を取り消ししました！§8§l[§e${players.size}§8§7/§f${Bukkit.getOnlinePlayers().size}§8§l]"))
        }
    }
    @EventHandler
    fun noDropJoinSwitchItem(event:PlayerDropItemEvent){
        val item = event.itemDrop
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE) return
        if (item.name == joinItem.i18NDisplayName || item.name == quitItem.i18NDisplayName){
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onSwitchGameMode(event:PlayerGameModeChangeEvent){
        if (gameStart) return
        val newGameMode = event.newGameMode
        if (newGameMode != GameMode.ADVENTURE) return
        if (event.player.hasPermission("admin")){
            event.player.inventory.addItem(joinItem)
        }

    }
}
fun playSoundToNearPlayers(location: Location, sound: Sound, radius: Double, volume: Float,pitch:Float) {
    // 半径内のプレイヤーを取得してサウンドを再生
    val world = location.world ?: return
    world.players.filter { it.location.distance(location) <= radius }.forEach { player ->
        player.playSound(location, sound, volume, pitch)
    }
}
// 旧chest配置用function
fun setupChestCount() {
    var floor: Int
    gameKeyTopNeed = 0
    gameKeyUnderNeed = 0
    for (num in -10..10){
        gameChestCount.clear()
    }
    for (entity in gameWorld!!.entities) {
        // ArmorStandかつ名前に "Floor" を含む場合に処理
        if (entity.type == EntityType.ARMOR_STAND && entity is ArmorStand) {
            if (entity.customName?.contains("Floor") == true) {
                val name = entity.name
                floor = name.replace("Floor","").toInt()
                gameChestCount[floor] = gameChestCount[floor]!! + 1
                if (floor >= 1){ //1階~4階
                    gameKeyUnderNeed += 1
                }else if (floor <= -1){
                    gameKeyTopNeed += 1
                }
            }
        }
    }

}