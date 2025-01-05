package me.rooyrooy.aooniJinrou.chest

import org.bukkit.block.Block
import org.bukkit.plugin.Plugin

class BlockMetaData(private val plugin: Plugin) {

    // メタデータをブロックに追加
    fun addMetadata(block: Block, key: String, value: String) {
        block.setMetadata(key, org.bukkit.metadata.FixedMetadataValue(plugin, value))
    }

    // ブロックのメタデータを読み取る
    fun getMetadata(block: Block, key: String): String? {
        if (block.hasMetadata(key)) {
            val metadataValues = block.getMetadata(key)
            if (metadataValues.isNotEmpty()) {
                return metadataValues[0].asString()
            }
        }
        return null
    }

    // メタデータを削除
    fun removeMetadataFromBlock(block: Block, key: String) {
        block.removeMetadata(key, plugin)
    }
}

