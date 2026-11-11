package com.sanix.mhgucodex.feature.armor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.sanix.mhgucodex.feature.armor.domain.ArmorDetail

@Composable
fun ArmorDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: ArmorDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ArmorDetailScreen(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArmorDetailScreen(
    uiState: ArmorDetailUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Armor") },
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
                    title = "Armor not found",
                    message = "This record is not in the database.",
                )
            }

            else -> ArmorDetailContent(
                detail = uiState.detail,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        }
    }
}

@Composable
private fun ArmorDetailContent(
    detail: ArmorDetail,
    modifier: Modifier = Modifier,
) {
    val armor = detail.armor
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = armor.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "${armor.setName} set · ${armor.piece} · Rarity ${armor.rarity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = armor.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item { SectionHeader(title = "Defense") }
        item {
            DetailRow(label = "Base defense", value = armor.defense.toString())
            DetailRow(label = "Max defense", value = armor.maxDefense.toString())
            DetailRow(label = "Slots", value = armor.slots)
        }

        item { SectionHeader(title = "Elemental resistances") }
        item {
            ResistanceRow(label = "Fire", value = armor.fireRes)
            ResistanceRow(label = "Water", value = armor.waterRes)
            ResistanceRow(label = "Thunder", value = armor.thunderRes)
            ResistanceRow(label = "Ice", value = armor.iceRes)
            ResistanceRow(label = "Dragon", value = armor.dragonRes)
        }

        item { SectionHeader(title = "Skills") }
        if (detail.skills.isEmpty()) {
            item {
                Text(
                    text = "No skills on this piece.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(detail.skills) { skill ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = skill.skillName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (skill.points >= 0) "+${skill.points}" else skill.points.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResistanceRow(
    label: String,
    value: Int,
) {
    DetailRow(
        label = label,
        value = if (value >= 0) "+$value" else value.toString(),
    )
}
