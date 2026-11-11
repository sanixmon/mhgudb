package com.sanix.mhgucodex.feature.locations.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val mapName: String,
    val terrain: String,
    val areaCount: Int,
    val description: String,
)

@Serializable
@Entity(tableName = "location_monsters", primaryKeys = ["locationId", "monsterName"])
data class LocationMonsterEntity(
    val locationId: Long,
    val monsterName: String,
)

@Serializable
@Entity(tableName = "gathering_points")
data class GatheringPointEntity(
    @PrimaryKey val id: Long,
    val locationId: Long,
    val area: String,
    val itemName: String,
    val nodeType: String,
    val percent: Int,
)
