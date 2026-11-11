package com.sanix.mhgucodex.feature.weapons.data

import com.sanix.mhgucodex.feature.weapons.domain.Sharpness
import com.sanix.mhgucodex.feature.weapons.domain.Weapon
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface WeaponRepository {
    fun observeWeapons(): Flow<List<Weapon>>
    fun observeWeapon(weaponId: Long): Flow<Weapon?>
}

@Singleton
class WeaponRepositoryImpl @Inject constructor(
    private val dao: WeaponDao,
) : WeaponRepository {

    override fun observeWeapons(): Flow<List<Weapon>> =
        dao.observeAll().map { entities -> entities.map(WeaponEntity::toDomain) }

    override fun observeWeapon(weaponId: Long): Flow<Weapon?> =
        dao.observeById(weaponId).map { it?.toDomain() }
}

private fun WeaponEntity.toDomain(): Weapon = Weapon(
    id = id,
    name = name,
    type = type,
    treeName = treeName,
    upgradeFrom = upgradeFrom,
    attack = attack,
    affinity = affinity,
    element = element,
    elementValue = elementValue,
    sharpness = Sharpness.parse(sharpness),
    special = special.lines().filter(String::isNotBlank),
    slots = slots,
    rarity = rarity,
    description = description,
)
