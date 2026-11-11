package com.sanix.mhgucodex.feature.weapons.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanix.mhgucodex.core.designsystem.theme.MhguTheme
import com.sanix.mhgucodex.feature.weapons.domain.Sharpness

/** Longest sharpness gauge in the game; bars are drawn to this scale so weapons compare. */
private const val MAX_SHARPNESS_HITS = 45

/** The in-game sharpness colors, red to purple. They carry meaning, so they ignore theming. */
private val SharpnessColors = listOf(
    Color(0xFFD9443A),
    Color(0xFFEE8A30),
    Color(0xFFE9D23C),
    Color(0xFF6CC24A),
    Color(0xFF3D7FDB),
    Color(0xFFF2F2F2),
    Color(0xFFA35CD6),
)

@Composable
fun SharpnessBar(
    label: String,
    sharpness: Sharpness,
    modifier: Modifier = Modifier,
) {
    val description = sharpness.segments
        .zip(Sharpness.COLORS)
        .filter { (hits, _) -> hits > 0 }
        .joinToString { (hits, color) -> "$color $hits" }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            sharpness.segments.forEachIndexed { index, hits ->
                if (hits > 0) {
                    Spacer(
                        modifier = Modifier
                            .weight(hits.toFloat())
                            .height(10.dp)
                            .background(SharpnessColors[index]),
                    )
                }
            }
            val remaining = MAX_SHARPNESS_HITS - sharpness.total
            if (remaining > 0) {
                Spacer(modifier = Modifier.weight(remaining.toFloat()))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharpnessBarPreview() {
    MhguTheme(dynamicColor = false) {
        SharpnessBar(
            label = "Base",
            sharpness = Sharpness(listOf(7, 3, 4, 16, 5, 0, 0)),
            modifier = Modifier.padding(16.dp),
        )
    }
}
