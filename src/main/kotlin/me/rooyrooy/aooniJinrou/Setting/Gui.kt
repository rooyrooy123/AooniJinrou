package me.rooyrooy.aooniJinrou.Setting

import me.rooyrooy.aooniJinrou.jobitem
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.window.Window

class SettingGui(
    config: FileConfiguration
){

    private val gui = PagedGui.items()
    init {
        val itemsSection = config.getConfigurationSection("AooniJinrou.Setting")
            ?: throw NullPointerException() //設定取得 例外はnull
        val setting: MutableSet<String> = itemsSection.getKeys(false) //Job Time etc...
        val items = arrayListOf<SettingGuiButton>()
        val border = SimpleItem(ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(""))
        setting.forEach { settingKey -> // settingkey == Job ,Game
            val keySettingSection =
                config.getConfigurationSection("AooniJinrou.Setting.${settingKey}") ?: throw NullPointerException() //etc AooniJinrou.Setting.Job
            var material: Material
            val keys = keySettingSection.getKeys(false) //Etc hiroshi time
            keys.forEach { key ->
                if (settingKey == "Job") {
                    material = Material.valueOf(jobitem[key].toString())
                } else { //JOB設定以外
                    material = Material.WHITE_WOOL
                }
                items.add(SettingGuiButton(material, "${settingKey}.${key}", keySettingSection.getInt(key)))
            }
        }
        gui
            .setStructure(
                "# # # # # # # # #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # # # # # # #"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('#', border)
            .setContent(items as List<xyz.xenondevs.invui.item.Item>)
            .build()
    }


    fun open(player: Player){
        val window = Window.single()
            .setViewer(player)
            .setTitle("§e§l§nShop§r §7§l§n()")
            .setGui(gui)
            .build()
        window.open()
    }
}