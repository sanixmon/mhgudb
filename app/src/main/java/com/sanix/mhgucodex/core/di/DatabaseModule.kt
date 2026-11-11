package com.sanix.mhgucodex.core.di

import android.content.Context
import androidx.room.Room
import com.sanix.mhgucodex.data.local.MhguDatabase
import com.sanix.mhgucodex.feature.armor.data.ArmorDao
import com.sanix.mhgucodex.feature.decorations.data.DecorationDao
import com.sanix.mhgucodex.feature.items.data.ItemDao
import com.sanix.mhgucodex.feature.locations.data.LocationDao
import com.sanix.mhgucodex.feature.monsters.data.MonsterDao
import com.sanix.mhgucodex.feature.quests.data.QuestDao
import com.sanix.mhgucodex.feature.skills.data.SkillDao
import com.sanix.mhgucodex.feature.weapons.data.WeaponDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.zip.GZIPInputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "mhgu_codex.db"
    private const val DATABASE_ASSET = "databases/mhgu_codex.db.gz"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MhguDatabase =
        Room.databaseBuilder(context, MhguDatabase::class.java, DATABASE_NAME)
            // The bundled database is gzip-compressed to keep the repository small; Room copies
            // it into app storage on first open.
            .createFromInputStream { GZIPInputStream(context.assets.open(DATABASE_ASSET)) }
            // The data is read-only, so a schema bump simply replaces it with the new asset.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideMonsterDao(database: MhguDatabase): MonsterDao = database.monsterDao()

    @Provides
    fun provideWeaponDao(database: MhguDatabase): WeaponDao = database.weaponDao()

    @Provides
    fun provideArmorDao(database: MhguDatabase): ArmorDao = database.armorDao()

    @Provides
    fun provideItemDao(database: MhguDatabase): ItemDao = database.itemDao()

    @Provides
    fun provideQuestDao(database: MhguDatabase): QuestDao = database.questDao()

    @Provides
    fun provideSkillDao(database: MhguDatabase): SkillDao = database.skillDao()

    @Provides
    fun provideDecorationDao(database: MhguDatabase): DecorationDao = database.decorationDao()

    @Provides
    fun provideLocationDao(database: MhguDatabase): LocationDao = database.locationDao()
}
