package com.sanix.mhgucodex.feature.weapons.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weapons")
data class WeaponEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val type: String,
    val treeName: String,
    val upgradeFrom: String? = null,
    val attack: Int,
    val affinity: Int,
    val element: String,
    val elementValue: Int,
    /** Three sharpness levels (base, +1, +2), each seven dot-separated hit counts. */
    val sharpness: String,
    /** Weapon-type specific stats such as phial, shelling or ammo, one per line. */
    val special: String,
    val slots: String,
    val rarity: Int,
    val description: String,
)
