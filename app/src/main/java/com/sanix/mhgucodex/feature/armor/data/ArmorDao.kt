package com.sanix.mhgucodex.feature.armor.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArmorDao {

    @Query("SELECT * FROM armor ORDER BY setName, piece")
    fun observeAll(): Flow<List<ArmorEntity>>

    @Query("SELECT * FROM armor WHERE id = :armorId")
    fun observeById(armorId: Long): Flow<ArmorEntity?>

    @Query("SELECT * FROM armor_skills WHERE armorId = :armorId ORDER BY skillName")
    fun observeSkills(armorId: Long): Flow<List<ArmorSkillEntity>>
}
