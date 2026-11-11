package com.sanix.mhgucodex.feature.more.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sanix.mhgucodex.R
import com.sanix.mhgucodex.core.designsystem.component.MhguListRow
import com.sanix.mhgucodex.core.designsystem.component.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onQuestsClick: () -> Unit,
    onSkillsClick: () -> Unit,
    onDecorationsClick: () -> Unit,
    onLocationsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More") },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                MhguListRow(
                    title = "Quests",
                    subtitle = "Village, Hub, Event and Arena quests",
                    iconRes = R.drawable.ic_assignment,
                    onClick = onQuestsClick,
                )
            }
            item {
                MhguListRow(
                    title = "Skills",
                    subtitle = "Skill trees, thresholds and sources",
                    iconRes = R.drawable.ic_stars,
                    onClick = onSkillsClick,
                )
            }
            item {
                MhguListRow(
                    title = "Decorations",
                    subtitle = "Jewels, slot sizes and skill points",
                    iconRes = R.drawable.ic_diamond,
                    onClick = onDecorationsClick,
                )
            }
            item {
                MhguListRow(
                    title = "Locations",
                    subtitle = "Maps, monsters and gathering points",
                    iconRes = R.drawable.ic_map,
                    onClick = onLocationsClick,
                )
            }

            item { SectionHeader(title = "About") }
            item {
                Card {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "MHGU Codex",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Offline reference for Monster Hunter Generations Ultimate. " +
                                "Unofficial fan project, not affiliated with Capcom.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Game data from MHGenDatabase by Gathering Hall Studios " +
                                "(MIT License, Copyright (c) 2014 DavianCorp). Icons are " +
                                "Material Symbols (Apache License 2.0).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
