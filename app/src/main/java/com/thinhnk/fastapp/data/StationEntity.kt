package com.thinhnk.fastapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Một trạm sạc lấy từ snapshot EVCS.
 *
 * `powers` lưu dạng "60,120" để Room không cần bảng phụ.
 * `source` giữ nguyên nguồn để màn chi tiết luôn ghi công.
 */
@Entity(
    tableName = "stations",
    indices = [
        Index("name"),
        Index("operator"),
        Index("lat"),
        Index("lng"),
        Index("status"),
    ],
)
data class StationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val operator: String,
    val powers: String,
    /** Số cổng theo từng công suất, dạng "60:2,7:2". */
    @ColumnInfo(defaultValue = "")
    val powerPorts: String,
    val ports: Int,
    val status: String,
    val openHours: String,
    /** Ngày nguồn cập nhật dữ liệu trạm, dạng "29/6/2026". */
    @ColumnInfo(defaultValue = "")
    val updatedAt: String,
    val source: String,
) {
    val powerList: List<Double>
        get() = powers.split(',').mapNotNull { it.trim().toDoubleOrNull() }

    val powerPortList: List<Pair<Double, Int>>
        get() = powerPorts.split(',').mapNotNull { entry ->
            val parts = entry.split(':')
            val power = parts.getOrNull(0)?.trim()?.toDoubleOrNull()
            val count = parts.getOrNull(1)?.trim()?.toIntOrNull()
            if (power != null && count != null) power to count else null
        }

    val isDc: Boolean
        get() = powerList.any { it >= 60 }
}
