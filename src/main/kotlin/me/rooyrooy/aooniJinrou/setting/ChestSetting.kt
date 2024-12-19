package me.rooyrooy.aooniJinrou.setting

import de.tr7zw.nbtapi.NBTEntity
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class ChestSetting(
    private val location: Location
){

    fun place(floorInt: Int) {
        val armorStand = location.world?.spawnEntity(location, EntityType.ARMOR_STAND) as? ArmorStand ?: return
        val floor = "Floor${floorInt}"
        // NBTを設定
        val nbtEntity = NBTEntity(armorStand)
        nbtEntity.setBoolean("CustomNameVisible", false)
        nbtEntity.setBoolean("Invulnerable", true)
        nbtEntity.setBoolean("Small", true)
        nbtEntity.setBoolean("Marker", true)
        nbtEntity.setBoolean("Invisible", true)
        // Tags を設定（NBTAPIは文字列リストを直接扱えないため、個別に追加）
        val tags = nbtEntity.getStringList("Tags") ?: mutableListOf()
        tags.add(floor)
        nbtEntity.setObject("Tags", tags)
        // CustomName を設定
        nbtEntity.setString("CustomName", """{"text":"$floor"}""")
    }
    fun check(boolean: Boolean){
        // location のワールドを取得
        val world = location.world ?: return
        // ワールド内のエンティティを走査
        for (entity in world.entities) {
            // ArmorStandかつ名前に "Floor" を含む場合に処理
            if (entity.type == EntityType.ARMOR_STAND && entity is ArmorStand) {
                if (entity.customName?.contains("Floor") == true) {
                    // 頭にエンダーチェストを装備
                    if (boolean) {
                        val headItem = ItemStack(Material.ENDER_CHEST)
                        entity.equipment.helmet = headItem
                        entity.isGlowing = true
                    }else {
                        val headItem = ItemStack(Material.AIR)
                        entity.equipment.helmet = headItem
                        entity.isGlowing = false
                    }
                }
            }
        }
    }

}