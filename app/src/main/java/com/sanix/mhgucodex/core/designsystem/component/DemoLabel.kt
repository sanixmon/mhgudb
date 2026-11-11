package com.sanix.mhgucodex.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Marks every scaffold screen as placeholder-only content. */
@Composable
fun DemoLabel(modifier: Modifier = Modifier) {
    Text(
        text = "DEMO",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(end = 16.dp),
    )
}
