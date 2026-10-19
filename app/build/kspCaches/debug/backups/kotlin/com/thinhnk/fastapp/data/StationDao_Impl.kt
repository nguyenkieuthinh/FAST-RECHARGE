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
public class StationDao_Impl(
  __db: RoomDatabase,
) : StationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStationEntity: EntityInsertAdapter<StationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfStationEntity = object : EntityInsertAdapter<StationEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `stations` (`id`,`name`,`address`,`lat`,`lng`,`operator`,`powers`,`powerPorts`,`ports`,`status`,`openHours`,`updatedAt`,`source`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StationEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.address)
        statement.bindDouble(4, entity.lat)
        statement.bindDouble(5, entity.lng)
        statement.bindText(6, entity.operator)
        statement.bindText(7, entity.powers)
        statement.bindText(8, entity.powerPorts)
        statement.bindLong(9, entity.ports.toLong())
        statement.bindText(10, entity.status)
        statement.bindText(11, entity.openHours)
        statement.bindText(12, entity.updatedAt)
        statement.bindText(13, entity.source)
      }
    }
  }

  public override suspend fun insertAll(stations: List<StationEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfStationEntity.insert(_connection, stations)
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM stations"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun observeAll(): Flow<List<StationEntity>> {
    val _sql: String = "SELECT * FROM stations ORDER BY name"
    return createFlow(__db, false, arrayOf("stations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun findById(id: String): StationEntity? {
    val _sql: String = "SELECT * FROM stations WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
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
        val _result: StationEntity?
        if (_stmt.step()) {
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
          _result = StationEntity(_tmpId,_tmpName,_tmpAddress,_tmpLat,_tmpLng,_tmpOperator,_tmpPowers,_tmpPowerPorts,_tmpPorts,_tmpStatus,_tmpOpenHours,_tmpUpdatedAt,_tmpSource)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun search(query: String): List<StationEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM stations
        |        WHERE name LIKE '%' || ? || '%'
        |           OR address LIKE '%' || ? || '%'
        |           OR operator LIKE '%' || ? || '%'
        |        ORDER BY name
        |        LIMIT 50
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        _argIndex = 3
        _stmt.bindText(_argIndex, query)
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

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM stations"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
