package com.sanix.mhgucodex.feature.weapons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.sanix.mhgucodex.core.designsystem.component.StatBar
import com.sanix.mhgucodex.feature.weapons.domain.Weapon

@Composable
fun WeaponDetailScreenRoute(
    onBack: () -> Unit,
    viewModel: WeaponDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeaponDetailScreen(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeaponDetailScreen(
    uiState: WeaponDetailUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weapon") },
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

            uiState.weapon == null -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                ErrorState(
                    title = "Weapon not found",
                    message = "This placeholder record is not part of the demo seed.",
                )
            }

            else -> WeaponDetailContent(
                weapon = uiState.weapon,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
        }
    }
}

@Composable
private fun WeaponDetailContent(
    weapon: Weapon,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = weapon.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "${weapon.type} · Rarity ${weapon.rarity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = weapon.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        item { SectionHeader(title = "Stats") }
        item {
            DetailRow(label = "Attack", value = weapon.attack.toString())
            DetailRow(label = "Affinity", value = "${weapon.affinity}%")
            DetailRow(
                label = "Element",
                value = if (weapon.elementValue == 0) {
                    weapon.element
                } else {
                    "${weapon.element} ${weapon.elementValue}"
                },
            )
            DetailRow(label = "Slots", value = weapon.slots)
            DetailRow(label = "Sharpness", value = "${weapon.sharpness}%")
        }

        item {
            RowElement(
                element = weapon.element,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        item {
            SectionHeader(title = "Sharpness")
            StatBar(
                label = "Base sharpness",
                value = weapon.sharpness,
                maxValue = 100,
                valueLabel = "${weapon.sharpness}%",
            )
        }

        item { SectionHeader(title = "Upgrade path") }
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = weapon.treeName,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = weapon.upgradeFrom ?: "Base of this placeholder tree",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowElement(
    element: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        ElementIcon(element = element)
        Text(
            text = element,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
