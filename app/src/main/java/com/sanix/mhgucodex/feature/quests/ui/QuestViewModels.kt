package com.sanix.mhgucodex.feature.quests.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.QuestDetailRoute
import com.sanix.mhgucodex.feature.quests.data.QuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class QuestListViewModel @Inject constructor(
    repository: QuestRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(QuestFilter.ALL)

    val uiState: StateFlow<QuestListUiState> = combine(
        query,
        filter,
        repository.observeQuests(),
    ) { currentQuery, currentFilter, quests ->
        val filtered = quests.filter { quest ->
            val matchesQuery = currentQuery.isBlank() ||
                quest.name.contains(currentQuery, ignoreCase = true) ||
                quest.objective.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                QuestFilter.ALL -> true
                QuestFilter.VILLAGE -> quest.type == "Village"
                QuestFilter.HUB -> quest.type == "Hub"
                QuestFilter.EVENT -> quest.type == "Event"
                QuestFilter.ARENA -> quest.type == "Arena"
            }
            matchesQuery && matchesFilter
        }
        QuestListUiState(
            query = currentQuery,
            filter = currentFilter,
            quests = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuestListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: QuestFilter) {
        filter.value = value
    }
}

@HiltViewModel
class QuestDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: QuestRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<QuestDetailRoute>()

    val uiState: StateFlow<QuestDetailUiState> = repository
        .observeQuestDetail(route.questId)
        .map { detail -> QuestDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = QuestDetailUiState(),
        )
}
