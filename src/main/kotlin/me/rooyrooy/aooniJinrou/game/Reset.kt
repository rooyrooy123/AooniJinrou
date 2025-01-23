package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.*
import me.rooyrooy.aooniJinrou.chest.Chest
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Bukkit.broadcast
import org.bukkit.World
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.ArmorStand

class Reset{

    private val config = AooniJinrou.instance.config

    fun reset(processAdd: () -> Unit) {
        // ゲーム終了時のラムダ式
        // gameJoinGame.clear()
        // Teleport
        //Bukkit.getOnlinePlayers().forEach {
        //            Start().lobbyItem(it)
        //        }
        gameStart = false
        gameJobPlayerList.clear()
        gameHideBallCount.clear()
        gameAooniKillCount.clear()
        gameChestID.clear()
        gameChestCount.clear()
        gameChestIDCount = 0
        gameChestEquipment.clear()
        gamePlayerChestOpened.clear()

        //リセ時の処理
        val worldString = config.getString("AooniJinrou.Setting.Game.World") ?: "world"
        gameWorld = Bukkit.getWorld(worldString)
        gameWorld?.let { removeEntities(it) }



        gameTime = config.getInt("AooniJinrou.Setting.Game.Time")

        // ちぇすとのplaceLocationデータを抽出
        val chestLocations = Expressions().getChestLocations()
        gameStartLocation =  Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Start")

        gameGateUnderFloor =  Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Gate.UnderFloor")
        gameGateTopFloor =  Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Gate.TopFloor")
        gameKeyPlateSilver = Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Plate.SilverKey")
        gameSignEntrance = Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Entrance.Block")
        gameSignEntranceTeleport = Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Entrance.Teleport")
        gameSignEscape = Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Escape")
        gameSignEntranceReturn = Expressions().getLocationFromCoordinate("AooniJinrou.Location.${gameWorld?.name}.Sign.Entrance.Return")
        gameLobby = Expressions().getSpawnLocationFromConfig()
        Gate().setup()

        val jobSection = config.getConfigurationSection("AooniJinrou.Setting.Job")
        val jobMutableMap: MutableMap<String, Int> = jobSection?.getValues(false)
            ?.filterValues { it is Int }
            ?.mapValues { it.value as Int }
            ?.toMutableMap()
            ?: mutableMapOf()
        gameJobCount = jobMutableMap


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
        Chest().breakALL(chestLocations)

        broadcast(Component.text("リセット処理が完了しました！"))
        processAdd()


    }
    private fun removeEntities(world: World) {
        // ワールド内のすべてのエンティティを取得
        val entities = world.entities

        entities.forEach { entity ->
            when (entity) {
                is AreaEffectCloud -> {
                    // ちぇすとのもやもや
                    entity.remove()
                }
                is ArmorStand -> {
                    // "AooniJinrou" のタグを持つ ArmorStand を削除
                    if (entity.scoreboardTags.contains("AooniJinrou")) {
                        entity.remove()
                    }
                }
            }
        }
    }
}