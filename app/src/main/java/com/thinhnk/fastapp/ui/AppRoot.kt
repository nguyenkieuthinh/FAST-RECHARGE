package com.thinhnk.fastapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thinhnk.fastapp.FastApp
import com.thinhnk.fastapp.R

@Composable
fun AppRoot() {
    val app = LocalContext.current.applicationContext as FastApp

    if (!app.firebaseReady) {
        SetupScreen()
        return
    }

    val auth = remember { app.authRepository }
    val user by auth.authState.collectAsStateWithLifecycle(initialValue = auth.currentUser)

    LaunchedEffect(user?.uid) {
        user?.let { runCatching { app.profileRepository.ensureProfile(it) } }
    }

    if (user == null) {
        AuthScreen(auth)
    } else {
        MainScaffold(app = app, user = user!!)
    }
}

@Composable
private fun SetupScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = androidx.compose.ui.platform.LocalContext.current.getString(R.string.firebase_missing_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = androidx.compose.ui.platform.LocalContext.current.getString(R.string.firebase_missing_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
