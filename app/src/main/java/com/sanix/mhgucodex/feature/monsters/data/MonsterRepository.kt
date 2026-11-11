package com.sanix.mhgucodex.feature.monsters.data

import com.sanix.mhgucodex.feature.monsters.domain.Monster
import com.sanix.mhgucodex.feature.monsters.domain.MonsterDetail
import com.sanix.mhgucodex.feature.monsters.domain.MonsterDrop
import com.sanix.mhgucodex.feature.monsters.domain.MonsterHabitat
import com.sanix.mhgucodex.feature.monsters.domain.MonsterHitzone
import com.sanix.mhgucodex.feature.monsters.domain.MonsterWeakness
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface MonsterRepository {
    fun observeMonsters(): Flow<List<Monster>>
    fun observeMonsterDetail(monsterId: Long): Flow<MonsterDetail?>
}

@Singleton
class MonsterRepositoryImpl @Inject constructor(
    private val dao: MonsterDao,
) : MonsterRepository {

    override fun observeMonsters(): Flow<List<Monster>> =
        dao.observeAll().map { entities -> entities.map(MonsterEntity::toDomain) }

    override fun observeMonsterDetail(monsterId: Long): Flow<MonsterDetail?> =
        combine(
            dao.observeById(monsterId),
            dao.observeWeaknesses(monsterId),
            dao.observeHitzones(monsterId),
            dao.observeDrops(monsterId),
            dao.observeHabitats(monsterId),
        ) { monster, weaknesses, hitzones, drops, habitats ->
            monster?.let {
                MonsterDetail(
                    monster = it.toDomain(),
                    weaknesses = weaknesses.map(MonsterWeaknessEntity::toDomain),
                    hitzones = hitzones.map(MonsterHitzoneEntity::toDomain),
                    drops = drops.map(MonsterDropEntity::toDomain),
                    habitats = habitats.map(MonsterHabitatEntity::toDomain),
                )
            }
        }
}

private fun MonsterEntity.toDomain(): Monster = Monster(
    id = id,
    name = name,
    type = type,
    isLarge = isLarge,
    iconRes = iconRes,
    description = description,
)

private fun MonsterWeaknessEntity.toDomain(): MonsterWeakness = MonsterWeakness(
    kind = kind,
    name = name,
    value = value,
)

private fun MonsterHitzoneEntity.toDomain(): MonsterHitzone = MonsterHitzone(
    part = part,
    slash = slash,
    impact = impact,
    shot = shot,
    fire = fire,
    water = water,
    thunder = thunder,
    ice = ice,
    dragon = dragon,
)

private fun MonsterDropEntity.toDomain(): MonsterDrop = MonsterDrop(
    itemName = itemName,
    rank = rank,
    method = method,
    percent = percent,
)

private fun MonsterHabitatEntity.toDomain(): MonsterHabitat = MonsterHabitat(
    location = location,
    areas = areas,
)
