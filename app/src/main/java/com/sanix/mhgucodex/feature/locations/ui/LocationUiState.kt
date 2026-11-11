package com.sanix.mhgucodex.feature.locations.ui

import com.sanix.mhgucodex.feature.locations.domain.Location
import com.sanix.mhgucodex.feature.locations.domain.LocationDetail

enum class LocationFilter(val label: String) {
    ALL("All"),
    FOREST("Forest"),
    SNOW("Snow"),
    DESERT("Desert"),
    VOLCANO("Volcano"),
}

data class LocationListUiState(
    val query: String = "",
    val filter: LocationFilter = LocationFilter.ALL,
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = true,
)

data class LocationDetailUiState(
    val detail: LocationDetail? = null,
    val isLoading: Boolean = true,
)
