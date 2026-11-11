package com.sanix.mhgucodex.feature.items.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items ORDER BY name")
    fun observeAll(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    fun observeById(itemId: Long): Flow<ItemEntity?>

    @Query("SELECT * FROM item_sources WHERE itemId = :itemId ORDER BY source")
    fun observeSources(itemId: Long): Flow<List<ItemSourceEntity>>

    @Query("SELECT * FROM item_usages WHERE itemId = :itemId ORDER BY usage")
    fun observeUsages(itemId: Long): Flow<List<ItemUsageEntity>>
}
