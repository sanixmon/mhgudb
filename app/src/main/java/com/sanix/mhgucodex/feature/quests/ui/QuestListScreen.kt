package com.sanix.mhgucodex.feature.quests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanix.mhgucodex.R
import com.sanix.mhgucodex.core.designsystem.component.EmptyState
import com.sanix.mhgucodex.core.designsystem.component.FilterChipRow
import com.sanix.mhgucodex.core.designsystem.component.LoadingState
import com.sanix.mhgucodex.core.designsystem.component.MhguListRow
import com.sanix.mhgucodex.core.designsystem.component.MhguSearchBar

@Composable
fun QuestListScreenRoute(
    onQuestClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: QuestListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuestListScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onQuestClick = onQuestClick,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestListScreen(
    uiState: QuestListUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (QuestFilter) -> Unit,
    onQuestClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quests") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            MhguSearchBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = "Search quests",
            )
            FilterChipRow(
                options = QuestFilter.entries.toList(),
                selected = uiState.filter,
                label = { it.label },
                onSelect = onFilterChange,
            )
            when {
                uiState.isLoading -> LoadingState()
                uiState.quests.isEmpty() -> EmptyState(
                    title = "No quests found",
                    message = "Try another search term or clear the filter.",
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.quests, key = { quest -> quest.id }) { quest ->
                        MhguListRow(
                            title = quest.name,
                            subtitle = "${quest.type} · ${quest.rank}",
                            iconRes = R.drawable.ic_assignment,
                            onClick = { onQuestClick(quest.id) },
                            trailing = quest.locationName,
                        )
                    }
                }
            }
        }
    }
}
