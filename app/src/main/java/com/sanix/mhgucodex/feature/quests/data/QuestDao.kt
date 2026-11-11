package com.sanix.mhgucodex.feature.quests.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests ORDER BY rank, name")
    fun observeAll(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :questId")
    fun observeById(questId: Long): Flow<QuestEntity?>

    @Query("SELECT * FROM quest_monsters WHERE questId = :questId ORDER BY monsterName")
    fun observeMonsters(questId: Long): Flow<List<QuestMonsterEntity>>

    @Query("SELECT * FROM quest_rewards WHERE questId = :questId ORDER BY id")
    fun observeRewards(questId: Long): Flow<List<QuestRewardEntity>>
}
