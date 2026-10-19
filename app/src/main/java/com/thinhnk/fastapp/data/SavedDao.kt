package com.thinhnk.fastapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedDao {

    @Query("SELECT * FROM saved_lists ORDER BY createdAt")
    fun observeLists(): Flow<List<SavedListEntity>>

    @Query("SELECT * FROM saved_lists WHERE id = :id LIMIT 1")
    suspend fun findList(id: String): SavedListEntity?

    @Query("SELECT * FROM saved_lists ORDER BY createdAt LIMIT 1")
    suspend fun firstList(): SavedListEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(list: SavedListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(item: SavedItemEntity)

    @Query("DELETE FROM saved_items WHERE listId = :listId AND stationId = :stationId")
    suspend fun removeItem(listId: String, stationId: String)

    @Query("SELECT COUNT(*) FROM saved_items WHERE listId = :listId AND stationId = :stationId")
    suspend fun isSaved(listId: String, stationId: String): Int

    @Query("SELECT stationId FROM saved_items WHERE listId = :listId")
    fun observeItemIds(listId: String): Flow<List<String>>

    @Query(
        """
        SELECT s.* FROM stations s
        INNER JOIN saved_items i ON i.stationId = s.id
        WHERE i.listId = :listId
        ORDER BY i.addedAt DESC
        """,
    )
    fun observeStations(listId: String): Flow<List<StationEntity>>
}
