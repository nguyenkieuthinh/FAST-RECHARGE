package com.thinhnk.fastapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Query("SELECT COUNT(*) FROM stations")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stations: List<StationEntity>)

    @Query("DELETE FROM stations")
    suspend fun deleteAll()

    @Query("SELECT * FROM stations ORDER BY name")
    fun observeAll(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): StationEntity?

    @Query(
        """
        SELECT * FROM stations
        WHERE name LIKE '%' || :query || '%'
           OR address LIKE '%' || :query || '%'
           OR operator LIKE '%' || :query || '%'
        ORDER BY name
        LIMIT 50
        """,
    )
    suspend fun search(query: String): List<StationEntity>
}
