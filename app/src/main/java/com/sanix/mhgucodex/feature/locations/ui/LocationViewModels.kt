package com.sanix.mhgucodex.feature.locations.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.LocationDetailRoute
import com.sanix.mhgucodex.feature.locations.data.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LocationListViewModel @Inject constructor(
    repository: LocationRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(LocationFilter.ALL)

    val uiState: StateFlow<LocationListUiState> = combine(
        query,
        filter,
        repository.observeLocations(),
    ) { currentQuery, currentFilter, locations ->
        val filtered = locations.filter { location ->
            val matchesQuery = currentQuery.isBlank() ||
                location.name.contains(currentQuery, ignoreCase = true) ||
                location.mapName.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                LocationFilter.ALL -> true
                LocationFilter.FOREST -> location.terrain == "Forest"
                LocationFilter.SNOW -> location.terrain == "Snow"
                LocationFilter.DESERT -> location.terrain == "Desert"
            }
            matchesQuery && matchesFilter
        }
        LocationListUiState(
            query = currentQuery,
            filter = currentFilter,
            locations = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LocationListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: LocationFilter) {
        filter.value = value
    }
}

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: LocationRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<LocationDetailRoute>()

    val uiState: StateFlow<LocationDetailUiState> = repository
        .observeLocationDetail(route.locationId)
        .map { detail -> LocationDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LocationDetailUiState(),
        )
}
