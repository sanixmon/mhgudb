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
    val sharpness: Int,
    val slots: String,
    val rarity: Int,
    val description: String,
)
