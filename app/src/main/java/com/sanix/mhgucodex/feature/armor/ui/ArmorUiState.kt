package com.sanix.mhgucodex.feature.armor.ui

import com.sanix.mhgucodex.feature.armor.domain.Armor
import com.sanix.mhgucodex.feature.armor.domain.ArmorDetail

enum class ArmorFilter(val label: String) {
    ALL("All"),
    HEAD("Head"),
    BODY("Body"),
    ARMS("Arms"),
    WAIST("Waist"),
    LEGS("Legs"),
}

data class ArmorListUiState(
    val query: String = "",
    val filter: ArmorFilter = ArmorFilter.ALL,
    val armor: List<Armor> = emptyList(),
    val isLoading: Boolean = true,
)

data class ArmorDetailUiState(
    val detail: ArmorDetail? = null,
    val isLoading: Boolean = true,
)
