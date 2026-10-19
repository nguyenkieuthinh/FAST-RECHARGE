package com.thinhnk.fastapp.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SavedDao_Impl(
  __db: RoomDatabase,
) : SavedDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSavedListEntity: EntityInsertAdapter<SavedListEntity>

  private val __insertAdapterOfSavedItemEntity: EntityInsertAdapter<SavedItemEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSavedListEntity = object : EntityInsertAdapter<SavedListEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `saved_lists` (`id`,`name`,`createdAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SavedListEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.createdAt)
      }
    }
    this.__insertAdapterOfSavedItemEntity = object : EntityInsertAdapter<SavedItemEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `saved_items` (`listId`,`stationId`,`addedAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SavedItemEntity) {
        statement.bindText(1, entity.listId)
        statement.bindText(2, entity.stationId)
        statement.bindLong(3, entity.addedAt)
      }
    }
  }

  public override suspend fun insertList(list: SavedListEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSavedListEntity.insert(_connection, list)
  }

  public override suspend fun addItem(item: SavedItemEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSavedItemEntity.insert(_connection, item)
  }

  public override fun observeLists(): Flow<List<SavedListEntity>> {
    val _sql: String = "SELECT * FROM saved_lists ORDER BY createdAt"
    return createFlow(__db, false, arrayOf("saved_lists")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<SavedListEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SavedListEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = SavedListEntity(_tmpId,_tmpName,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findList(id: String): SavedListEntity? {
    val _sql: String = "SELECT * FROM saved_lists WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: SavedListEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result = SavedListEntity(_tmpId,_tmpName,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun firstList(): SavedListEntity? {
    val _sql: String = "SELECT * FROM saved_lists ORDER BY createdAt LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: SavedListEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result = SavedListEntity(_tmpId,_tmpName,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isSaved(listId: String, stationId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM saved_items WHERE listId = ? AND stationId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, listId)
        _argIndex = 2
        _stmt.bindText(_argIndex, stationId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeItemIds(listId: String): Flow<List<String>> {
    val _sql: String = "SELECT stationId FROM saved_items WHERE listId = ?"
    return createFlow(__db, false, arrayOf("saved_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, listId)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeStations(listId: String): Flow<List<StationEntity>> {
    val _sql: String = """
        |
        |        SELECT s.* FROM stations s
        |        INNER JOIN saved_items i ON i.stationId = s.id
        |        WHERE i.listId = ?
        |        ORDER BY i.addedAt DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("stations", "saved_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, listId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfLat: Int = getColumnIndexOrThrow(_stmt, "lat")
        val _columnIndexOfLng: Int = getColumnIndexOrThrow(_stmt, "lng")
        val _columnIndexOfOperator: Int = getColumnIndexOrThrow(_stmt, "operator")
        val _columnIndexOfPowers: Int = getColumnIndexOrThrow(_stmt, "powers")
        val _columnIndexOfPowerPorts: Int = getColumnIndexOrThrow(_stmt, "powerPorts")
        val _columnIndexOfPorts: Int = getColumnIndexOrThrow(_stmt, "ports")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfOpenHours: Int = getColumnIndexOrThrow(_stmt, "openHours")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _result: MutableList<StationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpLat: Double
          _tmpLat = _stmt.getDouble(_columnIndexOfLat)
          val _tmpLng: Double
          _tmpLng = _stmt.getDouble(_columnIndexOfLng)
          val _tmpOperator: String
          _tmpOperator = _stmt.getText(_columnIndexOfOperator)
          val _tmpPowers: String
          _tmpPowers = _stmt.getText(_columnIndexOfPowers)
          val _tmpPowerPorts: String
          _tmpPowerPorts = _stmt.getText(_columnIndexOfPowerPorts)
          val _tmpPorts: Int
          _tmpPorts = _stmt.getLong(_columnIndexOfPorts).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpOpenHours: String
          _tmpOpenHours = _stmt.getText(_columnIndexOfOpenHours)
          val _tmpUpdatedAt: String
          _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          _item = StationEntity(_tmpId,_tmpName,_tmpAddress,_tmpLat,_tmpLng,_tmpOperator,_tmpPowers,_tmpPowerPorts,_tmpPorts,_tmpStatus,_tmpOpenHours,_tmpUpdatedAt,_tmpSource)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeItem(listId: String, stationId: String) {
    val _sql: String = "DELETE FROM saved_items WHERE listId = ? AND stationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, listId)
        _argIndex = 2
        _stmt.bindText(_argIndex, stationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
