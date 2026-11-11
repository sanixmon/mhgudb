package com.sanix.mhgucodex.feature.skills.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val treeName: String,
    val category: String,
    val maxPoints: Int,
    val description: String,
)

@Entity(tableName = "skill_levels", indices = [Index("skillId")])
data class SkillLevelEntity(
    @PrimaryKey val id: Long,
    val skillId: Long,
    val points: Int,
    val effect: String,
)

@Entity(tableName = "skill_armor_sources", primaryKeys = ["skillId", "armorName"])
data class SkillArmorSourceEntity(
    val skillId: Long,
    val armorName: String,
    val points: Int,
)

@Entity(tableName = "skill_decoration_sources", primaryKeys = ["skillId", "decorationName"])
data class SkillDecorationSourceEntity(
    val skillId: Long,
    val decorationName: String,
    val points: Int,
)
