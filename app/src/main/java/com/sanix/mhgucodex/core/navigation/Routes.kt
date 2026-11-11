package com.sanix.mhgucodex.core.navigation

import kotlinx.serialization.Serializable

// Bottom navigation destinations.
@Serializable
data object MonstersRoute

@Serializable
data object WeaponsRoute

@Serializable
data object ArmorRoute

@Serializable
data object ItemsRoute

@Serializable
data object MoreRoute

// Secondary destinations reachable from "More".
@Serializable
data object QuestsRoute

@Serializable
data object SkillsRoute

@Serializable
data object DecorationsRoute

@Serializable
data object LocationsRoute

// Detail destinations.
@Serializable
data class MonsterDetailRoute(val monsterId: Long)

@Serializable
data class WeaponDetailRoute(val weaponId: Long)

@Serializable
data class ArmorDetailRoute(val armorId: Long)

@Serializable
data class ItemDetailRoute(val itemId: Long)

@Serializable
data class QuestDetailRoute(val questId: Long)

@Serializable
data class SkillDetailRoute(val skillId: Long)

@Serializable
data class DecorationDetailRoute(val decorationId: Long)

@Serializable
data class LocationDetailRoute(val locationId: Long)
