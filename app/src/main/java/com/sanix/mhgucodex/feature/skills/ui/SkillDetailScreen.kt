package com.sanix.mhgucodex.feature.skills.ui

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
import com.sanix.mhgucodex.core.designsystem.component.ErrorState
import com.sanix.mhgucodex.core.designsystem.component.LoadingState
import com.sanix.mhgucodex.core.designsystem.component.SectionHeader
import com.sanix.mhgucodex.feature.skills.domain.SkillDetail

@Composable
fun SkillDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: SkillDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SkillDetailScreen(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailScreen(
    uiState: SkillDetailUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skill") },
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
                    title = "Skill not found",
                    message = "This record is not in the database.",
                )
            }

            else -> SkillDetailContent(
                detail = uiState.detail,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        }
    }
}

@Composable
private fun SkillDetailContent(
    detail: SkillDetail,
    modifier: Modifier = Modifier,
) {
    val skill = detail.skill
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = skill.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "${skill.treeName} tree · ${skill.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Max points: +${skill.maxPoints}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
                text = skill.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item { SectionHeader(title = "Point thresholds") }
        items(detail.levels) { level ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = if (level.points >= 0) "+${level.points}" else level.points.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp),
                )
                Text(
                    text = level.effect,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item { SectionHeader(title = "Armor that gives it") }
        if (detail.armorSources.isEmpty()) {
            item {
                Text(
                    text = "No armor sources recorded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(detail.armorSources) { source ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = source.armorName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "+${source.points}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        item { SectionHeader(title = "Decorations that give it") }
        if (detail.decorationSources.isEmpty()) {
            item {
                Text(
                    text = "No decoration sources recorded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(detail.decorationSources) { source ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = source.decorationName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "+${source.points}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
