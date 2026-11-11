package com.sanix.mhgucodex.feature.decorations.domain

data class Decoration(
    val id: Long,
    val name: String,
    val slotSize: Int,
    val rarity: Int,
    val obtain: String,
    val description: String,
)

data class DecorationSkill(
    val skillName: String,
    val points: Int,
)

data class DecorationDetail(
    val decoration: Decoration,
    val skills: List<DecorationSkill>,
)
