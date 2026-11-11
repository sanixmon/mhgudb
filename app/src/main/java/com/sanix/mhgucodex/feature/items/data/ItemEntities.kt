package com.sanix.mhgucodex.feature.items.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String,
    val rarity: Int,
    val description: String,
    val maxCarry: Int,
    val buyPrice: Int,
    val sellPrice: Int,
)

@Entity(tableName = "item_sources", indices = [Index("itemId")])
data class ItemSourceEntity(
    @PrimaryKey val id: Long,
    val itemId: Long,
    val source: String,
    val detail: String,
)

@Entity(tableName = "item_usages", indices = [Index("itemId")])
data class ItemUsageEntity(
    @PrimaryKey val id: Long,
    val itemId: Long,
    val usage: String,
)
