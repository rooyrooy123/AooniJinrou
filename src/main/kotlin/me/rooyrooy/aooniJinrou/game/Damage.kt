package me.rooyrooy.aooniJinrou.game

import de.tr7zw.nbtapi.NBTEntity
import me.rooyrooy.aooniJinrou.*
import me.rooyrooy.aooniJinrou.chest.ChestParticle
import me.rooyrooy.aooniJinrou.job.JobInfo
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

class Damage: Listener{

    @EventHandler
    fun onOtherDamage(event: EntityDamageEvent) {
        if (!gameStart) return
        val damageType = event.cause

        // 落下ダメージと窒息ダメージを無効化
        if (damageType == EntityDamageEvent.DamageCause.FALL || damageType == EntityDamageEvent.DamageCause.SUFFOCATION) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent){
        val damager = event.damager
        val victim = event.entity
        if (!gameStart) return
        if (victim !is Player) return
        if (gameJobList[victim] != "HUNTER") {
            if (damager is Arrow) {
                val attacker = damager.shooter
                if (attacker is Player) { // attacker victimが人間かつ弓で攻撃された場合
                    if (gameJobList[attacker] == "HUNTER") { //狩人かの確認
                        victim.health = 0.0
                        return
                    }
                }
            } else if (damager is Player) {
                if (gameJobList[damager] == "AOONI") { // 青鬼の攻撃だった場合
                    if (damager.equipment.helmet.type != Material.AIR) {
                        val killLimit = gameAooniKillLimit[damager] ?:0
                        if (killLimit >= 2){
                            damager.sendMessage("§41度の変身で3回以上の攻撃はできません！")
                            event.isCancelled = true
                            return
                        }
                        victim.health = 0.0
                        return
                    }
                }
            }
        }else {
            if (damager is Arrow) {
                val attacker = damager.shooter
                if (attacker is Player) { // attacker victimが人間かつ弓で攻撃された場合
                    if (gameJobList[attacker] == "AOONI"){ //青鬼の弓で攻撃された場合
                        if (victim.health <= 2.0){
                            victim.health = 0.0
                            return
                        }else{
                            event.setDamage(19.5)
                            victim.sendMessage("§1§l青鬼の弓に射抜かれました。§4§l残り1回の攻撃で青鬼の勝利となります。")
                            attacker.sendMessage("§b青鬼の弓で狩人にダメージを与えました！もう一度青鬼の弓を被弾すると撃破でき、青鬼の勝利となります！")
                            return
                        }
                    }
                }
            }
        }


        //上記の条件を満たさないダメージは無効
        event.isCancelled = true
        return

    }
    @EventHandler
    fun onDeath(event: PlayerDeathEvent){
        val victim = event.entity
        val attacker = victim.killer
        if (attacker !is Player) return
        event.isCancelled = true
        victim.gameMode = GameMode.SPECTATOR
        deathArmorStandSpawn(victim)
        val attackerJob = gameJobList[attacker] ?: return
        val victimJob = gameJobList[victim] ?: return
        if (victimJob == "HUNTER"){
            println("青鬼勝利 by 狩人死亡")
            Bukkit.broadcast(Component.text("§c§l狩人の${victim.name}が§1§l§n青鬼の弓§bを2度被弾し、§4§l§n死亡した。§bよって、§1§l§n青鬼の§6§l§n勝利§1です！"))
            return
        }
        if (attackerJob == "HUNTER") {
            onHunterKill(attacker, victim,victimJob)
        } else if (attackerJob == "AOONI") {
            onAooniKill(attacker,victim)
        }
    }
    @EventHandler
    fun onCheckDeathArmorStand(event: PlayerInteractEvent) {
        val player = event.player
        val targetArmorStand = getDeathArmorStand(player)

        if (targetArmorStand != null) {
            val customName = targetArmorStand.customName ?: "名前が設定されていません"
            player.sendMessage("§4§l視点先の死体§8§l[§e§l§n$customName§8§l]")
        }
    }

    // 視線方向にあるMarkerタグを持つアーマースタンドを取得
    private fun getDeathArmorStand(player: Player): ArmorStand? {
        val eyeLocation: Location = player.eyeLocation
        val direction: Vector = eyeLocation.direction.normalize()
        val world = player.world
        val maxDistance = 10.0
        val stepSize = 0.1//べくとるすすめる
        val hitBox = 1.7
        var distance = 0.0
// サーバー内の全エンティティを取得
        val entities = world.entities
        while (distance <= maxDistance) {
            // 今の座標を
            val point = eyeLocation.clone().add(direction.clone().multiply(distance))

            // すべてのエンティティをチェック
            for (entity in entities) {
                if (entity is ArmorStand && entity.isMarker) {
                    val tags = entity.scoreboardTags

                    if (tags.contains("death")) {
                        // エンティティの位置と現在の座標の距離をチェック
                        if (entity.location.distanceSquared(point) < hitBox * hitBox) {
                            return entity
                        }
                    }
                }
            }
            //vectorの 距離を進める
            distance += stepSize
        }
        return null
    }

