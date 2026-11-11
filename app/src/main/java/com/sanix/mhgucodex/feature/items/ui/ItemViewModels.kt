package com.sanix.mhgucodex.feature.items.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.ItemDetailRoute
import com.sanix.mhgucodex.feature.items.data.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ItemListViewModel @Inject constructor(
    repository: ItemRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(ItemFilter.ALL)

    val uiState: StateFlow<ItemListUiState> = combine(
        query,
        filter,
        repository.observeItems(),
    ) { currentQuery, currentFilter, items ->
        val filtered = items.filter { item ->
            val matchesQuery = currentQuery.isBlank() ||
                item.name.contains(currentQuery, ignoreCase = true) ||
                item.category.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                ItemFilter.ALL -> true
                ItemFilter.MATERIAL -> item.category == "Material" || item.category == "Ore"
                ItemFilter.CONSUMABLE -> item.category == "Consumable"
                ItemFilter.OTHER -> item.category !in setOf("Material", "Ore", "Consumable")
            }
            matchesQuery && matchesFilter
        }
        ItemListUiState(
            query = currentQuery,
            filter = currentFilter,
            items = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ItemListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: ItemFilter) {
        filter.value = value
    }
}

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ItemRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ItemDetailRoute>()

    val uiState: StateFlow<ItemDetailUiState> = repository
        .observeItemDetail(route.itemId)
        .map { detail -> ItemDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ItemDetailUiState(),
        )
}
