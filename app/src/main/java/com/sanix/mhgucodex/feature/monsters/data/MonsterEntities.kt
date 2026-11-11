package com.sanix.mhgucodex.feature.monsters.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val type: String,
    val isLarge: Boolean,
    val iconRes: String? = null,
    val description: String,
)

@Entity(tableName = "monster_weakness", primaryKeys = ["monsterId", "kind", "name"])
data class MonsterWeaknessEntity(
    val monsterId: Long,
    val kind: String,
    val name: String,
    val value: Int,
)

@Entity(tableName = "monster_hitzones", indices = [Index("monsterId")])
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

@Entity(tableName = "monster_drops", indices = [Index("monsterId")])
data class MonsterDropEntity(
    @PrimaryKey val id: Long,
    val monsterId: Long,
    val itemName: String,
    val rank: String,
    val method: String,
    val percent: Int,
)

@Entity(tableName = "monster_habitats", indices = [Index("monsterId")])
data class MonsterHabitatEntity(
    @PrimaryKey val id: Long,
    val monsterId: Long,
    val location: String,
    val areas: String,
)
