package com.sanix.mhgucodex.feature.weapons.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
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
    val sharpness: Int,
    val slots: String,
    val rarity: Int,
    val description: String,
)
