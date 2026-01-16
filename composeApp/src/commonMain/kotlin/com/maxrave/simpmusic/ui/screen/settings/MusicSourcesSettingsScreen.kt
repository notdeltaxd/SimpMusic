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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.domain.manager.DataStoreManager.Values.TRUE
import com.maxrave.simpmusic.ui.navigation.destination.settings.DiscordLoginDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.SpotifyLoginDestination
import com.maxrave.simpmusic.utils.LastFmAuthHelper
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import io.github.aakira.napier.Napier as Logger
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler

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
    val viewModel: SettingsViewModel = koinViewModel()
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // Last.fm
    val lastFmUsername by dataStoreManager.lastFmUsername.collectAsState(initial = "")
    val lastFmSessionKey by dataStoreManager.lastFmSessionKey.collectAsState(initial = "")
    val lastFmScrobbleEnabled by dataStoreManager.lastFmScrobbleEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)
    val lastFmNowPlayingEnabled by dataStoreManager.lastFmNowPlayingEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)
    
    // Last.fm Auth State
    var lastFmApiKey by remember { mutableStateOf("") }
    var lastFmApiSecret by remember { mutableStateOf("") }
    var lastFmAuthStarted by remember { mutableStateOf(false) }
    var lastFmAuthToken by remember { mutableStateOf<String?>(null) }
    val uriHandler = LocalUriHandler.current

    // JioSaavn
    val jioSaavnEnabled by dataStoreManager.jioSaavnEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)
    val jioSaavnQuality by dataStoreManager.jioSaavnQuality.collectAsState(initial = "320kbps")

    // Spotify
    val spotifyLoggedIn by viewModel.spotifyLogIn.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val spotifyLyrics by viewModel.spotifyLyrics.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val spotifyCanvas by viewModel.spotifyCanvas.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)

    // Discord
    val discordLoggedIn by viewModel.discordLoggedIn.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val richPresenceEnabled by viewModel.discordRichPresence.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)

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
                    apiKey = lastFmApiKey,
                    apiSecret = lastFmApiSecret,
                    scrobbleEnabled = lastFmScrobbleEnabled == DataStoreManager.Values.TRUE,
                    nowPlayingEnabled = lastFmNowPlayingEnabled == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onApiKeyChange = { lastFmApiKey = it },
                    onApiSecretChange = { lastFmApiSecret = it },
                    onStartAuth = {
                        scope.launch {
                            // Save API key and secret
                            dataStoreManager.setLastFmApiKey(lastFmApiKey)
                            dataStoreManager.setLastFmApiSecret(lastFmApiSecret)
                            
                            // Step 1: Fetch request token
                            val tokenResult = LastFmAuthHelper.fetchRequestToken(lastFmApiKey, lastFmApiSecret)
                            tokenResult.onSuccess { token ->
                                lastFmAuthToken = token
                                // Step 2: Open auth URL in browser
                                val authUrl = LastFmAuthHelper.getAuthUrl(lastFmApiKey, token)
                                uriHandler.openUri(authUrl)
                                lastFmAuthStarted = true
                            }.onFailure { error ->
                                // Handle error - could show a toast/snackbar
                                Logger.e("Last.fm", "Failed to get token: ${error.message}")
                            }
                        }
                    },
                    onGetSession = {
                        scope.launch {
                            val token = lastFmAuthToken
                            if (token != null) {
                                // Step 3: Exchange token for session key
                                val sessionResult = LastFmAuthHelper.fetchSessionKey(
                                    lastFmApiKey,
                                    lastFmApiSecret,
                                    token
                                )
                                sessionResult.onSuccess { (username, sessionKey) ->
                                    // Save session to DataStore
                                    dataStoreManager.setLastFmSession(username, sessionKey)
                                    lastFmAuthStarted = false
                                    lastFmAuthToken = null
                                }.onFailure { error ->
                                    Logger.e("Last.fm", "Failed to get session: ${error.message}")
                                }
                            } else {
                                Logger.e("Last.fm", "No auth token - click Start Auth first")
                            }
                        }
                    },
                    onDisconnect = { scope.launch { dataStoreManager.clearLastFmSession() } },
                    onScrobbleToggle = { enabled -> scope.launch { dataStoreManager.setLastFmScrobbleEnabled(enabled) } },
                    onNowPlayingToggle = { enabled -> scope.launch { dataStoreManager.setLastFmNowPlayingEnabled(enabled) } },
                    authStarted = lastFmAuthStarted,
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
                    onEnableToggle = { enabled -> scope.launch { dataStoreManager.setJioSaavnEnabled(enabled) } },
                    onQualityChange = { quality -> scope.launch { dataStoreManager.setJioSaavnQuality(quality) } },
                )
            }

            // Spotify Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(title = "Spotify")
            }

            item {
                SpotifyCard(
                    isLoggedIn = spotifyLoggedIn,
                    lyricsEnabled = spotifyLyrics,
                    canvasEnabled = spotifyCanvas,
                    accentColor = accentColor,
                    onLogin = { navController.navigate(SpotifyLoginDestination) },
                    onLogout = { viewModel.setSpotifyLogIn(false) },
                    onLyricsToggle = { enabled -> viewModel.setSpotifyLyrics(enabled) },
                    onCanvasToggle = { enabled -> viewModel.setSpotifyCanvas(enabled) },
                )
            }

            // Discord Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(title = "Discord Integration")
            }

            item {
                DiscordCard(
                    isLoggedIn = discordLoggedIn,
                    richPresenceEnabled = richPresenceEnabled,
                    accentColor = accentColor,
                    onLogin = { navController.navigate(DiscordLoginDestination) },
                    onLogout = { viewModel.logOutDiscord() },
                    onRichPresenceToggle = { enabled -> viewModel.setDiscordRichPresenceEnabled(enabled) },
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
    apiKey: String,
    apiSecret: String,
    scrobbleEnabled: Boolean,
    nowPlayingEnabled: Boolean,
    accentColor: Color,
    onApiKeyChange: (String) -> Unit,
    onApiSecretChange: (String) -> Unit,
    onStartAuth: () -> Unit,
    onGetSession: () -> Unit,
    onDisconnect: () -> Unit,
    onScrobbleToggle: (Boolean) -> Unit,
    onNowPlayingToggle: (Boolean) -> Unit,
    authStarted: Boolean,
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
                    tint = if (isConnected) Color(0xFFD51007) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Connected as $username" else "Not connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!isConnected) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Instructions
                Text(
                    text = "To set up Last.fm:\n1. Create an account at last.fm\n2. Get API Key & Secret from last.fm/api/account/create\n3. Enter credentials below and click 'Start Auth'\n4. Authorize in browser, then click 'Get & Save Session'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // API Key Input
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onApiKeyChange,
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // API Secret Input
                OutlinedTextField(
                    value = apiSecret,
                    onValueChange = onApiSecretChange,
                    label = { Text("API Secret") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Auth Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStartAuth,
                        enabled = apiKey.isNotBlank() && apiSecret.isNotBlank() && !authStarted,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD51007)
                        )
                    ) {
                        Text("1. Start Auth", color = Color.White)
                    }
                    
                    Button(
                        onClick = onGetSession,
                        enabled = authStarted,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD51007)
                        )
                    ) {
                        Text("2. Get Session", color = Color.White)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Disconnect Button
                OutlinedButton(
                    onClick = onDisconnect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Disconnect")
                }
                
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

@Composable
private fun SpotifyCard(
    isLoggedIn: Boolean,
    lyricsEnabled: Boolean,
    canvasEnabled: Boolean,
    accentColor: Color,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onLyricsToggle: (Boolean) -> Unit,
    onCanvasToggle: (Boolean) -> Unit,
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
                    imageVector = if (isLoggedIn) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                    contentDescription = null,
                    tint = if (isLoggedIn) Color(0xFF1DB954) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLoggedIn) "Connected to Spotify" else "Not connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Connect/Disconnect Button
            if (isLoggedIn) {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Disconnect")
                }
            } else {
                Button(
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DB954)
                    )
                ) {
                    Text("Connect to Spotify", color = Color.White)
                }
            }

            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))

                SettingsToggleItem(
                    title = "Enable Spotify lyrics",
                    subtitle = "Fetch lyrics from Spotify",
                    checked = lyricsEnabled,
                    accentColor = accentColor,
                    onCheckedChange = onLyricsToggle
                )

                SettingsToggleItem(
                    title = "Enable Canvas",
                    subtitle = "Show animated video backgrounds",
                    checked = canvasEnabled,
                    accentColor = accentColor,
                    onCheckedChange = onCanvasToggle
                )
            }
        }
    }
}

@Composable
private fun DiscordCard(
    isLoggedIn: Boolean,
    richPresenceEnabled: Boolean,
    accentColor: Color,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onRichPresenceToggle: (Boolean) -> Unit,
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
                    imageVector = if (isLoggedIn) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                    contentDescription = null,
                    tint = if (isLoggedIn) Color(0xFF5865F2) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLoggedIn) "Connected to Discord" else "Not connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Connect/Disconnect Button
            if (isLoggedIn) {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Disconnect")
                }
            } else {
                Button(
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5865F2)
                    )
                ) {
                    Text("Connect to Discord", color = Color.White)
                }
            }

            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))

                SettingsToggleItem(
                    title = "Rich Presence",
                    subtitle = "Show what you're playing on Discord",
                    checked = richPresenceEnabled,
                    accentColor = accentColor,
                    onCheckedChange = onRichPresenceToggle
                )
            }
        }
    }
}
