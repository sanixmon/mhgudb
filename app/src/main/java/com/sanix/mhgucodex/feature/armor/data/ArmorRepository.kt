package com.sanix.mhgucodex.feature.armor.data

import com.sanix.mhgucodex.data.local.DatabaseSeeder
import com.sanix.mhgucodex.feature.armor.domain.Armor
import com.sanix.mhgucodex.feature.armor.domain.ArmorDetail
import com.sanix.mhgucodex.feature.armor.domain.ArmorSkill
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface ArmorRepository {
    fun observeArmor(): Flow<List<Armor>>
    fun observeArmorDetail(armorId: Long): Flow<ArmorDetail?>
}

@Singleton
class ArmorRepositoryImpl @Inject constructor(
    private val dao: ArmorDao,
    private val seeder: DatabaseSeeder,
) : ArmorRepository {

    override fun observeArmor(): Flow<List<Armor>> = flow {
        seeder.ensureSeeded()
        emitAll(dao.observeAll().map { entities -> entities.map(ArmorEntity::toDomain) })
    }

    override fun observeArmorDetail(armorId: Long): Flow<ArmorDetail?> = flow {
        seeder.ensureSeeded()
        emitAll(
            combine(
                dao.observeById(armorId),
                dao.observeSkills(armorId),
            ) { armor, skills ->
                armor?.let {
                    ArmorDetail(
                        armor = it.toDomain(),
                        skills = skills.map(ArmorSkillEntity::toDomain),
                    )
                }
            },
        )
    }
}

private fun ArmorEntity.toDomain(): Armor = Armor(
    id = id,
    name = name,
    setName = setName,
    piece = piece,
    defense = defense,
    maxDefense = maxDefense,
    fireRes = fireRes,
    waterRes = waterRes,
    thunderRes = thunderRes,
    iceRes = iceRes,
    dragonRes = dragonRes,
    slots = slots,
    rarity = rarity,
    description = description,
)

private fun ArmorSkillEntity.toDomain(): ArmorSkill = ArmorSkill(
    skillName = skillName,
    points = points,
)
