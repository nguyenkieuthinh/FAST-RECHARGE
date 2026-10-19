package com.thinhnk.fastapp

import android.app.Application
import com.thinhnk.fastapp.auth.AuthRepository
import com.thinhnk.fastapp.auth.UserProfileRepository
import com.thinhnk.fastapp.data.AppDatabase
import com.thinhnk.fastapp.data.SavedDao
import com.thinhnk.fastapp.data.SeedImporter
import com.thinhnk.fastapp.data.StationDao
import com.google.firebase.FirebaseApp
import org.maplibre.android.MapLibre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FastApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val stationDao: StationDao by lazy { database.stationDao() }
    val savedDao: SavedDao by lazy { database.savedDao() }

    /** false khi chưa đặt app/google-services.json */
    val firebaseReady: Boolean by lazy { FirebaseApp.getApps(this).isNotEmpty() }

    val authRepository: AuthRepository by lazy { AuthRepository() }
    val profileRepository: UserProfileRepository by lazy { UserProfileRepository() }

    override fun onCreate() {
        super.onCreate()

        // Bắt buộc trước khi tạo MapView của MapLibre.
        MapLibre.getInstance(this)

        appScope.launch {
            runCatching { SeedImporter(this@FastApp, stationDao).importIfNeeded() }
        }
    }
}
