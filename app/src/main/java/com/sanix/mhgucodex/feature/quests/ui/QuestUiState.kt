package com.sanix.mhgucodex.feature.quests.ui

import com.sanix.mhgucodex.feature.quests.domain.Quest
import com.sanix.mhgucodex.feature.quests.domain.QuestDetail

enum class QuestFilter(val label: String) {
    ALL("All"),
    VILLAGE("Village"),
    HUB("Hub"),
    EVENT("Event"),
    ARENA("Arena"),
    PERMIT("Permit"),
}

data class QuestListUiState(
    val query: String = "",
    val filter: QuestFilter = QuestFilter.ALL,
    val quests: List<Quest> = emptyList(),
    val isLoading: Boolean = true,
)

data class QuestDetailUiState(
    val detail: QuestDetail? = null,
    val isLoading: Boolean = true,
)
