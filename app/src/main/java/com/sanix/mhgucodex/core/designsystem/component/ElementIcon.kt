package com.sanix.mhgucodex.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanix.mhgucodex.core.designsystem.theme.CodexAmethyst
import com.sanix.mhgucodex.core.designsystem.theme.CodexEmber
import com.sanix.mhgucodex.core.designsystem.theme.CodexGold
import com.sanix.mhgucodex.core.designsystem.theme.CodexMoss
import com.sanix.mhgucodex.core.designsystem.theme.CodexSky
import com.sanix.mhgucodex.core.designsystem.theme.CodexTeal
import com.sanix.mhgucodex.core.designsystem.theme.MhguTheme

/**
 * Small circular badge that stands in for an element / status icon. Keeping it text based means
 * the scaffold has no extra art dependency while the color mapping is already in place.
 */
@Composable
fun ElementIcon(
    element: String,
    modifier: Modifier = Modifier,
) {
    val color = elementColor(element)
    Box(
        modifier = modifier
            .size(28.dp)
            .background(color.copy(alpha = 0.16f), CircleShape)
            .border(1.dp, color.copy(alpha = 0.65f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = element.take(1).uppercase(),
            color = color,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

fun elementColor(element: String): Color = when (element.trim().lowercase()) {
    "fire" -> CodexEmber
    "water" -> CodexSky
    "thunder" -> CodexGold
    "ice" -> CodexTeal
    "dragon" -> CodexAmethyst
    "poison" -> CodexMoss
    "paralysis" -> CodexGold
    "sleep" -> CodexSky
    else -> CodexGold
}

@Preview(showBackground = true)
@Composable
private fun ElementIconPreview() {
    MhguTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("Fire", "Water", "Thunder", "Ice", "Dragon", "Poison", "Sleep").forEach {
                ElementIcon(element = it)
            }
        }
    }
}
