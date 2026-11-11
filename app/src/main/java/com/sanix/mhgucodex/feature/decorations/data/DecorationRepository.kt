package com.sanix.mhgucodex.feature.decorations.data

import com.sanix.mhgucodex.feature.decorations.domain.Decoration
import com.sanix.mhgucodex.feature.decorations.domain.DecorationDetail
import com.sanix.mhgucodex.feature.decorations.domain.DecorationSkill
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface DecorationRepository {
    fun observeDecorations(): Flow<List<Decoration>>
    fun observeDecorationDetail(decorationId: Long): Flow<DecorationDetail?>
}

@Singleton
class DecorationRepositoryImpl @Inject constructor(
    private val dao: DecorationDao,
) : DecorationRepository {

    override fun observeDecorations(): Flow<List<Decoration>> =
        dao.observeAll().map { entities -> entities.map(DecorationEntity::toDomain) }

    override fun observeDecorationDetail(decorationId: Long): Flow<DecorationDetail?> =
        combine(
            dao.observeById(decorationId),
            dao.observeSkills(decorationId),
        ) { decoration, skills ->
            decoration?.let {
                DecorationDetail(
                    decoration = it.toDomain(),
                    skills = skills.map(DecorationSkillEntity::toDomain),
                )
            }
        }
}

private fun DecorationEntity.toDomain(): Decoration = Decoration(
    id = id,
    name = name,
    slotSize = slotSize,
    rarity = rarity,
    obtain = obtain,
    description = description,
)

private fun DecorationSkillEntity.toDomain(): DecorationSkill = DecorationSkill(
    skillName = skillName,
    points = points,
)
