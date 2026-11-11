package com.sanix.mhgucodex.feature.locations.data

import com.sanix.mhgucodex.data.local.DatabaseSeeder
import com.sanix.mhgucodex.feature.locations.domain.GatheringPoint
import com.sanix.mhgucodex.feature.locations.domain.Location
import com.sanix.mhgucodex.feature.locations.domain.LocationDetail
import com.sanix.mhgucodex.feature.locations.domain.LocationMonster
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface LocationRepository {
    fun observeLocations(): Flow<List<Location>>
    fun observeLocationDetail(locationId: Long): Flow<LocationDetail?>
}

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
    private val seeder: DatabaseSeeder,
) : LocationRepository {

    override fun observeLocations(): Flow<List<Location>> = flow {
        seeder.ensureSeeded()
        emitAll(dao.observeAll().map { entities -> entities.map(LocationEntity::toDomain) })
    }

    override fun observeLocationDetail(locationId: Long): Flow<LocationDetail?> = flow {
        seeder.ensureSeeded()
        emitAll(
            combine(
                dao.observeById(locationId),
                dao.observeMonsters(locationId),
                dao.observeGatheringPoints(locationId),
            ) { location, monsters, gatheringPoints ->
                location?.let {
                    LocationDetail(
                        location = it.toDomain(),
                        monsters = monsters.map(LocationMonsterEntity::toDomain),
                        gatheringPoints = gatheringPoints.map(GatheringPointEntity::toDomain),
                    )
                }
            },
        )
    }
}

private fun LocationEntity.toDomain(): Location = Location(
    id = id,
    name = name,
    mapName = mapName,
    terrain = terrain,
    areaCount = areaCount,
    description = description,
)

private fun LocationMonsterEntity.toDomain(): LocationMonster =
    LocationMonster(monsterName = monsterName)

private fun GatheringPointEntity.toDomain(): GatheringPoint = GatheringPoint(
    area = area,
    itemName = itemName,
    nodeType = nodeType,
    percent = percent,
)
