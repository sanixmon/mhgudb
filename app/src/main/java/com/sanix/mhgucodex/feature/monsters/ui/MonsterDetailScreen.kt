package com.sanix.mhgucodex.feature.monsters.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanix.mhgucodex.R
import com.sanix.mhgucodex.core.designsystem.component.DemoLabel
import com.sanix.mhgucodex.core.designsystem.component.DetailRow
import com.sanix.mhgucodex.core.designsystem.component.ElementIcon
import com.sanix.mhgucodex.core.designsystem.component.ErrorState
import com.sanix.mhgucodex.core.designsystem.component.LoadingState
import com.sanix.mhgucodex.core.designsystem.component.SectionHeader
import com.sanix.mhgucodex.feature.monsters.domain.MonsterDetail

@Composable
fun MonsterDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: MonsterDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MonsterDetailScreen(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonsterDetailScreen(
    uiState: MonsterDetailUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monster") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
                actions = { DemoLabel() },
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
                    title = "Monster not found",
                    message = "This placeholder record is not part of the demo seed.",
                )
            }

            else -> MonsterDetailContent(
                detail = uiState.detail,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        }
    }
}

@Composable
private fun MonsterDetailContent(
    detail: MonsterDetail,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Card {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pets),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = detail.monster.name,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = "${detail.monster.type} · ${if (detail.monster.isLarge) "Large" else "Small"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Text(
                text = detail.monster.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item { SectionHeader(title = "Weaknesses") }
        items(detail.weaknesses) { weakness ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElementIcon(element = weakness.name)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = weakness.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${weakness.kind} · ${weakness.value}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        item { SectionHeader(title = "Hitzones") }
        item {
            Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Row {
                    TableCell(text = "Part", width = 120.dp, header = true)
                    listOf("Slash", "Impact", "Shot", "Fire", "Water", "Thunder", "Ice", "Dragon")
                        .forEach { label -> TableCell(text = label, width = 64.dp, header = true) }
                }
                HorizontalDivider()
                detail.hitzones.forEach { hitzone ->
                    Row {
                        TableCell(text = hitzone.part, width = 120.dp)
                        TableCell(text = hitzone.slash.toString(), width = 64.dp)
                        TableCell(text = hitzone.impact.toString(), width = 64.dp)
                        TableCell(text = hitzone.shot.toString(), width = 64.dp)
                        TableCell(text = hitzone.fire.toString(), width = 64.dp)
                        TableCell(text = hitzone.water.toString(), width = 64.dp)
                        TableCell(text = hitzone.thunder.toString(), width = 64.dp)
                        TableCell(text = hitzone.ice.toString(), width = 64.dp)
                        TableCell(text = hitzone.dragon.toString(), width = 64.dp)
                    }
                    HorizontalDivider()
                }
            }
        }

        item { SectionHeader(title = "Drops by rank") }
        items(detail.drops) { drop ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = drop.itemName, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${drop.rank} · ${drop.method}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${drop.percent}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        item { SectionHeader(title = "Habitats") }
        items(detail.habitats) { habitat ->
            DetailRow(label = habitat.location, value = "Areas ${habitat.areas}")
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: Dp,
    header: Boolean = false,
) {
    Text(
        text = text,
        modifier = Modifier.width(width).padding(horizontal = 6.dp, vertical = 8.dp),
        style = if (header) {
            MaterialTheme.typography.labelSmall
        } else {
            MaterialTheme.typography.bodySmall
        },
        color = if (header) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.onSurface
        },
    )
}
