package me.rooyrooy.aooniJinrou.game

import kotlinx.coroutines.*
import me.rooyrooy.aooniJinrou.Items
import me.rooyrooy.aooniJinrou.gameHideBallCount
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent

class HideBall : Listener{
    private val scope = CoroutineScope(Dispatchers.Default)
    @EventHandler
    fun onHideItem(event: PlayerItemHeldEvent){
        val player = event.player
        val inventory = player.inventory
        val mainHandSlot = event.newSlot
        val mainHandItem = inventory.getItem(mainHandSlot)
        if (gameHideBallCount[player]!! <= 0){
            player.sendMessage("§4隠し玉の持ち時間がありません！")
            return
        }
        if (mainHandItem?.type == Items.HIDEBALL.toItemStack().type) {
            var placed = false

            // 他のホットバーの空きスロットに火薬を設置
            for (slot in 0..8) {
                if (slot != mainHandSlot && inventory.getItem(slot) == null) {
                    inventory.setItem(slot, Items.HIDEBALL_OFF.toItemStack())
                    placed = true
                }
            }
            if (!placed) {
                player.sendMessage("§cホットバーに空きがありません！隠し玉解除用アイテムを付与するホットバースロットを用意してください。")
                return
            }
            player.inventory.remove(mainHandItem)
            startCountDown(player)
        } else if (mainHandItem?.type == Items.HIDEBALL_OFF.toItemStack().type) {
            stop(player)
        }
    }
    private fun startCountDown(player: Player){
        scope.launch {
            while (player.inventory.containsAtLeast(Items.HIDEBALL_OFF.toItemStack(),1)) {
                if (gameHideBallCount[player]!! > 0) {
                    var count = gameHideBallCount[player] ?: 0
                    count -= 1
                    gameHideBallCount[player] = count
                }else{
                    scope.cancel()
                    player.sendMessage("§4隠し玉持ち時間がありません！")
                    stop(player)
                }
                delay(50L) // 50L = 1TICKだから 300 = 15秒
            }
        }
    }
    private fun stop(player: Player){
        player.inventory.remove(Items.HIDEBALL_OFF.toItemStack())
        player.inventory.addItem(Items.HIDEBALL.toItemStack())
    }
}