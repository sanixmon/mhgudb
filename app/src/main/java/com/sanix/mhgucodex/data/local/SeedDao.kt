package com.sanix.mhgucodex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

@Dao
interface SeedDao {

    @Query("SELECT COUNT(*) FROM monsters")
    suspend fun monsterCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonsters(items: List<MonsterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonsterWeaknesses(items: List<MonsterWeaknessEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonsterHitzones(items: List<MonsterHitzoneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonsterDrops(items: List<MonsterDropEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonsterHabitats(items: List<MonsterHabitatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapons(items: List<WeaponEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArmor(items: List<ArmorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArmorSkills(items: List<ArmorSkillEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemSources(items: List<ItemSourceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemUsages(items: List<ItemUsageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(items: List<QuestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestMonsters(items: List<QuestMonsterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestRewards(items: List<QuestRewardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(items: List<SkillEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkillLevels(items: List<SkillLevelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkillArmorSources(items: List<SkillArmorSourceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkillDecorationSources(items: List<SkillDecorationSourceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecorations(items: List<DecorationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecorationSkills(items: List<DecorationSkillEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(items: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationMonsters(items: List<LocationMonsterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGatheringPoints(items: List<GatheringPointEntity>)
}
