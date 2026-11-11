package com.sanix.mhgucodex.feature.quests.domain

data class Quest(
    val id: Long,
    val name: String,
    val type: String,
    val rank: String,
    val objective: String,
    val locationName: String,
    val timeLimit: Int,
    val rewardZenny: Int,
    val description: String,
)

data class QuestMonster(
    val monsterName: String,
)

data class QuestReward(
    val itemName: String,
    val slot: String,
    val percent: Int,
)

data class QuestDetail(
    val quest: Quest,
    val monsters: List<QuestMonster>,
    val rewards: List<QuestReward>,
)
