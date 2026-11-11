package com.sanix.mhgucodex.feature.quests.data

import com.sanix.mhgucodex.data.local.DatabaseSeeder
import com.sanix.mhgucodex.feature.quests.domain.Quest
import com.sanix.mhgucodex.feature.quests.domain.QuestDetail
import com.sanix.mhgucodex.feature.quests.domain.QuestMonster
import com.sanix.mhgucodex.feature.quests.domain.QuestReward
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface QuestRepository {
    fun observeQuests(): Flow<List<Quest>>
    fun observeQuestDetail(questId: Long): Flow<QuestDetail?>
}

@Singleton
class QuestRepositoryImpl @Inject constructor(
    private val dao: QuestDao,
    private val seeder: DatabaseSeeder,
) : QuestRepository {

    override fun observeQuests(): Flow<List<Quest>> = flow {
        seeder.ensureSeeded()
        emitAll(dao.observeAll().map { entities -> entities.map(QuestEntity::toDomain) })
    }

    override fun observeQuestDetail(questId: Long): Flow<QuestDetail?> = flow {
        seeder.ensureSeeded()
        emitAll(
            combine(
                dao.observeById(questId),
                dao.observeMonsters(questId),
                dao.observeRewards(questId),
            ) { quest, monsters, rewards ->
                quest?.let {
                    QuestDetail(
                        quest = it.toDomain(),
                        monsters = monsters.map(QuestMonsterEntity::toDomain),
                        rewards = rewards.map(QuestRewardEntity::toDomain),
                    )
                }
            },
        )
    }
}

private fun QuestEntity.toDomain(): Quest = Quest(
    id = id,
    name = name,
    type = type,
    rank = rank,
    objective = objective,
    locationName = locationName,
    timeLimit = timeLimit,
    rewardZenny = rewardZenny,
    description = description,
)

private fun QuestMonsterEntity.toDomain(): QuestMonster = QuestMonster(monsterName = monsterName)

private fun QuestRewardEntity.toDomain(): QuestReward = QuestReward(
    itemName = itemName,
    slot = slot,
    percent = percent,
)
