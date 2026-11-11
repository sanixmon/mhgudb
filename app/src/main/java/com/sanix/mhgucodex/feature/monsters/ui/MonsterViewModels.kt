package com.sanix.mhgucodex.feature.monsters.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanix.mhgucodex.core.navigation.MonsterDetailRoute
import com.sanix.mhgucodex.feature.monsters.data.MonsterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.navigation.toRoute

@HiltViewModel
class MonsterListViewModel @Inject constructor(
    repository: MonsterRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(MonsterFilter.ALL)

    val uiState: StateFlow<MonsterListUiState> = combine(
        query,
        filter,
        repository.observeMonsters(),
    ) { currentQuery, currentFilter, monsters ->
        val filtered = monsters.filter { monster ->
            val matchesQuery = currentQuery.isBlank() ||
                monster.name.contains(currentQuery, ignoreCase = true) ||
                monster.type.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                MonsterFilter.ALL -> true
                MonsterFilter.LARGE -> monster.isLarge
                MonsterFilter.SMALL -> !monster.isLarge
            }
            matchesQuery && matchesFilter
        }
        MonsterListUiState(
            query = currentQuery,
            filter = currentFilter,
            monsters = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MonsterListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: MonsterFilter) {
        filter.value = value
    }
}

@HiltViewModel
class MonsterDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: MonsterRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MonsterDetailRoute>()

    val uiState: StateFlow<MonsterDetailUiState> = repository
        .observeMonsterDetail(route.monsterId)
        .map { detail -> MonsterDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MonsterDetailUiState(),
        )
}
