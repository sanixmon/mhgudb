package com.sanix.mhgucodex.feature.armor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "armor")
data class ArmorEntity(
    @PrimaryKey val id: Long,
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

@Entity(tableName = "armor_skills", primaryKeys = ["armorId", "skillName"])
data class ArmorSkillEntity(
    val armorId: Long,
    val skillName: String,
    val points: Int,
)
