package me.rooyrooy.aooniJinrou

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


enum class Items(private val displayName: String, private val lore: List<String>, private val material: Material) {
    KEY_UNDERFLOOR_PARTS("§e§l地下室の鍵の§e§l§n欠片", listOf("§7本館1階～4階のチェストの鍵の欠片を","§7すべて集めると、§9地下室の鍵§e（木の感圧版）§7に！"), Material.OAK_BUTTON),
    KEY_TOPFLOOR_PARTS("§9§l最上階の鍵の§9§l§n欠片", listOf("§7本館地下1階のチェストの鍵の欠片を","§7すべて集めると、§9最上階への鍵§e（青羊毛）§7に！"), Material.BLUE_CARPET),
    KEY_SILVER("§7§l§n銀の鍵", listOf("§6§l§n金の鍵§bも取得すると、","§1§l§n青鬼の鍵§bを入手できます。","§cヒント§f➤","§6§l§n金の鍵§bは1階より上のチェスト(ランダム1つ)にあります！"),Material.IRON_INGOT),
    KEY_GOLD("§6§l§n金の鍵", listOf("§7§l§n銀の鍵§bも取得すると、","§1§l§n青鬼の鍵§bを入手できます。","§cヒント§f➤","§7§l§n銀の鍵§bは最上階にあります！"),Material.DIAMOND),
    KEY_WARNING_ALREADY("§4§l※§cここの§e鍵の欠片§cは取得済みです", listOf("§7未開封のチェストに鍵の欠片があります"), Material.STRUCTURE_VOID),
    EQUIPMENT_WARNING("§4§l装備取得不可", listOf("§7もやもやはある間は装備の取得不可"), Material.STRUCTURE_VOID);


    // ItemStackを作成するメソッド
    fun toItemStack(): ItemStack {
        val item = ItemStack(material)
        item.editMeta { meta ->
            meta.displayName(Component.text(displayName)) // 名前を設定
            meta.lore(lore.map { Component.text(it) }) // lore を String から Component に変換
        }
        return item
    }
}