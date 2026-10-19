package com.thinhnk.fastapp.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.gson.JsonObject
import com.thinhnk.fastapp.FastApp
import com.thinhnk.fastapp.R
import com.thinhnk.fastapp.data.DEFAULT_LIST_ID
import com.thinhnk.fastapp.data.SavedItemEntity
import com.thinhnk.fastapp.data.StationEntity
import com.thinhnk.fastapp.data.ensureDefaultList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.iconSize
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textFont
import org.maplibre.android.style.layers.PropertyFactory.textHaloColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloWidth
import org.maplibre.android.style.layers.PropertyFactory.textIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonOptions
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val STYLE_URL = "https://tiles.openfreemap.org/styles/bright"

private const val SRC_STATIONS = "stations"
private const val SRC_USER = "user-location"
private const val SRC_SELECTED = "selected-station"

private const val LYR_CLUSTERS = "station-clusters"
private const val LYR_CLUSTER_COUNT = "station-cluster-count"
private const val LYR_EXCLUDED = "station-excluded"
private const val LYR_SELECTED = "selected-halo"
private const val LYR_STATIONS = "station-icons"
private const val LYR_USER = "user-dot"

private const val ICON_DC = "station-dc"
private const val ICON_AC = "station-ac"
private const val ICON_OFF = "station-off"

/** Bán kính danh sách "trạm gần bạn". */
private const val NEARBY_RADIUS_METERS = 5_000.0

/** Bán kính vuông vùng chạm quanh một icon trạm. */
private const val TOUCH_RADIUS_DP = 28f

/** Việc người dùng cần làm trong Cài đặt để ứng dụng dùng được vị trí. */
private enum class LocationPrompt { PERMISSION, SERVICES }

private fun isLocationEnabled(context: Context): Boolean {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        ?: return false
    return runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            manager.isLocationEnabled
        } else {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }.getOrDefault(false)
}

/**
 * Người dùng có thể chọn "Vị trí gần đúng" (approximate) trong hộp thoại hệ thống;
 * khi đó chỉ ACCESS_COARSE_LOCATION được cấp. Vẫn đủ để xem trạm gần bạn, nên
 * chấp nhận cả quyền gần đúng thay vì bắt người dùng vào Cài đặt bật lại.
 */
