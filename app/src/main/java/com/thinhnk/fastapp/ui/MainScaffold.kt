package com.thinhnk.fastapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseUser
import com.thinhnk.fastapp.FastApp
import com.thinhnk.fastapp.R
import com.thinhnk.fastapp.data.ensureDefaultList

@Composable
fun MainScaffold(app: FastApp, user: FirebaseUser) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val stations by app.stationDao.observeAll().collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(Unit) {
        runCatching { app.savedDao.ensureDefaultList() }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Icon(Icons.Filled.Place, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_map)) },
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_saved)) },
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_profile)) },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (tab) {
                0 -> MapScreen(app = app, stations = stations)
                1 -> SavedScreen(app = app)
                else -> ProfileScreen(app = app, user = user)
            }
        }
    }
}
