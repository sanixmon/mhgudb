package com.sanix.mhgucodex.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sanix.mhgucodex.feature.armor.ui.ArmorDetailScreenRoute
import com.sanix.mhgucodex.feature.armor.ui.ArmorListScreenRoute
import com.sanix.mhgucodex.feature.decorations.ui.DecorationDetailScreenRoute
import com.sanix.mhgucodex.feature.decorations.ui.DecorationListScreenRoute
import com.sanix.mhgucodex.feature.items.ui.ItemDetailScreenRoute
import com.sanix.mhgucodex.feature.items.ui.ItemListScreenRoute
import com.sanix.mhgucodex.feature.locations.ui.LocationDetailScreenRoute
import com.sanix.mhgucodex.feature.locations.ui.LocationListScreenRoute
import com.sanix.mhgucodex.feature.monsters.ui.MonsterDetailScreenRoute
import com.sanix.mhgucodex.feature.monsters.ui.MonsterListScreenRoute
import com.sanix.mhgucodex.feature.more.ui.MoreScreen
import com.sanix.mhgucodex.feature.quests.ui.QuestDetailScreenRoute
import com.sanix.mhgucodex.feature.quests.ui.QuestListScreenRoute
import com.sanix.mhgucodex.feature.skills.ui.SkillDetailScreenRoute
import com.sanix.mhgucodex.feature.skills.ui.SkillListScreenRoute
import com.sanix.mhgucodex.feature.weapons.ui.WeaponDetailScreenRoute
import com.sanix.mhgucodex.feature.weapons.ui.WeaponListScreenRoute

@Composable
fun MhguNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MonstersRoute,
        modifier = modifier,
    ) {
        composable<MonstersRoute> {
            MonsterListScreenRoute(
                onMonsterClick = { id -> navController.navigate(MonsterDetailRoute(monsterId = id)) },
            )
        }
        composable<MonsterDetailRoute> {
            MonsterDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<WeaponsRoute> {
            WeaponListScreenRoute(
                onWeaponClick = { id -> navController.navigate(WeaponDetailRoute(weaponId = id)) },
            )
        }
        composable<WeaponDetailRoute> {
            WeaponDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<ArmorRoute> {
            ArmorListScreenRoute(
                onArmorClick = { id -> navController.navigate(ArmorDetailRoute(armorId = id)) },
            )
        }
        composable<ArmorDetailRoute> {
            ArmorDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<ItemsRoute> {
            ItemListScreenRoute(
                onItemClick = { id -> navController.navigate(ItemDetailRoute(itemId = id)) },
            )
        }
        composable<ItemDetailRoute> {
            ItemDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<MoreRoute> {
            MoreScreen(
                onQuestsClick = { navController.navigate(QuestsRoute) },
                onSkillsClick = { navController.navigate(SkillsRoute) },
                onDecorationsClick = { navController.navigate(DecorationsRoute) },
                onLocationsClick = { navController.navigate(LocationsRoute) },
            )
        }

        composable<QuestsRoute> {
            QuestListScreenRoute(
                onQuestClick = { id -> navController.navigate(QuestDetailRoute(questId = id)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable<QuestDetailRoute> {
            QuestDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<SkillsRoute> {
            SkillListScreenRoute(
                onSkillClick = { id -> navController.navigate(SkillDetailRoute(skillId = id)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable<SkillDetailRoute> {
            SkillDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<DecorationsRoute> {
            DecorationListScreenRoute(
                onDecorationClick = { id ->
                    navController.navigate(DecorationDetailRoute(decorationId = id))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable<DecorationDetailRoute> {
            DecorationDetailScreenRoute(onBack = { navController.popBackStack() })
        }

        composable<LocationsRoute> {
            LocationListScreenRoute(
                onLocationClick = { id ->
                    navController.navigate(LocationDetailRoute(locationId = id))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable<LocationDetailRoute> {
            LocationDetailScreenRoute(onBack = { navController.popBackStack() })
        }
    }
}
