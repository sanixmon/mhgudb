package com.sanix.mhgucodex.feature.weapons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
fun WeaponListScreenRoute(
    onWeaponClick: (Long) -> Unit,
    viewModel: WeaponListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeaponListScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onWeaponClick = onWeaponClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeaponListScreen(
    uiState: WeaponListUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (WeaponFilter) -> Unit,
    onWeaponClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weapons") },
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
                placeholder = "Search weapons",
            )
            FilterChipRow(
                options = WeaponFilter.entries.toList(),
                selected = uiState.filter,
                label = { it.label },
                onSelect = onFilterChange,
            )
            when {
                uiState.isLoading -> LoadingState()
                uiState.weapons.isEmpty() -> EmptyState(
                    title = "No weapons found",
                    message = "Try another search term or clear the filter.",
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.weapons, key = { weapon -> weapon.id }) { weapon ->
                        MhguListRow(
                            title = weapon.name,
                            subtitle = listOf(weapon.type, weapon.element, "Rarity ${weapon.rarity}")
                                .filter(String::isNotBlank)
                                .joinToString(" · "),
                            iconRes = R.drawable.ic_swords,
                            trailing = "ATK ${weapon.attack}",
                            onClick = { onWeaponClick(weapon.id) },
                        )
                    }
                }
            }
        }
    }
}
