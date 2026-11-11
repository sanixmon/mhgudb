package com.sanix.mhgucodex.feature.skills.ui

import com.sanix.mhgucodex.feature.skills.domain.Skill
import com.sanix.mhgucodex.feature.skills.domain.SkillDetail

enum class SkillFilter(val label: String) {
    ALL("All"),
    OFFENSE("Offense"),
    DEFENSE("Defense"),
    UTILITY("Utility"),
}

data class SkillListUiState(
    val query: String = "",
    val filter: SkillFilter = SkillFilter.ALL,
    val skills: List<Skill> = emptyList(),
    val isLoading: Boolean = true,
)

data class SkillDetailUiState(
    val detail: SkillDetail? = null,
    val isLoading: Boolean = true,
)
