package me.rooyrooy.aooniJinrou.setting

import me.rooyrooy.aooniJinrou.AooniJinrou
import me.rooyrooy.aooniJinrou.JobInfo
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class GuiButton(
    private val item: Material,
    private val setting: String, //設定名
    private var result: Int, //Config設定値
) : AbstractItem() {
    private val plugin: JavaPlugin = JavaPlugin.getPlugin(AooniJinrou::class.java) //CONFIG読み込み、書き込み用
    private var job = ""
    init {
        if (setting.contains("Job.")){
            job = setting.replace("Job.","")
        }
    }
    override fun getItemProvider(): ItemProvider {
        var resultConfig = result
        if (resultConfig < 0){
            resultConfig = 0
        }
        updateConfig("AooniJinrou.Setting.${setting}",resultConfig)
        var outputItem = item
        var outputAmount = result
        if (result < 1){
            outputAmount = 0
            result = 1
            outputItem = Material.BARRIER
        }
        return ItemBuilder(outputItem)
            .setAmount(result)
            .setDisplayName("§a§l§n設定項目➤${setting} §2§l== §e§l§n${outputAmount}")
            .addLoreLines(JobInfo.valueOf(job).displayName)
            .addLoreLines("§3§l右クリック➤§b§l+1")
            .addLoreLines("§c§l左クリック➤§4§l+1")
            .addLoreLines("§3§l§nシフト右クリック➤§b§l+10")
            .addLoreLines("§c§l§nシフト左クリック➤§4§l-10")
    }
    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {

        result += if (clickType.isLeftClick) { //LEFT
            if (clickType.isShiftClick) 10 else 1 //しふとちゅうかどうかで 1 OR 10
        } else { //RIGHT
            if (clickType.isShiftClick) -10 else -1
        }
        notifyWindows()
    }
    private fun updateConfig(key: String, value: Any) {
        // config.ymlを更新
        plugin.config.set(key, value)
        plugin.saveConfig()
    }


}