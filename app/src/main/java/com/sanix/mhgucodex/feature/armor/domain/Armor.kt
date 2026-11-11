package com.sanix.mhgucodex.feature.armor.domain

data class Armor(
    val id: Long,
    val name: String,
    val setName: String,
    val piece: String,
    val defense: Int,
    val maxDefense: Int,
    val fireRes: Int,
    val waterRes: Int,
    val thunderRes: Int,
    val iceRes: Int,
    val dragonRes: Int,
    val slots: String,
    val rarity: Int,
    val description: String,
)

data class ArmorSkill(
    val skillName: String,
    val points: Int,
)

data class ArmorDetail(
    val armor: Armor,
    val skills: List<ArmorSkill>,
)
