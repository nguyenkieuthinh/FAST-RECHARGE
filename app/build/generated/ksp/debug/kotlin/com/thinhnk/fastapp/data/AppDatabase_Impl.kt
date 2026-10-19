package com.thinhnk.fastapp.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _stationDao: Lazy<StationDao> = lazy {
    StationDao_Impl(this)
  }

  private val _savedDao: Lazy<SavedDao> = lazy {
    SavedDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "5e7466cec0361cf5c652343a9faa9179", "b5733fa3a021c454e26e69f138bce1b7") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `stations` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `address` TEXT NOT NULL, `lat` REAL NOT NULL, `lng` REAL NOT NULL, `operator` TEXT NOT NULL, `powers` TEXT NOT NULL, `powerPorts` TEXT NOT NULL DEFAULT '', `ports` INTEGER NOT NULL, `status` TEXT NOT NULL, `openHours` TEXT NOT NULL, `updatedAt` TEXT NOT NULL DEFAULT '', `source` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stations_name` ON `stations` (`name`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stations_operator` ON `stations` (`operator`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stations_lat` ON `stations` (`lat`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stations_lng` ON `stations` (`lng`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stations_status` ON `stations` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `saved_lists` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `saved_items` (`listId` TEXT NOT NULL, `stationId` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`listId`, `stationId`), FOREIGN KEY(`listId`) REFERENCES `saved_lists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_items_stationId` ON `saved_items` (`stationId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5e7466cec0361cf5c652343a9faa9179')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `stations`")
        connection.execSQL("DROP TABLE IF EXISTS `saved_lists`")
        connection.execSQL("DROP TABLE IF EXISTS `saved_items`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsStations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStations.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("address", TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("lat", TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("lng", TableInfo.Column("lng", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("operator", TableInfo.Column("operator", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("powers", TableInfo.Column("powers", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("powerPorts", TableInfo.Column("powerPorts", "TEXT", true, 0, "''", TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("ports", TableInfo.Column("ports", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("openHours", TableInfo.Column("openHours", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", true, 0, "''", TableInfo.CREATED_FROM_ENTITY))
        _columnsStations.put("source", TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStations: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesStations.add(TableInfo.Index("index_stations_name", false, listOf("name"), listOf("ASC")))
        _indicesStations.add(TableInfo.Index("index_stations_operator", false, listOf("operator"), listOf("ASC")))
        _indicesStations.add(TableInfo.Index("index_stations_lat", false, listOf("lat"), listOf("ASC")))
        _indicesStations.add(TableInfo.Index("index_stations_lng", false, listOf("lng"), listOf("ASC")))
        _indicesStations.add(TableInfo.Index("index_stations_status", false, listOf("status"), listOf("ASC")))
        val _infoStations: TableInfo = TableInfo("stations", _columnsStations, _foreignKeysStations, _indicesStations)
        val _existingStations: TableInfo = read(connection, "stations")
        if (!_infoStations.equals(_existingStations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |stations(com.thinhnk.fastapp.data.StationEntity).
              | Expected:
              |""".trimMargin() + _infoStations + """
              |
              | Found:
              |""".trimMargin() + _existingStations)
        }
        val _columnsSavedLists: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSavedLists.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedLists.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedLists.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedLists: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSavedLists: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSavedLists: TableInfo = TableInfo("saved_lists", _columnsSavedLists, _foreignKeysSavedLists, _indicesSavedLists)
        val _existingSavedLists: TableInfo = read(connection, "saved_lists")
        if (!_infoSavedLists.equals(_existingSavedLists)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |saved_lists(com.thinhnk.fastapp.data.SavedListEntity).
              | Expected:
              |""".trimMargin() + _infoSavedLists + """
              |
              | Found:
              |""".trimMargin() + _existingSavedLists)
        }
        val _columnsSavedItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSavedItems.put("listId", TableInfo.Column("listId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedItems.put("stationId", TableInfo.Column("stationId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedItems.put("addedAt", TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysSavedItems.add(TableInfo.ForeignKey("saved_lists", "CASCADE", "NO ACTION", listOf("listId"), listOf("id")))
        val _indicesSavedItems: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSavedItems.add(TableInfo.Index("index_saved_items_stationId", false, listOf("stationId"), listOf("ASC")))
        val _infoSavedItems: TableInfo = TableInfo("saved_items", _columnsSavedItems, _foreignKeysSavedItems, _indicesSavedItems)
        val _existingSavedItems: TableInfo = read(connection, "saved_items")
        if (!_infoSavedItems.equals(_existingSavedItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |saved_items(com.thinhnk.fastapp.data.SavedItemEntity).
              | Expected:
              |""".trimMargin() + _infoSavedItems + """
              |
              | Found:
              |""".trimMargin() + _existingSavedItems)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "stations", "saved_lists", "saved_items")
  }

  public override fun clearAllTables() {
    super.performClear(true, "stations", "saved_lists", "saved_items")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(StationDao::class, StationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SavedDao::class, SavedDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun stationDao(): StationDao = _stationDao.value

  public override fun savedDao(): SavedDao = _savedDao.value
}
