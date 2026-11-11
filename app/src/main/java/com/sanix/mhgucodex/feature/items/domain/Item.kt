package com.sanix.mhgucodex.feature.items.domain

data class Item(
    val id: Long,
    val name: String,
    val category: String,
    val rarity: Int,
    val description: String,
    val maxCarry: Int,
    val buyPrice: Int,
    val sellPrice: Int,
)

data class ItemSource(
    val source: String,
    val detail: String,
)

data class ItemUsage(
    val usage: String,
)

data class ItemDetail(
    val item: Item,
    val sources: List<ItemSource>,
    val usages: List<ItemUsage>,
)
