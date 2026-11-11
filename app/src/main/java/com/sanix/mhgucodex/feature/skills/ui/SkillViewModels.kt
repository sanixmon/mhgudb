package com.sanix.mhgucodex.feature.skills.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sanix.mhgucodex.core.navigation.SkillDetailRoute
import com.sanix.mhgucodex.feature.skills.data.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SkillListViewModel @Inject constructor(
    repository: SkillRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(SkillFilter.ALL)

    val uiState: StateFlow<SkillListUiState> = combine(
        query,
        filter,
        repository.observeSkills(),
    ) { currentQuery, currentFilter, skills ->
        val filtered = skills.filter { skill ->
            val matchesQuery = currentQuery.isBlank() ||
                skill.name.contains(currentQuery, ignoreCase = true) ||
                skill.treeName.contains(currentQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                SkillFilter.ALL -> true
                SkillFilter.JEWEL -> skill.category == "Jewel"
                SkillFilter.ARMOR_ONLY -> skill.category == "Armor only"
            }
            matchesQuery && matchesFilter
        }
        SkillListUiState(
            query = currentQuery,
            filter = currentFilter,
            skills = filtered,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SkillListUiState(),
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: SkillFilter) {
        filter.value = value
    }
}

@HiltViewModel
class SkillDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SkillRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<SkillDetailRoute>()

    val uiState: StateFlow<SkillDetailUiState> = repository
        .observeSkillDetail(route.skillId)
        .map { detail -> SkillDetailUiState(detail = detail, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SkillDetailUiState(),
        )
}
