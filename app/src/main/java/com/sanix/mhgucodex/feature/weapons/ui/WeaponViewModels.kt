package com.sanix.mhgucodex.feature.weapons.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.WeaponDetailRoute
import com.sanix.mhgucodex.feature.weapons.data.WeaponRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class WeaponListViewModel @Inject constructor(
    repository: WeaponRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(WeaponFilter.ALL)

    val uiState: StateFlow<WeaponListUiState> = combine(
        query,
        filter,
        repository.observeWeapons(),
    ) { currentQuery, currentFilter, weapons ->
        val filtered = weapons.filter { weapon ->
            val matchesQuery = currentQuery.isBlank() ||
                weapon.name.contains(currentQuery, ignoreCase = true) ||
                weapon.type.contains(currentQuery, ignoreCase = true)
            val isRanged = weapon.type in RANGED_TYPES
            val matchesFilter = when (currentFilter) {
                WeaponFilter.ALL -> true
                WeaponFilter.MELEE -> !isRanged
                WeaponFilter.RANGED -> isRanged
            }
            matchesQuery && matchesFilter
        }
        WeaponListUiState(
            query = currentQuery,
            filter = currentFilter,
            weapons = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeaponListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: WeaponFilter) {
        filter.value = value
    }

    private companion object {
        val RANGED_TYPES = setOf("Bow", "Heavy Bowgun", "Light Bowgun")
    }
}

@HiltViewModel
class WeaponDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: WeaponRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<WeaponDetailRoute>()

    val uiState: StateFlow<WeaponDetailUiState> = repository
        .observeWeapon(route.weaponId)
        .map { weapon -> WeaponDetailUiState(weapon = weapon, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeaponDetailUiState(),
        )
}
