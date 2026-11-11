package com.sanix.mhgucodex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sanix.mhgucodex.feature.armor.data.ArmorDao
import com.sanix.mhgucodex.feature.armor.data.ArmorEntity
import com.sanix.mhgucodex.feature.armor.data.ArmorSkillEntity
import com.sanix.mhgucodex.feature.decorations.data.DecorationDao
import com.sanix.mhgucodex.feature.decorations.data.DecorationEntity
import com.sanix.mhgucodex.feature.decorations.data.DecorationSkillEntity
import com.sanix.mhgucodex.feature.items.data.ItemDao
import com.sanix.mhgucodex.feature.items.data.ItemEntity
import com.sanix.mhgucodex.feature.items.data.ItemSourceEntity
import com.sanix.mhgucodex.feature.items.data.ItemUsageEntity
import com.sanix.mhgucodex.feature.locations.data.GatheringPointEntity
import com.sanix.mhgucodex.feature.locations.data.LocationDao
import com.sanix.mhgucodex.feature.locations.data.LocationEntity
import com.sanix.mhgucodex.feature.locations.data.LocationMonsterEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterDao
import com.sanix.mhgucodex.feature.monsters.data.MonsterDropEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterHabitatEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterHitzoneEntity
import com.sanix.mhgucodex.feature.monsters.data.MonsterWeaknessEntity
import com.sanix.mhgucodex.feature.quests.data.QuestDao
import com.sanix.mhgucodex.feature.quests.data.QuestEntity
import com.sanix.mhgucodex.feature.quests.data.QuestMonsterEntity
import com.sanix.mhgucodex.feature.quests.data.QuestRewardEntity
import com.sanix.mhgucodex.feature.skills.data.SkillArmorSourceEntity
import com.sanix.mhgucodex.feature.skills.data.SkillDao
import com.sanix.mhgucodex.feature.skills.data.SkillDecorationSourceEntity
import com.sanix.mhgucodex.feature.skills.data.SkillEntity
import com.sanix.mhgucodex.feature.skills.data.SkillLevelEntity
import com.sanix.mhgucodex.feature.weapons.data.WeaponDao
import com.sanix.mhgucodex.feature.weapons.data.WeaponEntity

@Database(
    entities = [
        MonsterEntity::class,
        MonsterWeaknessEntity::class,
        MonsterHitzoneEntity::class,
        MonsterDropEntity::class,
        MonsterHabitatEntity::class,
        WeaponEntity::class,
        ArmorEntity::class,
        ArmorSkillEntity::class,
        ItemEntity::class,
        ItemSourceEntity::class,
        ItemUsageEntity::class,
        QuestEntity::class,
        QuestMonsterEntity::class,
        QuestRewardEntity::class,
        SkillEntity::class,
        SkillLevelEntity::class,
        SkillArmorSourceEntity::class,
        SkillDecorationSourceEntity::class,
        DecorationEntity::class,
        DecorationSkillEntity::class,
        LocationEntity::class,
        LocationMonsterEntity::class,
        GatheringPointEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MhguDatabase : RoomDatabase() {

    abstract fun monsterDao(): MonsterDao
    abstract fun weaponDao(): WeaponDao
    abstract fun armorDao(): ArmorDao
    abstract fun itemDao(): ItemDao
    abstract fun questDao(): QuestDao
    abstract fun skillDao(): SkillDao
    abstract fun decorationDao(): DecorationDao
    abstract fun locationDao(): LocationDao
    abstract fun seedDao(): SeedDao
}
