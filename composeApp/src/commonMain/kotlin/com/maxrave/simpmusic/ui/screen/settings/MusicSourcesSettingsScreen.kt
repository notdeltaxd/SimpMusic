package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maxrave.domain.manager.DataStoreManager
import org.koin.compose.koinInject

/**
 * Music Sources settings screen with Last.fm and JioSaavn configuration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSourcesSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val dataStoreManager: DataStoreManager = koinInject()

    // Last.fm
    val lastFmUsername by dataStoreManager.lastFmUsername.collectAsState(initial = "")
    val lastFmSessionKey by dataStoreManager.lastFmSessionKey.collectAsState(initial = "")
    val lastFmScrobbleEnabled by dataStoreManager.lastFmScrobbleEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)
    val lastFmNowPlayingEnabled by dataStoreManager.lastFmNowPlayingEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)

    // JioSaavn
    val jioSaavnEnabled by dataStoreManager.jioSaavnEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)
    val jioSaavnQuality by dataStoreManager.jioSaavnQuality.collectAsState(initial = "320kbps")

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.MUSIC_SOURCES)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.MUSIC_SOURCES.title,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            )
        ) {
            // Last.fm Section
            item {
                SettingsSectionHeader(title = "Last.fm")
            }

            item {
                LastFmCard(
                    isConnected = lastFmSessionKey.isNotBlank(),
                    username = lastFmUsername,
                    scrobbleEnabled = lastFmScrobbleEnabled == DataStoreManager.Values.TRUE,
                    nowPlayingEnabled = lastFmNowPlayingEnabled == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onConnect = { /* TODO: Launch auth flow */ },
                    onDisconnect = { /* TODO: Clear session */ },
                    onScrobbleToggle = { /* TODO: Toggle scrobble */ },
                    onNowPlayingToggle = { /* TODO: Toggle now playing */ },
                )
            }

            // JioSaavn Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(title = "JioSaavn")
            }

            item {
                JioSaavnCard(
                    enabled = jioSaavnEnabled == DataStoreManager.Values.TRUE,
                    quality = jioSaavnQuality,
                    accentColor = accentColor,
                    onEnableToggle = { /* TODO: Toggle JioSaavn */ },
                    onQualityChange = { /* TODO: Change quality */ },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LastFmCard(
    isConnected: Boolean,
    username: String,
    scrobbleEnabled: Boolean,
    nowPlayingEnabled: Boolean,
    accentColor: Color,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onScrobbleToggle: (Boolean) -> Unit,
    onNowPlayingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Connection Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                    contentDescription = null,
                    tint = if (isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Connected as $username" else "Not connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Connect/Disconnect Button
            if (isConnected) {
                OutlinedButton(
                    onClick = onDisconnect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Disconnect")
                }
            } else {
                Button(
                    onClick = onConnect,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    )
                ) {
                    Text("Connect to Last.fm", color = Color.White)
                }
            }

            if (isConnected) {
                Spacer(modifier = Modifier.height(16.dp))

                SettingsToggleItem(
                    title = "Enable scrobbling",
                    subtitle = "Track your listening history",
                    checked = scrobbleEnabled,
                    accentColor = accentColor,
                    onCheckedChange = onScrobbleToggle
                )

                SettingsToggleItem(
                    title = "Update Now Playing",
                    subtitle = "Show what you're currently listening to",
                    checked = nowPlayingEnabled,
                    accentColor = accentColor,
                    onCheckedChange = onNowPlayingToggle
                )
            }
        }
    }
}

@Composable
private fun JioSaavnCard(
    enabled: Boolean,
    quality: String,
    accentColor: Color,
    onEnableToggle: (Boolean) -> Unit,
    onQualityChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SettingsToggleItem(
                title = "Enable JioSaavn",
                subtitle = "Search and play music from JioSaavn",
                checked = enabled,
                accentColor = accentColor,
                onCheckedChange = onEnableToggle
            )

            if (enabled) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Audio Quality",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("96kbps", "160kbps", "320kbps").forEach { q ->
                        QualityChip(
                            quality = q,
                            selected = quality == q,
                            accentColor = accentColor,
                            onClick = { onQualityChange(q) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityChip(
    quality: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) accentColor else MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = quality,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
