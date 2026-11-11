package com.sanix.mhgucodex.feature.skills.domain

data class Skill(
    val id: Long,
    val name: String,
    val treeName: String,
    val category: String,
    val maxPoints: Int,
    val description: String,
)

data class SkillLevel(
    val points: Int,
    val effect: String,
)

data class SkillArmorSource(
    val armorName: String,
    val points: Int,
)

data class SkillDecorationSource(
    val decorationName: String,
    val points: Int,
)

data class SkillDetail(
    val skill: Skill,
    val levels: List<SkillLevel>,
    val armorSources: List<SkillArmorSource>,
    val decorationSources: List<SkillDecorationSource>,
)
