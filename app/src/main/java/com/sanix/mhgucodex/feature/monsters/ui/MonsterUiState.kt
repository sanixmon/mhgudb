package com.sanix.mhgucodex.feature.monsters.ui

import com.sanix.mhgucodex.feature.monsters.domain.Monster
import com.sanix.mhgucodex.feature.monsters.domain.MonsterDetail

enum class MonsterFilter(val label: String) {
    ALL("All"),
    LARGE("Large"),
    SMALL("Small"),
}

data class MonsterListUiState(
    val query: String = "",
    val filter: MonsterFilter = MonsterFilter.ALL,
    val monsters: List<Monster> = emptyList(),
    val isLoading: Boolean = true,
)

data class MonsterDetailUiState(
    val detail: MonsterDetail? = null,
    val isLoading: Boolean = true,
)
