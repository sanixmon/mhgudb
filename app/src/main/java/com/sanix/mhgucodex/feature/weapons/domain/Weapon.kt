package com.sanix.mhgucodex.feature.weapons.domain

data class Weapon(
    val id: Long,
    val name: String,
    val type: String,
    val treeName: String,
    val upgradeFrom: String?,
    val attack: Int,
    val affinity: Int,
    val element: String,
    val elementValue: Int,
    /** Base, Sharpness +1 and Sharpness +2. Empty for bows and bowguns. */
    val sharpness: List<Sharpness>,
    val special: List<String>,
    val slots: String,
    val rarity: Int,
    val description: String,
)

/** Hit counts per sharpness color, from red to purple. */
data class Sharpness(val segments: List<Int>) {

    val total: Int get() = segments.sum()

    companion object {
        val COLORS = listOf("Red", "Orange", "Yellow", "Green", "Blue", "White", "Purple")

        /** Parses `"r.o.y.g.b.w.p r.o.y.g.b.w.p ..."`; malformed levels are skipped. */
        fun parse(raw: String): List<Sharpness> = raw.trim()
            .split(' ')
            .filter(String::isNotBlank)
            .mapNotNull { level ->
                val values = level.split('.').map { it.toIntOrNull() ?: return@mapNotNull null }
                if (values.size == COLORS.size) Sharpness(values) else null
            }
    }
}
