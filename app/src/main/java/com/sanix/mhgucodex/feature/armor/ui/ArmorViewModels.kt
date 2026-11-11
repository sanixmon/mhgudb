package com.sanix.mhgucodex.feature.armor.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.ArmorDetailRoute
import com.sanix.mhgucodex.feature.armor.data.ArmorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ArmorListViewModel @Inject constructor(
    repository: ArmorRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(ArmorFilter.ALL)

    val uiState: StateFlow<ArmorListUiState> = combine(
        query,
        filter,
        repository.observeArmor(),
    ) { currentQuery, currentFilter, armor ->
        val filtered = armor.filter { piece ->
            val matchesQuery = currentQuery.isBlank() ||
                piece.name.contains(currentQuery, ignoreCase = true) ||
                piece.setName.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                ArmorFilter.ALL -> true
                ArmorFilter.HEAD -> piece.piece == "Head"
                ArmorFilter.BODY -> piece.piece == "Body"
                ArmorFilter.ARMS -> piece.piece == "Arms"
                ArmorFilter.WAIST -> piece.piece == "Waist"
                ArmorFilter.LEGS -> piece.piece == "Legs"
            }
            matchesQuery && matchesFilter
        }
        ArmorListUiState(
            query = currentQuery,
            filter = currentFilter,
            armor = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ArmorListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: ArmorFilter) {
        filter.value = value
    }
}

@HiltViewModel
class ArmorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ArmorRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ArmorDetailRoute>()

    val uiState: StateFlow<ArmorDetailUiState> = repository
        .observeArmorDetail(route.armorId)
        .map { detail -> ArmorDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArmorDetailUiState(),
        )
}
