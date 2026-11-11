package com.sanix.mhgucodex.core.di

import com.sanix.mhgucodex.feature.armor.data.ArmorRepository
import com.sanix.mhgucodex.feature.armor.data.ArmorRepositoryImpl
import com.sanix.mhgucodex.feature.decorations.data.DecorationRepository
import com.sanix.mhgucodex.feature.decorations.data.DecorationRepositoryImpl
import com.sanix.mhgucodex.feature.items.data.ItemRepository
import com.sanix.mhgucodex.feature.items.data.ItemRepositoryImpl
import com.sanix.mhgucodex.feature.locations.data.LocationRepository
import com.sanix.mhgucodex.feature.locations.data.LocationRepositoryImpl
import com.sanix.mhgucodex.feature.monsters.data.MonsterRepository
import com.sanix.mhgucodex.feature.monsters.data.MonsterRepositoryImpl
import com.sanix.mhgucodex.feature.quests.data.QuestRepository
import com.sanix.mhgucodex.feature.quests.data.QuestRepositoryImpl
import com.sanix.mhgucodex.feature.skills.data.SkillRepository
import com.sanix.mhgucodex.feature.skills.data.SkillRepositoryImpl
import com.sanix.mhgucodex.feature.weapons.data.WeaponRepository
import com.sanix.mhgucodex.feature.weapons.data.WeaponRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMonsterRepository(impl: MonsterRepositoryImpl): MonsterRepository

    @Binds
    @Singleton
    abstract fun bindWeaponRepository(impl: WeaponRepositoryImpl): WeaponRepository

    @Binds
    @Singleton
    abstract fun bindArmorRepository(impl: ArmorRepositoryImpl): ArmorRepository

    @Binds
    @Singleton
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Binds
    @Singleton
    abstract fun bindQuestRepository(impl: QuestRepositoryImpl): QuestRepository

    @Binds
    @Singleton
    abstract fun bindSkillRepository(impl: SkillRepositoryImpl): SkillRepository

    @Binds
    @Singleton
    abstract fun bindDecorationRepository(impl: DecorationRepositoryImpl): DecorationRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