    private fun deathArmorStandSpawn(player: Player){
        val location = player.location
        val armorStand = location.world?.spawnEntity(location, EntityType.ARMOR_STAND) as? ArmorStand ?: return

        val nbtEntity = NBTEntity(armorStand)

        nbtEntity.setByte("NoGravity", 1.toByte())
        nbtEntity.setByte("Invulnerable", 1.toByte())
        nbtEntity.setByte("Marker", 1.toByte())


        nbtEntity.setString("CustomName", """{"text":"${player.name}"}""")
        //nbtEntity.setBoolean("CustomNameVisible", true)
        // Tags を設定
        val tags = nbtEntity.getStringList("Tags") ?: mutableListOf()
        tags.add(player.name)
        tags.add("death")
        tags.add("AooniJinrou")
        nbtEntity.setObject("Tags", tags)
        // プレイヤーの頭を装備
        val skullItem = ItemStack(org.bukkit.Material.PLAYER_HEAD)
        val meta = skullItem.itemMeta as? org.bukkit.inventory.meta.SkullMeta
        meta?.owningPlayer = Bukkit.getOfflinePlayer(player.name) // オフラインプレイヤーでも動作
        skullItem.itemMeta = meta
        armorStand.equipment.helmet =skullItem

        // NBTのArmorItemsスロットを直接設定
        val armorItems = """
            [{},{},{},{id:"minecraft:player_head",Count:1b,tag:{SkullOwner:"${player.name}"}}]
        """
        nbtEntity.setString("ArmorItems", armorItems)
    }
    private fun onAooniKill(attacker: Player,victim: Player) {
        Bukkit.broadcastMessage(
            "§e「§c${victim.name}§e」§bは§9§l青鬼§bに食べられた。"
        )
        var killLimit = gameAooniKillLimit[attacker] ?: 0
        killLimit ++
        var killCount = gameAooniKillCount[attacker] ?: 0
        killCount ++
        if (killCount % 3 == 0){
            val bow=
                ItemStack(Material.BOW)
            bow.editMeta {  meta ->
            meta.displayName(Component.text("§1§l§n青鬼の弓")) // 名前を設定
            meta.lore(listOf(
                Component.text("§7狩人にのみ攻撃可能"),
                Component.text("§72度被弾すると死亡し、青鬼の勝利")
            ))
            }
            attacker.inventory.addItem(bow)
        }
        gameAooniKillCount[attacker] = killCount
        attacker.sendMessage("§b${victim.name}を倒した！§c1度の変身で2回まで攻撃可能です！")
        attacker.sendMessage("§7§n現在のキル数 ${killLimit}/2")
        gameAooniKillLimit[attacker] = killLimit

    }

    private fun onHunterKill(attacker: Player, victim: Player, victimJob: String) {
        val attackerJob = gameJobList[attacker] ?: return

        Bukkit.broadcastMessage(
            "${JobInfo.valueOf(victimJob).displayName}(${victim.name})§bは" +
                    "${JobInfo.valueOf(attackerJob).displayName}(${attacker.name})§bに射抜かれた。"
        )
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_ARROW_HIT,1.0f,1.0f)
            it.playSound(it.location,Sound.ENTITY_PLAYER_HURT,1.0f,1.0f)


        }

        if (victimJob == "HIROSHI") {
            ChestParticle().durationToHalf()
            Bukkit.broadcast(Component.text("§2狩人§bが§dひろし§bを誤射したので、§4ペナルティを実行します。"))
            Bukkit.broadcast(Component.text("§c青鬼以外に盲目を付与"))
            Bukkit.broadcast(Component.text("§c館内のチェストもやもや継続時間が半減"))
            val blindnessEffect = PotionEffect(PotionEffectType.BLINDNESS, 200, 100)
            blindnessEffect.withParticles(false) // パーティクルを非表示にする
            // プレイヤーに効果を付与
            Bukkit.getOnlinePlayers().forEach {
                if (gameJobList[it] !="AOONI"){
                    it.addPotionEffect(blindnessEffect)
                    it.playSound(it.location,Sound.ENTITY_VILLAGER_NO,1.0f,0.8f)
                }
            }
        }
    }

}