package com.sanix.mhgucodex.feature.quests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanix.mhgucodex.R
import com.sanix.mhgucodex.core.designsystem.component.DetailRow
import com.sanix.mhgucodex.core.designsystem.component.ErrorState
import com.sanix.mhgucodex.core.designsystem.component.LoadingState
import com.sanix.mhgucodex.core.designsystem.component.SectionHeader
import com.sanix.mhgucodex.feature.quests.domain.QuestDetail

@Composable
fun QuestDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: QuestDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuestDetailScreen(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestDetailScreen(
    uiState: QuestDetailUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quest") },
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
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                LoadingState()
            }

            uiState.detail == null -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                ErrorState(
                    title = "Quest not found",
                    message = "This record is not in the database.",
                )
            }

            else -> QuestDetailContent(
                detail = uiState.detail,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        }
    }
}

@Composable
private fun QuestDetailContent(
    detail: QuestDetail,
    modifier: Modifier = Modifier,
) {
    val quest = detail.quest
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = quest.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "${quest.type} · ${quest.rank}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item { SectionHeader(title = "Objective") }
        item {
            Text(text = quest.objective, style = MaterialTheme.typography.bodyLarge)
            DetailRow(label = "Map", value = quest.locationName)
            DetailRow(label = "Time limit", value = "${quest.timeLimit} min")
            DetailRow(label = "Reward", value = "${quest.rewardZenny}z")
        }

        item { SectionHeader(title = "Monsters") }
        if (detail.monsters.isEmpty()) {
            item {
                Text(
                    text = "No monsters recorded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(detail.monsters) { monster ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pets),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = monster.monsterName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
        }

        item { SectionHeader(title = "Rewards") }
        items(detail.rewards) { reward ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = reward.itemName, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = reward.slot,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${reward.percent}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
