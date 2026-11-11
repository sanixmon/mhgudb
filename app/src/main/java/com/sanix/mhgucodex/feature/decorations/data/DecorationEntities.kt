package com.sanix.mhgucodex.feature.decorations.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decorations")
data class DecorationEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val slotSize: Int,
    val rarity: Int,
    val obtain: String,
    val description: String,
)

@Entity(tableName = "decoration_skills", primaryKeys = ["decorationId", "skillName"])
data class DecorationSkillEntity(
    val decorationId: Long,
    val skillName: String,
    val points: Int,
)
