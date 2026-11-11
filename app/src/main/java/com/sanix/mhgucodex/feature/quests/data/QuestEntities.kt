package com.sanix.mhgucodex.feature.quests.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
@Entity(tableName = "quest_monsters", primaryKeys = ["questId", "monsterName"])
data class QuestMonsterEntity(
    val questId: Long,
    val monsterName: String,
)

@Serializable
@Entity(tableName = "quest_rewards")
data class QuestRewardEntity(
    @PrimaryKey val id: Long,
    val questId: Long,
    val itemName: String,
    val slot: String,
    val percent: Int,
)
