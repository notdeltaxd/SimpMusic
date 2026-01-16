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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.maxrave.simpmusic.viewModel.SettingsViewModel
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
    val uriHandler = LocalUriHandler.current

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
                    apiKey = lastFmApiKey,
                    apiSecret = lastFmApiSecret,
                    scrobbleEnabled = lastFmScrobbleEnabled == DataStoreManager.Values.TRUE,
                    nowPlayingEnabled = lastFmNowPlayingEnabled == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onApiKeyChange = { lastFmApiKey = it },
                    onApiSecretChange = { lastFmApiSecret = it },
                    onStartAuth = {
                        viewModel.startLastFmAuth(
                            apiKey = lastFmApiKey,
                            apiSecret = lastFmApiSecret,
                            onAuthUrl = { authUrl ->
                                uriHandler.openUri(authUrl)
                            }
                        )
                    },
                    onGetSession = {
                        viewModel.getLastFmSession(
                            apiKey = lastFmApiKey,
                            apiSecret = lastFmApiSecret
                        )
                    },
                    onDisconnect = { 
                        scope.launch { 
                            dataStoreManager.clearLastFmSession()
                            viewModel.resetLastFmAuthState()
                        } 
                    },
                    onScrobbleToggle = { enabled -> scope.launch { dataStoreManager.setLastFmScrobbleEnabled(enabled) } },
                    onNowPlayingToggle = { enabled -> scope.launch { dataStoreManager.setLastFmNowPlayingEnabled(enabled) } },
                    authStarted = viewModel.lastFmAuthStarted.collectAsStateWithLifecycle().value,
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
    val jioSaavnGreen = Color(0xFF2BC5B4)  // JioSaavn brand color
    
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
            // Header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable JioSaavn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Search and play music from JioSaavn",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnableToggle,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = jioSaavnGreen,
                        checkedThumbColor = Color.White
                    )
                )
            }

            // Quality section - only shown when enabled
            androidx.compose.animation.AnimatedVisibility(visible = enabled) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Audio Quality",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "Higher quality uses more data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "96kbps" to "Low",
                            "160kbps" to "Medium",
                            "320kbps" to "High"
                        ).forEach { (q, label) ->
                            QualityChip(
                                quality = q,
                                label = label,
                                selected = quality == q,
                                accentColor = jioSaavnGreen,
                                onClick = { onQualityChange(q) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityChip(
    quality: String,
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) accentColor else MaterialTheme.colorScheme.surfaceContainer,
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = quality,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
