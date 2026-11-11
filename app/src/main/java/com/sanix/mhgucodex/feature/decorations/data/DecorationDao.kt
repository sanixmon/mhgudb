package com.sanix.mhgucodex.feature.decorations.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DecorationDao {

    @Query("SELECT * FROM decorations ORDER BY slotSize, name")
    fun observeAll(): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM decorations WHERE id = :decorationId")
    fun observeById(decorationId: Long): Flow<DecorationEntity?>

    @Query("SELECT * FROM decoration_skills WHERE decorationId = :decorationId ORDER BY skillName")
    fun observeSkills(decorationId: Long): Flow<List<DecorationSkillEntity>>
}
