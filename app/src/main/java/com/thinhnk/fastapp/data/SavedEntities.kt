package com.thinhnk.fastapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "saved_lists")
data class SavedListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long,
)

@Entity(
    tableName = "saved_items",
    primaryKeys = ["listId", "stationId"],
    indices = [Index("stationId")],
    foreignKeys = [
        ForeignKey(
            entity = SavedListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SavedItemEntity(
    val listId: String,
    val stationId: String,
    val addedAt: Long,
)
