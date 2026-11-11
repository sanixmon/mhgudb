package com.sanix.mhgucodex.feature.skills.data

import com.sanix.mhgucodex.data.local.DatabaseSeeder
import com.sanix.mhgucodex.feature.skills.domain.Skill
import com.sanix.mhgucodex.feature.skills.domain.SkillArmorSource
import com.sanix.mhgucodex.feature.skills.domain.SkillDecorationSource
import com.sanix.mhgucodex.feature.skills.domain.SkillDetail
import com.sanix.mhgucodex.feature.skills.domain.SkillLevel
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface SkillRepository {
    fun observeSkills(): Flow<List<Skill>>
    fun observeSkillDetail(skillId: Long): Flow<SkillDetail?>
}

@Singleton
class SkillRepositoryImpl @Inject constructor(
    private val dao: SkillDao,
    private val seeder: DatabaseSeeder,
) : SkillRepository {

    override fun observeSkills(): Flow<List<Skill>> = flow {
        seeder.ensureSeeded()
        emitAll(dao.observeAll().map { entities -> entities.map(SkillEntity::toDomain) })
    }

    override fun observeSkillDetail(skillId: Long): Flow<SkillDetail?> = flow {
        seeder.ensureSeeded()
        emitAll(
            combine(
                dao.observeById(skillId),
                dao.observeLevels(skillId),
                dao.observeArmorSources(skillId),
                dao.observeDecorationSources(skillId),
            ) { skill, levels, armorSources, decorationSources ->
                skill?.let {
                    SkillDetail(
                        skill = it.toDomain(),
                        levels = levels.map(SkillLevelEntity::toDomain),
                        armorSources = armorSources.map(SkillArmorSourceEntity::toDomain),
                        decorationSources = decorationSources.map(SkillDecorationSourceEntity::toDomain),
                    )
                }
            },
        )
    }
}

private fun SkillEntity.toDomain(): Skill = Skill(
    id = id,
    name = name,
    treeName = treeName,
    category = category,
    maxPoints = maxPoints,
    description = description,
)

private fun SkillLevelEntity.toDomain(): SkillLevel = SkillLevel(
    points = points,
    effect = effect,
)

private fun SkillArmorSourceEntity.toDomain(): SkillArmorSource = SkillArmorSource(
    armorName = armorName,
    points = points,
)

private fun SkillDecorationSourceEntity.toDomain(): SkillDecorationSource = SkillDecorationSource(
    decorationName = decorationName,
    points = points,
)
