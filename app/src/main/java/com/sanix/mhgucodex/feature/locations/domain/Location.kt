package com.sanix.mhgucodex.feature.locations.domain

data class Location(
    val id: Long,
    val name: String,
    val mapName: String,
    val terrain: String,
    val areaCount: Int,
    val description: String,
)

data class LocationMonster(
    val monsterName: String,
)

data class GatheringPoint(
    val area: String,
    val itemName: String,
    val nodeType: String,
    val percent: Int,
)

data class LocationDetail(
    val location: Location,
    val monsters: List<LocationMonster>,
    val gatheringPoints: List<GatheringPoint>,
)
