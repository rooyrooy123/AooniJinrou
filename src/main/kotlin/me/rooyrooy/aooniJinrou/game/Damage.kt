package me.rooyrooy.aooniJinrou.game

import de.tr7zw.nbtapi.NBTEntity
import me.rooyrooy.aooniJinrou.gameAooniKillLimit
import me.rooyrooy.aooniJinrou.gameJobList
import me.rooyrooy.aooniJinrou.gameStart
import me.rooyrooy.aooniJinrou.job.JobInfo
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

class Damage: Listener{
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
                        if (killLimit >= 3){
                            damager.sendMessage("§41度の変身で3回以上の攻撃はできません！")
                            event.isCancelled = true
                            return
                        }
                        victim.health = 0.0
                        return
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
                    // エンティティの位置と現在の座標の距離をチェック
                    if (entity.location.distanceSquared(point) < hitBox * hitBox) {
                        return entity
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

        if (victimJob == "HIROSHI") {
            Bukkit.broadcastMessage("§2狩人§bが§dひろし§bを誤射したので、§4青鬼以外に盲目を付与")
            val blindnessEffect = PotionEffect(PotionEffectType.BLINDNESS, 200, 0)
            blindnessEffect.withParticles(false) // パーティクルを非表示にする
            // プレイヤーに効果を付与
            victim.addPotionEffect(blindnessEffect)
        }
    }

}