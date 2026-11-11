package com.sanix.mhgucodex.feature.weapons.ui

import com.sanix.mhgucodex.feature.weapons.domain.Weapon

enum class WeaponFilter(val label: String) {
    ALL("All"),
    MELEE("Melee"),
    RANGED("Ranged"),
}

data class WeaponListUiState(
    val query: String = "",
    val filter: WeaponFilter = WeaponFilter.ALL,
    val weapons: List<Weapon> = emptyList(),
    val isLoading: Boolean = true,
)

data class WeaponDetailUiState(
    val weapon: Weapon? = null,
    val isLoading: Boolean = true,
)
