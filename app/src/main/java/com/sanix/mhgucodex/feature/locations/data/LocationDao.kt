package com.sanix.mhgucodex.feature.locations.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations ORDER BY name")
    fun observeAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :locationId")
    fun observeById(locationId: Long): Flow<LocationEntity?>

    @Query("SELECT * FROM location_monsters WHERE locationId = :locationId ORDER BY monsterName")
    fun observeMonsters(locationId: Long): Flow<List<LocationMonsterEntity>>

    @Query("SELECT * FROM gathering_points WHERE locationId = :locationId ORDER BY area, itemName")
    fun observeGatheringPoints(locationId: Long): Flow<List<GatheringPointEntity>>
}
