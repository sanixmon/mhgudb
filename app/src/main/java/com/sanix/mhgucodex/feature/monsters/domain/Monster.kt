package com.sanix.mhgucodex.feature.monsters.domain

data class Monster(
    val id: Long,
    val name: String,
    val type: String,
    val isLarge: Boolean,
    val iconRes: String?,
    val description: String,
)

data class MonsterWeakness(
    val kind: String,
    val name: String,
    val value: Int,
)

data class MonsterHitzone(
    val part: String,
    val slash: Int,
    val impact: Int,
    val shot: Int,
    val fire: Int,
    val water: Int,
    val thunder: Int,
    val ice: Int,
    val dragon: Int,
)

data class MonsterDrop(
    val itemName: String,
    val rank: String,
    val method: String,
    val percent: Int,
)

data class MonsterHabitat(
    val location: String,
    val areas: String,
)

data class MonsterDetail(
    val monster: Monster,
    val weaknesses: List<MonsterWeakness>,
    val hitzones: List<MonsterHitzone>,
    val drops: List<MonsterDrop>,
    val habitats: List<MonsterHabitat>,
)
