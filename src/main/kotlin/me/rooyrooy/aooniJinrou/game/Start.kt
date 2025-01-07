package me.rooyrooy.aooniJinrou.game

import me.rooyrooy.aooniJinrou.*
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType

/**
 * 間違ってダッシュするのを防ぐため、開始TP時に盲目と鈍足をつける
 *
 */
class Start(world: World) {
    init {
        Reset()
        gameWorld = world
        gameStart = true
        //setupChestCount()
    }
}
fun setupChestCount() {
    var floor: Int
    gameKeyTopNeed = 0
    gameKeyUnderNeed = 0
    for (num in -10..10){
        gameChestCount.clear()
    }
    for (entity in gameWorld!!.entities) {
        // ArmorStandかつ名前に "Floor" を含む場合に処理
        if (entity.type == EntityType.ARMOR_STAND && entity is ArmorStand) {
            if (entity.customName?.contains("Floor") == true) {
                val name = entity.name
                floor = name.replace("Floor","").toInt()
                gameChestCount[floor] = gameChestCount[floor]!! + 1
                if (floor >= 1){ //1階~4階
                    gameKeyUnderNeed += 1
                }else if (floor <= -1){
                    gameKeyTopNeed += 1
                }
            }
        }
    }

}