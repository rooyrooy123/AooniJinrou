package me.rooyrooy.aooniJinrou.job.aooni

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag


class AooniStick : Listener {
    @EventHandler
    fun onBlazeRod(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        val item = event.item ?: return
        if (item.type == Material.BLAZE_ROD) {
            // アクションの種類を判定
            when (event.action) {
                org.bukkit.event.block.Action.RIGHT_CLICK_AIR,
                org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK -> {
                    val player = event.player
                    // ブレイズロッドの右クリックが検知された場合の処理
                    if (player.inventory.helmet?.type != Material.PLAYER_HEAD) {
                        val meta = item.itemMeta ?: return
                        meta.addEnchant(Enchantment.LUCK, 1, true)
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        meta.displayName(Component.text("§1§l§n青鬼の杖§4§n(右クリックで変身を解除)")) // 名前を青色で設定
                        // メタデータを更新
                        item.itemMeta = meta
                        AooniTransform(player).start()
                    } else {
                        val meta = item.itemMeta ?: return
                        meta.removeEnchant(Enchantment.LUCK)
                        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
                        item.itemMeta = meta
                        AooniTransform(player).finish()
                    }
                }

                else -> {
                    // 無視
                }
            }
        }
    }
}
