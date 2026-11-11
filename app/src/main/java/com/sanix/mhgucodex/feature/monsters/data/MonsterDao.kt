package com.sanix.mhgucodex.feature.monsters.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MonsterDao {

    @Query("SELECT * FROM monsters ORDER BY name")
    fun observeAll(): Flow<List<MonsterEntity>>

    @Query("SELECT * FROM monsters WHERE id = :monsterId")
    fun observeById(monsterId: Long): Flow<MonsterEntity?>

    @Query("SELECT * FROM monster_weakness WHERE monsterId = :monsterId ORDER BY kind, name")
    fun observeWeaknesses(monsterId: Long): Flow<List<MonsterWeaknessEntity>>

    @Query("SELECT * FROM monster_hitzones WHERE monsterId = :monsterId ORDER BY id")
    fun observeHitzones(monsterId: Long): Flow<List<MonsterHitzoneEntity>>

    @Query("SELECT * FROM monster_drops WHERE monsterId = :monsterId ORDER BY rank, method")
    fun observeDrops(monsterId: Long): Flow<List<MonsterDropEntity>>

    @Query("SELECT * FROM monster_habitats WHERE monsterId = :monsterId ORDER BY id")
    fun observeHabitats(monsterId: Long): Flow<List<MonsterHabitatEntity>>
}
