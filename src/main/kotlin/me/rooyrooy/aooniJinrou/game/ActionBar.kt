package me.rooyrooy.aooniJinrou.game

import kotlinx.coroutines.*
import me.rooyrooy.aooniJinrou.gameHideBallCount
import me.rooyrooy.aooniJinrou.gameStart
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class ActionBar {
    private val scope = CoroutineScope(Dispatchers.Default)
    fun start() {
        scope.launch {
            while(gameStart){
                Bukkit.getOnlinePlayers().forEach {
                    val count = gameHideBallCount[it] ?:0
                    it.sendActionBar(Component.text("隠し玉残り時間:${count}"))
                }
                delay(50L)

            }

        }
    }
}