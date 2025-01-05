package me.rooyrooy.aooniJinrou.job.aooni

import com.saicone.rtag.util.SkullTexture
import me.rooyrooy.aooniJinrou.PluginProvider.plugin
import me.rooyrooy.aooniJinrou.gameAooniKillLimit
import me.rooyrooy.aooniJinrou.gameJobList
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class AooniTransform(val player: Player) {
    fun start(){
// 各防具スロットに対応するアイテムを作成
        //     val helmet = createColoredLeatherArmor(Material.LEATHER_HELMET,Color.fromRGB(72, 61, 139))
        if (gameJobList[player] != "HIROSHI"){
            player.sendMessage("§1§l青鬼のみ使用可能")
            return
        }
        val chestPlate = createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, Color.fromRGB(72, 61, 139))
        val leggings = createColoredLeatherArmor(Material.LEATHER_LEGGINGS, Color.fromRGB(72, 61, 139))
        val boots = createColoredLeatherArmor(Material.LEATHER_BOOTS, Color.fromRGB(72, 61, 139))
        val helmet = SkullTexture.getTexturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZkNGEwMGNkYWJiYzZlMGI5ODNkMjRlNjIzM2Q2ZWZmODU1ZTE2NDgwMGYyOGIwOGY2MWYwNzZmYjZiMTdmNCJ9fX0=")
        // プレイヤーに防具を装備させる
        val inventory = player.inventory
        inventory.helmet = helmet
        inventory.chestplate = chestPlate
        inventory.leggings = leggings
        inventory.boots = boots
        invisibilityAdd(player,600)
        gameAooniKillLimit[player] = 0

        player.sendMessage("§4変身しました！")
        player.sendMessage("§b1回の変身で§c§n2人§bまでキルが可能です！")
        player.sendMessage("§b§n杖をもう一度クリックするか、")
        player.sendMessage("§b§n30秒経過すると変身が解除できます。。")
        player.playSound(player.location,Sound.ITEM_ARMOR_EQUIP_DIAMOND,1.0F,1.0F)
        player.playSound(player.location, Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F)

        var timer = 0
        var taskId = Random().nextInt()
        var helmetWearing: Material
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{
            timer ++
            helmetWearing = player.inventory.helmet?.type ?: Material.AIR
            if (helmetWearing != Material.PLAYER_HEAD){
                Bukkit.getScheduler().cancelTask(taskId)
            }
            if (timer >= 30){
                finish()
                Bukkit.getScheduler().cancelTask(taskId)

            }
        },0L,20L)
    }
    fun finish(){
        val inventory = player.inventory
        inventory.helmet = ItemStack(Material.AIR)
        inventory.chestplate = ItemStack(Material.AIR)
        inventory.leggings = ItemStack(Material.AIR)
        inventory.boots = ItemStack(Material.AIR)
        invisibilityRemove(player)
        player.sendMessage("§1§l青鬼§4への変身が終了しました。")
        player.playSound(player.location,Sound.ITEM_ARMOR_EQUIP_DIAMOND,2.0F,0.5F)
        removeBlazeRod()

    }
    private fun removeBlazeRod(){
        val inventory = player.inventory

        // インベントリ内をループしてブレイズロッドを探す
        for (item in inventory.contents) {
            if (item != null && item.type == Material.BLAZE_ROD) {
                if (item.amount > 1) {
                    // スタック内のアイテム数を減らす
                    item.amount -= 1
                } else {
                    // スタックが1個の場合はアイテムを削除
                    inventory.remove(item)
                }
                return
            }
        }
    }
    // 色付きの革防具を作成する関数
    private fun createColoredLeatherArmor(material: Material, color: Color): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta as? LeatherArmorMeta ?: return item
        meta.setColor(color) // 防具の色を設定
        item.itemMeta = meta
        return item
    }

}
fun invisibilityAdd(player: Player,duration: Int){
    val blindnessEffect = PotionEffect(PotionEffectType.INVISIBILITY, duration, 0)
    blindnessEffect.withParticles(false) // パーティクルを非表示にする
    // プレイヤーに効果を付与
    player.addPotionEffect(blindnessEffect)
}
fun invisibilityRemove(player: Player) {
    // プレイヤーから透明化ポーション効果を削除
    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY)
    }
}