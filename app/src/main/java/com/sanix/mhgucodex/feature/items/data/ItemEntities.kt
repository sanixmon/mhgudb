package com.sanix.mhgucodex.feature.items.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
@Entity(tableName = "item_sources")
data class ItemSourceEntity(
    @PrimaryKey val id: Long,
    val itemId: Long,
    val source: String,
    val detail: String,
)

@Serializable
@Entity(tableName = "item_usages")
data class ItemUsageEntity(
    @PrimaryKey val id: Long,
    val itemId: Long,
    val usage: String,
)
