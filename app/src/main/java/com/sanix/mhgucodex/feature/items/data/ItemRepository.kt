package com.sanix.mhgucodex.feature.items.data

import com.sanix.mhgucodex.data.local.DatabaseSeeder
import com.sanix.mhgucodex.feature.items.domain.Item
import com.sanix.mhgucodex.feature.items.domain.ItemDetail
import com.sanix.mhgucodex.feature.items.domain.ItemSource
import com.sanix.mhgucodex.feature.items.domain.ItemUsage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface ItemRepository {
    fun observeItems(): Flow<List<Item>>
    fun observeItemDetail(itemId: Long): Flow<ItemDetail?>
}

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val dao: ItemDao,
    private val seeder: DatabaseSeeder,
) : ItemRepository {

    override fun observeItems(): Flow<List<Item>> = flow {
        seeder.ensureSeeded()
        emitAll(dao.observeAll().map { entities -> entities.map(ItemEntity::toDomain) })
    }

    override fun observeItemDetail(itemId: Long): Flow<ItemDetail?> = flow {
        seeder.ensureSeeded()
        emitAll(
            combine(
                dao.observeById(itemId),
                dao.observeSources(itemId),
                dao.observeUsages(itemId),
            ) { item, sources, usages ->
                item?.let {
                    ItemDetail(
                        item = it.toDomain(),
                        sources = sources.map(ItemSourceEntity::toDomain),
                        usages = usages.map(ItemUsageEntity::toDomain),
                    )
                }
            },
        )
    }
}

private fun ItemEntity.toDomain(): Item = Item(
    id = id,
    name = name,
    category = category,
    rarity = rarity,
    description = description,
    maxCarry = maxCarry,
    buyPrice = buyPrice,
    sellPrice = sellPrice,
)

private fun ItemSourceEntity.toDomain(): ItemSource = ItemSource(
    source = source,
    detail = detail,
)

private fun ItemUsageEntity.toDomain(): ItemUsage = ItemUsage(usage = usage)
