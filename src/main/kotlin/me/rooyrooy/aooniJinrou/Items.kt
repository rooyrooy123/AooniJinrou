package me.rooyrooy.aooniJinrou

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


enum class Items(private val displayName: String, private val lore: List<String>, private val material: Material) {
    HIDEBALL("§f[§9§l§n隠し玉§f]§b§l手に持つと§f§l透明化",
        listOf("§c§l手に持つだけで、§f§l透明化します。",
            "§7別スロットに解除用アイテムが付与されるので、",
            "§7そのアイテムを手に持つと隠し玉を解除。",
            "§7※ホットバーに空きがないと、発動不可。",
            "§b隠し玉の持ち時間は画面中央に表示。"),

        Material.ENDER_EYE),
    HIDEBALL_OFF("§f[§c§l§n隠し玉解除§f]§b§l手に持つと§f§l透明化§4解除",
        listOf("§c§l手に持つだけで、§4透明化を解除します。",
            "§b隠し玉の持ち時間は画面中央に表示。"),
        Material.MUSIC_DISC_13),

    KEY_UNDERFLOOR_PARTS("§e§l地下室の鍵の§e§l§n欠片",
        listOf("§7本館1階～4階のチェストの鍵の欠片を",
            "§7すべて集めると、§9地下室の鍵§e（木の感圧版）§7に！"),
        Material.OAK_BUTTON),
    KEY_TOPFLOOR_PARTS("§9§l最上階の鍵の§9§l§n欠片",
        listOf("§7本館地下1階のチェストの鍵の欠片を",
            "§7すべて集めると、§9最上階への鍵§e（青羊毛）§7に！"),
        Material.BLUE_CARPET),
    KEY_SILVER("§7§l§n銀の鍵",
        listOf("§6§l§n金の鍵§bも取得すると、",
            "§1§l§n青鬼の鍵§bを入手できます。",
            "§cヒント§f➤",
            "§6§l§n金の鍵§bは1階より上のチェスト(ランダム1つ)にあります！"),
        Material.IRON_INGOT),
    KEY_GOLD("§6§l§n金の鍵",
        listOf("§7§l§n銀の鍵§bも取得すると、",
            "§1§l§n青鬼の鍵§bを入手できます。",
            "§cヒント§f➤",
            "§7§l§n銀の鍵§bは最上階にあります！"),
        Material.GOLD_INGOT),
    KEY_AOONI("§1§l§n青鬼の鍵",
        listOf("§b館の脱出に使用する鍵。",
            "§e脱出場所の看板を右クリックで脱出します。",
            "§7残り120秒から使用可能！"),
        Material.DIAMOND),
    KEY_WARNING_ALREADY("§4§l※§cここの§e鍵の欠片§cは取得済みです",
        listOf("§7未開封のチェストに鍵の欠片があります"),
        Material.STRUCTURE_VOID),
    EQUIPMENT_WARNING("§4§l装備取得不可",
        listOf("§7もやもやはある間は装備の取得不可"),
        Material.STRUCTURE_VOID);


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