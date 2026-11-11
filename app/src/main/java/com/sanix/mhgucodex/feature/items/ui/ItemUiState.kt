package com.sanix.mhgucodex.feature.items.ui

import com.sanix.mhgucodex.feature.items.domain.Item
import com.sanix.mhgucodex.feature.items.domain.ItemDetail

enum class ItemFilter(val label: String) {
    ALL("All"),
    MATERIAL("Materials"),
    CONSUMABLE("Consumables"),
    OTHER("Other"),
}

data class ItemListUiState(
    val query: String = "",
    val filter: ItemFilter = ItemFilter.ALL,
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = true,
)

data class ItemDetailUiState(
    val detail: ItemDetail? = null,
    val isLoading: Boolean = true,
)
