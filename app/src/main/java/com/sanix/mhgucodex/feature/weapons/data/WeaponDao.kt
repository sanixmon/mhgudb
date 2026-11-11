package com.sanix.mhgucodex.feature.weapons.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeaponDao {

    @Query("SELECT * FROM weapons ORDER BY name")
    fun observeAll(): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons WHERE id = :weaponId")
    fun observeById(weaponId: Long): Flow<WeaponEntity?>
}
