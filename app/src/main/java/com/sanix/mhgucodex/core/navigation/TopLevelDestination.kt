package com.sanix.mhgucodex.core.navigation

import com.sanix.mhgucodex.R
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val label: String,
    val iconRes: Int,
) {
    MONSTERS(MonstersRoute, MonstersRoute::class, "Monsters", R.drawable.ic_pets),
    WEAPONS(WeaponsRoute, WeaponsRoute::class, "Weapons", R.drawable.ic_swords),
    ARMOR(ArmorRoute, ArmorRoute::class, "Armor", R.drawable.ic_shield),
    ITEMS(ItemsRoute, ItemsRoute::class, "Items", R.drawable.ic_inventory),
    MORE(MoreRoute, MoreRoute::class, "More", R.drawable.ic_more),
}
