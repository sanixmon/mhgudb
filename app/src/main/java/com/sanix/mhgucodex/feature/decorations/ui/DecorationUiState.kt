package com.sanix.mhgucodex.feature.decorations.ui

import com.sanix.mhgucodex.feature.decorations.domain.Decoration
import com.sanix.mhgucodex.feature.decorations.domain.DecorationDetail

enum class DecorationFilter(val label: String) {
    ALL("All"),
    ONE("1-slot"),
    TWO("2-slot"),
    THREE("3-slot"),
}

data class DecorationListUiState(
    val query: String = "",
    val filter: DecorationFilter = DecorationFilter.ALL,
    val decorations: List<Decoration> = emptyList(),
    val isLoading: Boolean = true,
)

data class DecorationDetailUiState(
    val detail: DecorationDetail? = null,
    val isLoading: Boolean = true,
)