private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(app: FastApp, stations: List<StationEntity>) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf<List<StationEntity>>(emptyList()) }
    var selected by remember { mutableStateOf<StationEntity?>(null) }
    var onlyDc by rememberSaveable { mutableStateOf(false) }
    var onlyActive by rememberSaveable { mutableStateOf(false) }
    var listId by remember { mutableStateOf(DEFAULT_LIST_ID) }

    var mapLibre by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var autoCentered by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    var locationEnabled by remember { mutableStateOf(isLocationEnabled(context)) }
    var locationPrompt by remember { mutableStateOf<LocationPrompt?>(null) }
    var requestFromUser by remember { mutableStateOf(false) }

    val mapView = remember {
        MapView(context).apply { onCreate(null) }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    // Có thể người dùng vừa bật/tắt quyền hoặc vị trí trong Cài đặt.
                    locationGranted = hasLocationPermission(context)
                    locationEnabled = isLocationEnabled(context)
                }
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(Unit) {
        listId = runCatching { app.savedDao.ensureDefaultList().id }.getOrDefault(DEFAULT_LIST_ID)
    }

    val filteredStations = remember(stations, onlyDc, onlyActive) {
        stations.filter { station ->
            (!onlyDc || station.isDc) && (!onlyActive || station.status == "active")
        }
    }
    val filteredState = rememberUpdatedState(filteredStations)

    val savedIds by remember(listId) { app.savedDao.observeItemIds(listId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // Trạm trong bán kính 5 km quanh vị trí hiện tại, gần nhất trước.
    val nearby = remember(stations, userLocation, onlyDc, onlyActive) {
        val location = userLocation ?: return@remember emptyList()
        stations.asSequence()
            .filter { (!onlyDc || it.isDc) && (!onlyActive || it.status == "active") }
            .map { NearbyStation(it, distanceMeters(location.latitude, location.longitude, it.lat, it.lng)) }
            .filter { it.distanceMeters <= NEARBY_RADIUS_METERS }
            .sortedBy { it.distanceMeters }
            .take(40)
            .toList()
    }

    val onMapTap: (LatLng) -> Unit = { latLng ->
        val map = mapLibre
        if (map != null) {
            val screen = map.projection.toScreenLocation(latLng)
            val radius = with(density) { TOUCH_RADIUS_DP.dp.toPx() }
            val rect = RectF(
                screen.x - radius,
                screen.y - radius,
                screen.x + radius,
                screen.y + radius,
            )

            val clusters = map.queryRenderedFeatures(rect, LYR_CLUSTERS)
            if (clusters.isNotEmpty()) {
                val nextZoom = (map.cameraPosition.zoom + 2.0).coerceAtMost(17.5)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, nextZoom))
            } else {
                val hits = map.queryRenderedFeatures(rect, LYR_STATIONS)
                val id = hits.firstOrNull()?.getStringProperty("id")
                val station = stations.firstOrNull { it.id == id }
                if (station != null) {
                    selected = station
                    map.animateCamera(
                        CameraUpdateFactory.newLatLng(LatLng(station.lat, station.lng)),
                    )
                } else {
                    // Chạm vào chỗ trống trên bản đồ cũng đóng thẻ trạm.
                    selected = null
                }
            }
        }
    }
    val tapState = rememberUpdatedState(onMapTap)

    DisposableEffect(mapView) {
        mapView.getMapAsync { map ->
            mapLibre = map
            map.uiSettings.isCompassEnabled = true
            map.uiSettings.isAttributionEnabled = true
            map.uiSettings.isLogoEnabled = false
            map.cameraPosition = CameraPosition.Builder()
                .target(LatLng(16.0, 106.0))
                .zoom(5.8)
                .build()

            map.setStyle(Style.Builder().fromUri(STYLE_URL)) { loaded ->
                mapStyle = loaded
                installMapStyle(loaded, context)
                loaded.getSourceAs<GeoJsonSource>(SRC_STATIONS)
                    ?.setGeoJson(stationFeatures(filteredState.value))
            }

            map.addOnMapClickListener { latLng ->
                tapState.value(latLng)
                true
            }
        }

        onDispose { }
    }

    // Cập nhật dữ liệu trạm mỗi khi bộ lọc thay đổi.
    LaunchedEffect(mapStyle, filteredStations) {
        mapStyle?.getSourceAs<GeoJsonSource>(SRC_STATIONS)
            ?.setGeoJson(stationFeatures(filteredStations))
    }

    // Vòng sáng quanh trạm đang chọn.
    LaunchedEffect(mapStyle, selected) {
        val collection = selected?.let {
            FeatureCollection.fromFeatures(listOf(stationFeature(it)))
        } ?: FeatureCollection.fromFeatures(emptyList())
        mapStyle?.getSourceAs<GeoJsonSource>(SRC_SELECTED)?.setGeoJson(collection)
    }

    // Chấm vị trí hiện tại.
    LaunchedEffect(mapStyle, userLocation) {
        val location = userLocation ?: return@LaunchedEffect
        mapStyle?.getSourceAs<GeoJsonSource>(SRC_USER)
            ?.setGeoJson(Point.fromLngLat(location.longitude, location.latitude))
    }

    // Căn về vị trí người dùng một lần, ngay khi cả bản đồ và GPS đều sẵn sàng.
    LaunchedEffect(mapLibre, userLocation) {
        val map = mapLibre ?: return@LaunchedEffect
        val location = userLocation ?: return@LaunchedEffect
        if (!autoCentered) {
            autoCentered = true
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    15.0,
                ),
            )
        }
    }

    DisposableEffect(locationGranted, locationEnabled) {
        if (!locationGranted || !locationEnabled) return@DisposableEffect onDispose { }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@DisposableEffect onDispose { }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                userLocation = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            override fun onProviderEnabled(provider: String) = Unit
            override fun onProviderDisabled(provider: String) = Unit
        }

        runCatching {
            manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2_000L,
                5f,
                listener,
                Looper.getMainLooper(),
            )
        }
        runCatching {
            manager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                2_000L,
                5f,
                listener,
                Looper.getMainLooper(),
            )
        }
        runCatching {
            manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let(listener::onLocationChanged)
        }
        runCatching {
            manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let(listener::onLocationChanged)
        }

        onDispose { runCatching { manager.removeUpdates(listener) } }
    }

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        // Người dùng có thể chọn "Vị trí gần đúng": khi đó FINE bị từ chối
        // nhưng COARSE vẫn được cấp và vẫn dùng được.
        locationGranted = granted || hasLocationPermission(context)
        locationEnabled = isLocationEnabled(context)
        when {
            !locationGranted && requestFromUser ->
                // Hệ thống không mở hộp thoại xin quyền nữa (đã từ chối vĩnh
                // viễn), hoặc người dùng vừa từ chối -> hướng dẫn vào Cài đặt.
                locationPrompt = LocationPrompt.PERMISSION

            locationGranted && !locationEnabled ->
                // Đã có quyền nhưng GPS đang tắt -> hướng dẫn bật ngay,
                // không bắt người dùng phải bấm nút vị trí thêm lần nữa.
                locationPrompt = LocationPrompt.SERVICES
        }
        requestFromUser = false
    }

    // Bật/tắt Vị trí từ thanh cài đặt nhanh không làm activity resume,
    // nên theo dõi broadcast để icon và luồng cập nhật vị trí đổi ngay.
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                if (receiverContext != null) {
                    locationEnabled = isLocationEnabled(receiverContext)
                }
            }
        }
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }

    fun centerOnUser() {
        val granted = hasLocationPermission(context)
        locationGranted = granted

        if (!granted) {
            requestFromUser = true
            locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        locationEnabled = isLocationEnabled(context)
        if (!locationEnabled) {
            locationPrompt = LocationPrompt.SERVICES
            return
        }

        val map = mapLibre ?: return
        val known = userLocation
        if (known != null) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(known.latitude, known.longitude), 15.0),
            )
            return
        }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
        val last = runCatching {
            manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }.getOrNull() ?: return

        userLocation = last
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(last.latitude, last.longitude), 15.0),
        )
    }

    LaunchedEffect(Unit) {
        if (!locationGranted) {
            locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(query, onlyDc, onlyActive) {
        val text = query.trim()
        if (text.length < 2) {
            results = emptyList()
            return@LaunchedEffect
        }
        delay(250)
        results = runCatching { app.stationDao.search(text) }
            .getOrDefault(emptyList())
            .filter { (!onlyDc || it.isDc) && (!onlyActive || it.status == "active") }
            .take(30)
    }

    // Giữ trạm gần nhất để thẻ còn nội dung trong lúc chạy animation đóng.
    var cardStation by remember { mutableStateOf<StationEntity?>(null) }
    LaunchedEffect(selected) {
        if (selected != null) cardStation = selected
    }

    // targetState đổi ngay khi chọn/bỏ chọn trạm; currentState chỉ đổi khi
    // animation chạy xong, nhờ đó danh sách gần đây chỉ hiện lại sau khi
    // thẻ trạm đã trượt xuống hẳn.
    val cardTransition = remember { MutableTransitionState(false) }
    cardTransition.targetState = selected != null

    val showNearby = selected == null &&
        !cardTransition.currentState &&
        cardTransition.isIdle &&
        nearby.isNotEmpty()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true,
    )
    val sheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    // Khi danh sách xuất hiện, chỉ mở ở mức thu gọn để không che bản đồ.
    LaunchedEffect(showNearby) {
        if (showNearby) sheetState.partialExpand()
    }

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetPeekHeight = if (showNearby) 84.dp else 0.dp,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {
            if (showNearby) {
                NearbySheetContent(
                    nearby = nearby,
                    onSelect = { station ->
                        selected = station
                        mapLibre?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(station.lat, station.lng), 15.0),
                        )
                    },
                )
            }
        },
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
            )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = onlyDc,
                    onClick = { onlyDc = !onlyDc },
                    label = { Text("DC nhanh") },
                )
                FilterChip(
                    selected = onlyActive,
                    onClick = { onlyActive = !onlyActive },
                    label = { Text("Đang hoạt động") },
                )
            }

            if (results.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(results, key = { it.id }) { station ->
                            TextButton(
                                onClick = {
                                    selected = station
                                    query = ""
                                    results = emptyList()
                                    mapLibre?.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(station.lat, station.lng),
                                            15.0,
                                        ),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = station.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = listOfNotNull(
                                            station.operator.takeIf { it.isNotBlank() },
                                            station.address.takeIf { it.isNotBlank() },
                                        ).joinToString(" · "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val locationAvailable = locationGranted && locationEnabled
        val locationIconTint = if (locationAvailable) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        }
        val slashColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)

        FloatingActionButton(
            onClick = { centerOnUser() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (showNearby) 100.dp else 16.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = if (locationAvailable) {
                        "Vị trí của tôi"
                    } else {
                        "Vị trí của tôi (chưa bật vị trí)"
                    },
                    tint = locationIconTint,
                )
                if (!locationAvailable) {
                    // Gạch chéo giống Google Maps khi không có vị trí.
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawLine(
                            color = slashColor,
                            start = Offset(size.width * 0.12f, size.height * 0.88f),
                            end = Offset(size.width * 0.88f, size.height * 0.12f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visibleState = cardTransition,
            enter = slideInVertically(animationSpec = tween(240)) { it } +
                fadeIn(animationSpec = tween(160)),
            exit = slideOutVertically(animationSpec = tween(220)) { it } +
                fadeOut(animationSpec = tween(160)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            label = "stationCard",
        ) {
            (selected ?: cardStation)?.let { station ->
                val distance = userLocation?.let {
                    distanceMeters(it.latitude, it.longitude, station.lat, station.lng)
                }
                StationCard(
                    station = station,
                    distanceMeters = distance,
                    saved = station.id in savedIds,
                    onClose = { selected = null },
                    onNavigate = {
                        val uri = Uri.parse(
                            "geo:${station.lat},${station.lng}" +
                                "?q=${station.lat},${station.lng}(${Uri.encode(station.name)})",
                        )
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                    },
                    onToggleSave = {
                        scope.launch {
                            if (station.id in savedIds) {
                                app.savedDao.removeItem(listId, station.id)
                            } else {
                                app.savedDao.addItem(
                                    SavedItemEntity(
                                        listId = listId,
                                        stationId = station.id,
                                        addedAt = System.currentTimeMillis(),
                                    ),
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        when (locationPrompt) {
            LocationPrompt.PERMISSION -> AlertDialog(
                onDismissRequest = { locationPrompt = null },
                title = { Text("Cần quyền vị trí") },
                text = {
                    Text(
                        "Ứng dụng cần quyền vị trí để hiện trạm gần bạn và đưa bản đồ " +
                            "về chỗ bạn đang đứng. Hãy bật quyền Vị trí cho ứng dụng " +
                            "trong Cài đặt.",
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            locationPrompt = null
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            )
                            runCatching { context.startActivity(intent) }
                        },
                    ) { Text("Mở cài đặt") }
                },
                dismissButton = {
                    TextButton(onClick = { locationPrompt = null }) { Text("Để sau") }
                },
            )

            LocationPrompt.SERVICES -> AlertDialog(
                onDismissRequest = { locationPrompt = null },
                title = { Text("Vị trí đang tắt") },
                text = {
                    Text(
                        "Vị trí (GPS) của máy đang tắt nên chưa xác định được bạn ở đâu. " +
                            "Hãy bật Vị trí rồi quay lại ứng dụng.",
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            locationPrompt = null
                            runCatching {
                                context.startActivity(
                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                )
                            }
                        },
                    ) { Text("Bật vị trí") }
                },
                dismissButton = {
                    TextButton(onClick = { locationPrompt = null }) { Text("Để sau") }
                },
            )

            null -> Unit
        }
        }
    }
}

private data class NearbyStation(
    val station: StationEntity,
    val distanceMeters: Double,
)

@Composable
private fun NearbySheetContent(
    nearby: List<NearbyStation>,
    onSelect: (StationEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Trạm gần bạn (≤ 5 km)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${nearby.size} trạm",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                items(nearby, key = { it.station.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(item.station) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(stationColor(item.station), CircleShape),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName(item.station.name, item.station.address),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = item.station.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(
                            text = formatDistance(item.distanceMeters),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    HorizontalDivider()
                }
            }

        Text(
            text = stringResource(R.string.map_credit),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StationCard(
    station: StationEntity,
    distanceMeters: Double?,
    saved: Boolean,
    onClose: () -> Unit,
    onNavigate: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName(station.name, station.address),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = statusLabel(station.status),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = distanceMeters?.let { "Cách bạn ${formatDistance(it)}" }
                            ?: "Chưa xác định được vị trí của bạn",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                TextButton(onClick = onClose) { Text("Đóng") }
            }

            HorizontalDivider()

            Text(
                text = "Chi tiết trạm",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )

            DetailRow("Địa chỉ", station.address)
            DetailRow("Nhà vận hành", station.operator)
            DetailRow(
                label = "Cổng sạc",
                value = if (station.ports > 0) "${station.ports} cổng" else "",
            )
            DetailRow("Công suất", powerBreakdown(station))
            DetailRow("Mở cửa", hoursLabel(station.openHours))
            DetailRow("Truy cập", accessLabel(station.openHours))
            DetailRow("Cập nhật", station.updatedAt)
            DetailRow("Nguồn", "EVCS (${station.source})")

            Text(
                text = "Khoảng cách tính theo đường thẳng. Giá sạc và số cổng " +
                    "đang trống chưa có trong dữ liệu — nên kiểm tra app của " +
                    "hãng trước khi đi.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider()

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onToggleSave) {
                    Text(if (saved) "Bỏ lưu" else "Lưu trạm")
                }
                OutlinedButton(onClick = onNavigate) {
                    Text("Đi tới")
                }
            }

            Text(
                text = stringResource(R.string.map_credit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Icon tròn có tia sét, màu theo loại trạm. */
private fun stationIcon(context: Context, fillColor: Int): Bitmap {
    val density = context.resources.displayMetrics.density
    val size = (40 * density).toInt().coerceAtLeast(24)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = size / 2f

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.color = 0x33000000
    canvas.drawCircle(center, center + 1.5f * density, center - 2f * density, paint)

    paint.color = Color.WHITE
    canvas.drawCircle(center, center, center - 1f * density, paint)

    paint.color = fillColor
    canvas.drawCircle(center, center, center - 3.5f * density, paint)

    val bolt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    val w = size * 0.22f
    val h = size * 0.50f
    val path = Path().apply {
        moveTo(center + w * 0.35f, center - h * 0.55f)
        lineTo(center - w * 0.75f, center + h * 0.12f)
        lineTo(center - w * 0.10f, center + h * 0.12f)
        lineTo(center - w * 0.35f, center + h * 0.55f)
        lineTo(center + w * 0.75f, center - h * 0.12f)
        lineTo(center + w * 0.10f, center - h * 0.12f)
        close()
    }
    canvas.drawPath(path, bolt)
    return bitmap
}

private fun installMapStyle(style: Style, context: Context) {
    style.addImage(ICON_DC, stationIcon(context, 0xFFF97316.toInt()))
    style.addImage(ICON_AC, stationIcon(context, 0xFF2563EB.toInt()))
    style.addImage(ICON_OFF, stationIcon(context, 0xFF6B7280.toInt()))

    val options = GeoJsonOptions()
        .withCluster(true)
        .withClusterRadius(50)
        .withClusterMaxZoom(14)
        .withClusterMinPoints(2)

    style.addSource(
        GeoJsonSource(
            SRC_STATIONS,
            FeatureCollection.fromFeatures(emptyList<Feature>()),
            options,
        ),
    )
    style.addSource(GeoJsonSource(SRC_USER, Point.fromLngLat(0.0, 0.0)))
    style.addSource(
        GeoJsonSource(
            SRC_SELECTED,
            FeatureCollection.fromFeatures(emptyList<Feature>()),
        ),
    )

    val clusterLayer = CircleLayer(LYR_CLUSTERS, SRC_STATIONS)
        .withFilter(Expression.has("point_count"))
        .withProperties(
            circleColor(0xFF1D4ED8.toInt()),
            circleOpacity(0.92f),
            circleRadius(
                Expression.interpolate(
                    Expression.linear(),
                    Expression.get("point_count"),
                    Expression.stop(2, 16f),
                    Expression.stop(20, 22f),
                    Expression.stop(100, 28f),
                ),
            ),
            circleStrokeColor(Color.WHITE),
            circleStrokeWidth(2f),
        )

    val clusterCountLayer = SymbolLayer(LYR_CLUSTER_COUNT, SRC_STATIONS)
        .withFilter(Expression.has("point_count"))
        .withProperties(
            textField(Expression.toString(Expression.get("point_count"))),
            textFont(arrayOf("Noto Sans Bold")),
            textSize(12f),
            textColor(Color.WHITE),
            textHaloColor(0x55000000),
            textHaloWidth(0.5f),
            textAllowOverlap(true),
            textIgnorePlacement(true),
        )

    val selectedLayer = CircleLayer(LYR_SELECTED, SRC_SELECTED)
        .withProperties(
            circleRadius(21f),
            circleColor(0x55F97316),
            circleStrokeWidth(2f),
            circleStrokeColor(0xCCF97316.toInt()),
        )

    val stationLayer = SymbolLayer(LYR_STATIONS, SRC_STATIONS)
        .withFilter(Expression.has("id"))
        .withProperties(
            iconImage(
                Expression.switchCase(
                    Expression.eq(
                        Expression.get("status"),
                        Expression.literal("maintenance"),
                    ),
                    Expression.literal(ICON_OFF),
                    Expression.eq(Expression.get("dc"), true),
                    Expression.literal(ICON_DC),
                    Expression.literal(ICON_AC),
                ),
            ),
            iconSize(1f),
            iconAllowOverlap(true),
            iconIgnorePlacement(true),
        )

    val userLayer = CircleLayer(LYR_USER, SRC_USER)
        .withProperties(
            circleRadius(7f),
            circleColor(0xFF1A73E8.toInt()),
            circleStrokeColor(Color.WHITE),
            circleStrokeWidth(3f),
        )

    style.addLayer(clusterLayer)
    style.addLayer(clusterCountLayer)
    style.addLayer(selectedLayer)
    style.addLayer(stationLayer)
    style.addLayer(userLayer)
}

private fun stationFeatures(stations: List<StationEntity>): FeatureCollection =
    FeatureCollection.fromFeatures(stations.map(::stationFeature))

private fun stationFeature(station: StationEntity): Feature =
    Feature.fromGeometry(
        Point.fromLngLat(station.lng, station.lat),
        JsonObject().apply {
            addProperty("id", station.id)
            addProperty("dc", station.isDc)
            addProperty("status", station.status)
        },
    )

@Composable
private fun stationColor(station: StationEntity): androidx.compose.ui.graphics.Color = when {
    station.status == "maintenance" -> androidx.compose.ui.graphics.Color(0xFF6B7280)
    station.isDc -> androidx.compose.ui.graphics.Color(0xFFF97316)
    else -> androidx.compose.ui.graphics.Color(0xFF2563EB)
}

private fun statusLabel(status: String): String = when (status) {
    "maintenance" -> "Đang bảo trì"
    "busy" -> "Đang bận"
    else -> "Đang hoạt động"
}

/** Người Việt đọc số thập phân bằng dấu phẩy ("1,4 km", "3,5 kW"). */
private val VIETNAMESE_LOCALE: Locale = Locale.forLanguageTag("vi-VN")

/**
 * Tên trong dữ liệu nguồn đôi khi chép cả địa chỉ vào tên trạm
 * ("NQ Cty TNHH Charging Station, 546 Hùng Vương, ..."). Cắt phần đuôi
 * trùng địa chỉ để thẻ không hiện cùng một nội dung hai lần.
 */
private fun displayName(name: String, address: String): String {
    if (address.isBlank()) return name
    val parts = name.split(',').map { it.trim() }
    for (index in 1 until parts.size) {
        val tail = parts.drop(index).joinToString(", ")
        if (tail.length >= 8 && address.contains(tail, ignoreCase = true)) {
            return parts.take(index).joinToString(", ").trim()
        }
    }
    return name
}

/** "2 cổng 60 kW · 2 cổng 7 kW" — cho biết loại cổng nào có bao nhiêu. */
private fun powerBreakdown(station: StationEntity): String {
    val pairs = station.powerPortList.sortedByDescending { it.first }
    if (pairs.isNotEmpty()) {
        return pairs.joinToString(" · ") { (power, count) ->
            "$count cổng ${formatPower(power)} kW"
        }
    }
    return station.powerList.sortedDescending()
        .joinToString(" · ") { "${formatPower(it)} kW" }
}

/** Nguồn ghi "24/7 • Công cộng": tách giờ mở cửa và hình thức truy cập. */
private fun hoursLabel(openHours: String): String = openHours.substringBefore('•').trim()

private fun accessLabel(openHours: String): String =
    openHours.substringAfter('•', "").trim()

@Composable
private fun DetailRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            modifier = Modifier.width(96.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun formatPower(value: Double): String =
    if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(VIETNAMESE_LOCALE, "%.1f", value)
    }

private fun formatDistance(meters: Double): String =
    if (meters < 1_000) {
        "${meters.roundToInt()} m"
    } else {
        String.format(VIETNAMESE_LOCALE, "%.1f km", meters / 1_000)
    }

private fun distanceMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
): Double {
    val earthRadius = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).let { it * it } +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(dLon / 2).let { it * it }
    return 2 * earthRadius * asin(sqrt(a))
}
