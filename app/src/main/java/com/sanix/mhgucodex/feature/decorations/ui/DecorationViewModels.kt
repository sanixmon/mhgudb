package com.sanix.mhgucodex.feature.decorations.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.DecorationDetailRoute
import com.sanix.mhgucodex.feature.decorations.data.DecorationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DecorationListViewModel @Inject constructor(
    repository: DecorationRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(DecorationFilter.ALL)

    val uiState: StateFlow<DecorationListUiState> = combine(
        query,
        filter,
        repository.observeDecorations(),
    ) { currentQuery, currentFilter, decorations ->
        val filtered = decorations.filter { decoration ->
            val matchesQuery = currentQuery.isBlank() ||
                decoration.name.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                DecorationFilter.ALL -> true
                DecorationFilter.ONE -> decoration.slotSize == 1
                DecorationFilter.TWO -> decoration.slotSize == 2
                DecorationFilter.THREE -> decoration.slotSize == 3
            }
            matchesQuery && matchesFilter
        }
        DecorationListUiState(
            query = currentQuery,
            filter = currentFilter,
            decorations = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DecorationListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: DecorationFilter) {
        filter.value = value
    }
}

@HiltViewModel
class DecorationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: DecorationRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<DecorationDetailRoute>()

    val uiState: StateFlow<DecorationDetailUiState> = repository
        .observeDecorationDetail(route.decorationId)
        .map { detail -> DecorationDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DecorationDetailUiState(),
        )
}
