package com.thinhnk.fastapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Nạp snapshot trạm đóng gói trong APK ở lần mở đầu tiên.
 * File nguồn: app/src/main/assets/stations_seed.json (sinh bởi tools/build_seed.py).
 * Không dùng đuôi .gz: AAPT2 tự giải nén asset .gz và bỏ đuôi khi đóng gói APK.
 */
class SeedImporter(
    private val context: Context,
    private val stationDao: StationDao,
) {

    suspend fun importIfNeeded(): Int = withContext(Dispatchers.IO) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val installedVersion = preferences.getInt(KEY_SEED_VERSION, 0)
        if (stationDao.count() > 0 && installedVersion >= SEED_VERSION) {
            return@withContext 0
        }

        val text = context.assets.open(ASSET_NAME).use { input ->
            input.bufferedReader().use(BufferedReader::readText)
        }

        val array = JSONArray(text)
        val stations = ArrayList<StationEntity>(array.length())

        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val powers = item.optJSONArray("powers") ?: JSONArray()
            val powerText = buildList {
                for (powerIndex in 0 until powers.length()) {
                    add(formatPower(powers.optDouble(powerIndex, 0.0)))
                }
            }.joinToString(",")

            val powerPortArray = item.optJSONArray("powerPorts") ?: JSONArray()
            val powerPortText = buildList {
                for (pairIndex in 0 until powerPortArray.length()) {
                    val pair = powerPortArray.optJSONArray(pairIndex) ?: continue
                    val power = pair.optDouble(0, Double.NaN)
                    val count = pair.optInt(1, 0)
                    if (!power.isNaN() && count > 0) {
                        add("${formatPower(power)}:$count")
                    }
                }
            }.joinToString(",")

            stations += StationEntity(
                id = item.getString("id"),
                name = item.getString("name"),
                address = item.optString("address"),
                lat = item.getDouble("lat"),
                lng = item.getDouble("lng"),
                operator = item.optString("operator", "Khác"),
                powers = powerText,
                powerPorts = powerPortText,
                ports = item.optInt("ports", 0),
                status = item.optString("status", "active"),
                openHours = item.optString("openHours"),
                updatedAt = item.optString("updatedAt"),
                source = item.optString("source", "evcs.vn"),
            )
        }

        // ID trạm ổn định theo tên + toạ độ nên danh sách đã lưu vẫn khớp
        // sau khi nạp lại snapshot mới.
        if (stationDao.count() > 0) stationDao.deleteAll()
        stationDao.insertAll(stations)
        preferences.edit().putInt(KEY_SEED_VERSION, SEED_VERSION).apply()
        stations.size
    }

    private fun formatPower(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

    private companion object {
        const val ASSET_NAME = "stations_seed.json"

        /** Tăng khi snapshot trong assets đổi cấu trúc/dữ liệu cần nạp lại. */
        const val SEED_VERSION = 2
        const val PREFS_NAME = "seed_import"
        const val KEY_SEED_VERSION = "version"
    }
}
