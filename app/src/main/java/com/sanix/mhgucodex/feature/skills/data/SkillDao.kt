package com.sanix.mhgucodex.feature.skills.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {

    @Query("SELECT * FROM skills ORDER BY treeName, name")
    fun observeAll(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE id = :skillId")
    fun observeById(skillId: Long): Flow<SkillEntity?>

    @Query("SELECT * FROM skill_levels WHERE skillId = :skillId ORDER BY points")
    fun observeLevels(skillId: Long): Flow<List<SkillLevelEntity>>

    @Query("SELECT * FROM skill_armor_sources WHERE skillId = :skillId ORDER BY armorName")
    fun observeArmorSources(skillId: Long): Flow<List<SkillArmorSourceEntity>>

    @Query("SELECT * FROM skill_decoration_sources WHERE skillId = :skillId ORDER BY decorationName")
    fun observeDecorationSources(skillId: Long): Flow<List<SkillDecorationSourceEntity>>
}
