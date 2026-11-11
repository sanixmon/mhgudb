package com.sanix.mhgucodex.feature.quests.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val type: String,
    val rank: String,
    val objective: String,
    val locationName: String,
    val timeLimit: Int,
    val rewardZenny: Int,
    val description: String,
)

@Entity(tableName = "quest_monsters", primaryKeys = ["questId", "monsterName"])
data class QuestMonsterEntity(
    val questId: Long,
    val monsterName: String,
)

@Entity(tableName = "quest_rewards", indices = [Index("questId")])
data class QuestRewardEntity(
    @PrimaryKey val id: Long,
    val questId: Long,
    val itemName: String,
    val slot: String,
    val percent: Int,
)
