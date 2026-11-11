package com.sanix.mhgucodex.data.local

import com.sanix.mhgucodex.feature.armor.data.ArmorEntity
import com.sanix.mhgucodex.feature.armor.data.ArmorSkillEntity
import com.sanix.mhgucodex.feature.decorations.data.DecorationEntity
import com.sanix.mhgucodex.feature.decorations.data.DecorationSkillEntity
import com.sanix.mhgucodex.feature.items.data.ItemEntity
import com.sanix.mhgucodex.feature.items.data.ItemSourceEntity
import com.sanix.mhgucodex.feature.items.data.ItemUsageEntity
import com.sanix.mhgucodex.feature.locations.data.GatheringPointEntity
import com.sanix.mhgucodex.feature.locations.data.LocationEntity
import com.sanix.mhgucodex.feature.locations.data.LocationMonsterEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterDropEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterHabitatEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterHitzoneEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterWeaknessEntity
import com.sanix.mhgucodex.feature.quests.data.QuestEntity
import com.sanix.mhgucodex.feature.quests.data.QuestMonsterEntity
import com.sanix.mhgucodex.feature.quests.data.QuestRewardEntity
import com.sanix.mhgucodex.feature.skills.data.SkillArmorSourceEntity
import com.sanix.mhgucodex.feature.skills.data.SkillDecorationSourceEntity
import com.sanix.mhgucodex.feature.skills.data.SkillEntity
import com.sanix.mhgucodex.feature.skills.data.SkillLevelEntity
import com.sanix.mhgucodex.feature.weapons.data.WeaponEntity
import kotlinx.serialization.Serializable

/**
 * Shape of `assets/seed/placeholder_seed.json`.
 *
 * Every feature table in the scaffold is seeded from this single document. All names are
 * prefixed with "Placeholder" and contain no real Monster Hunter Generations Ultimate data.
 */
@Serializable
data class PlaceholderSeed(
    val monsters: List<MonsterEntity> = emptyList(),
    val monsterWeaknesses: List<MonsterWeaknessEntity> = emptyList(),
    val monsterHitzones: List<MonsterHitzoneEntity> = emptyList(),
    val monsterDrops: List<MonsterDropEntity> = emptyList(),
    val monsterHabitats: List<MonsterHabitatEntity> = emptyList(),
    val weapons: List<WeaponEntity> = emptyList(),
    val armor: List<ArmorEntity> = emptyList(),
    val armorSkills: List<ArmorSkillEntity> = emptyList(),
    val items: List<ItemEntity> = emptyList(),
    val itemSources: List<ItemSourceEntity> = emptyList(),
    val itemUsages: List<ItemUsageEntity> = emptyList(),
    val quests: List<QuestEntity> = emptyList(),
    val questMonsters: List<QuestMonsterEntity> = emptyList(),
    val questRewards: List<QuestRewardEntity> = emptyList(),
    val skills: List<SkillEntity> = emptyList(),
    val skillLevels: List<SkillLevelEntity> = emptyList(),
    val skillArmorSources: List<SkillArmorSourceEntity> = emptyList(),
    val skillDecorationSources: List<SkillDecorationSourceEntity> = emptyList(),
    val decorations: List<DecorationEntity> = emptyList(),
    val decorationSkills: List<DecorationSkillEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val locationMonsters: List<LocationMonsterEntity> = emptyList(),
    val gatheringPoints: List<GatheringPointEntity> = emptyList(),
)
