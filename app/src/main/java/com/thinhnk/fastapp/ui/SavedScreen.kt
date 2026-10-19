package com.thinhnk.fastapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thinhnk.fastapp.FastApp
import com.thinhnk.fastapp.R
import com.thinhnk.fastapp.data.DEFAULT_LIST_ID
import com.thinhnk.fastapp.data.StationEntity
import com.thinhnk.fastapp.data.ensureDefaultList
import kotlinx.coroutines.launch

@Composable
fun SavedScreen(app: FastApp) {
    val scope = rememberCoroutineScope()
    var listId by remember { mutableStateOf(DEFAULT_LIST_ID) }

    LaunchedEffect(Unit) {
        listId = runCatching { app.savedDao.ensureDefaultList().id }.getOrDefault(DEFAULT_LIST_ID)
    }

    val savedStations by app.savedDao
        .observeStations(listId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.tab_saved),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.saved_dev_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        text = stringResource(R.string.saved_dev_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }

        if (savedStations.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.saved_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                )
            }
        } else {
            items(savedStations, key = { it.id }) { station ->
                SavedStationCard(
                    station = station,
                    onRemove = {
                        scope.launch {
                            runCatching { app.savedDao.removeItem(listId, station.id) }
                        }
                    },
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.data_credit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun SavedStationCard(
    station: StationEntity,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onRemove) {
                    Text("Bỏ lưu")
                }
            }

            if (station.address.isNotBlank()) {
                Text(
                    text = station.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = buildString {
                    if (station.operator.isNotBlank()) append(station.operator)
                    val powers = station.powerList
                    if (powers.isNotEmpty()) {
                        if (isNotEmpty()) append(" · ")
                        append(powers.joinToString(", "))
                        append(" kW")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
