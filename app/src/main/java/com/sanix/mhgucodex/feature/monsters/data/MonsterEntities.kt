package com.sanix.mhgucodex.feature.monsters.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val type: String,
    val isLarge: Boolean,
    val iconRes: String? = null,
    val description: String,
)

@Serializable
@Entity(tableName = "monster_weakness", primaryKeys = ["monsterId", "kind", "name"])
data class MonsterWeaknessEntity(
    val monsterId: Long,
    val kind: String,
    val name: String,
    val value: Int,
)

@Serializable
@Entity(tableName = "monster_hitzones")
data class MonsterHitzoneEntity(
    @PrimaryKey val id: Long,
    val monsterId: Long,
    val part: String,
    val slash: Int,
    val impact: Int,
    val shot: Int,
    val fire: Int,
    val water: Int,
    val thunder: Int,
    val ice: Int,
    val dragon: Int,
)

@Serializable
@Entity(tableName = "monster_drops")
data class MonsterDropEntity(
    @PrimaryKey val id: Long,
    val monsterId: Long,
    val itemName: String,
    val rank: String,
    val method: String,
    val percent: Int,
)

@Serializable
@Entity(tableName = "monster_habitats")
data class MonsterHabitatEntity(
    @PrimaryKey val id: Long,
    val monsterId: Long,
    val location: String,
    val areas: String,
)
