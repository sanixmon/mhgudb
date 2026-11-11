package com.sanix.mhgucodex.data.local

import android.content.Context
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads the bundled placeholder dataset once, in a single transaction.
 *
 * The check-and-insert is guarded by a [Mutex] so concurrent first screens cannot seed twice.
 * Replacing this class with a `createFromAsset("mhgu.db")` based seeder later does not require
 * changes in any repository.
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MhguDatabase,
    private val seedDao: SeedDao,
) {

    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun ensureSeeded() {
        if (seedDao.monsterCount() > 0) return
        mutex.withLock {
            if (seedDao.monsterCount() > 0) return
            val seed = withContext(Dispatchers.IO) {
                context.assets.open(SEED_ASSET).bufferedReader().use { it.readText() }
                    .let { payload -> json.decodeFromString<PlaceholderSeed>(payload) }
            }
            database.withTransaction { insert(seed) }
        }
    }

    private suspend fun insert(seed: PlaceholderSeed) {
        seedDao.insertMonsters(seed.monsters)
        seedDao.insertMonsterWeaknesses(seed.monsterWeaknesses)
        seedDao.insertMonsterHitzones(seed.monsterHitzones)
        seedDao.insertMonsterDrops(seed.monsterDrops)
        seedDao.insertMonsterHabitats(seed.monsterHabitats)
        seedDao.insertWeapons(seed.weapons)
        seedDao.insertArmor(seed.armor)
        seedDao.insertArmorSkills(seed.armorSkills)
        seedDao.insertItems(seed.items)
        seedDao.insertItemSources(seed.itemSources)
        seedDao.insertItemUsages(seed.itemUsages)
        seedDao.insertQuests(seed.quests)
        seedDao.insertQuestMonsters(seed.questMonsters)
        seedDao.insertQuestRewards(seed.questRewards)
        seedDao.insertSkills(seed.skills)
        seedDao.insertSkillLevels(seed.skillLevels)
        seedDao.insertSkillArmorSources(seed.skillArmorSources)
        seedDao.insertSkillDecorationSources(seed.skillDecorationSources)
        seedDao.insertDecorations(seed.decorations)
        seedDao.insertDecorationSkills(seed.decorationSkills)
        seedDao.insertLocations(seed.locations)
        seedDao.insertLocationMonsters(seed.locationMonsters)
        seedDao.insertGatheringPoints(seed.gatheringPoints)
    }

    private companion object {
        const val SEED_ASSET = "seed/placeholder_seed.json"
    }
}
