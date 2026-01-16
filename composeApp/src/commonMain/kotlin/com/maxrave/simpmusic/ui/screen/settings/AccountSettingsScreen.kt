package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maxrave.common.QUALITY
import com.maxrave.common.SUPPORTED_LANGUAGE
import com.maxrave.common.SUPPORTED_LOCATION
import com.maxrave.common.VIDEO_QUALITY
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.domain.manager.DataStoreManager.Values.TRUE
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.ui.navigation.destination.login.LoginDestination
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.youtube_account
import simpmusic.composeapp.generated.resources.manage_your_youtube_accounts
import simpmusic.composeapp.generated.resources.language
import simpmusic.composeapp.generated.resources.content_country
import simpmusic.composeapp.generated.resources.quality
import simpmusic.composeapp.generated.resources.download_quality
import simpmusic.composeapp.generated.resources.play_video_for_video_track_instead_of_audio_only
import simpmusic.composeapp.generated.resources.such_as_music_video_lyrics_video_podcasts_and_more
import simpmusic.composeapp.generated.resources.video_quality
import simpmusic.composeapp.generated.resources.video_download_quality
import simpmusic.composeapp.generated.resources.send_back_listening_data_to_google
import simpmusic.composeapp.generated.resources.upload_your_listening_history_to_youtube_music_server_it_will_make_yt_music_recommendation_system_better_working_only_if_logged_in
import simpmusic.composeapp.generated.resources.play_explicit_content
import simpmusic.composeapp.generated.resources.play_explicit_content_description
import simpmusic.composeapp.generated.resources.keep_your_youtube_playlist_offline
import simpmusic.composeapp.generated.resources.keep_your_youtube_playlist_offline_description
import simpmusic.composeapp.generated.resources.proxy
import simpmusic.composeapp.generated.resources.proxy_description
import simpmusic.composeapp.generated.resources.proxy_type
import simpmusic.composeapp.generated.resources.proxy_host
import simpmusic.composeapp.generated.resources.proxy_port
import simpmusic.composeapp.generated.resources.http
import simpmusic.composeapp.generated.resources.socks

/**
 * Account & Content settings screen with all content-related settings.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    // State from ViewModel - using same pattern as old SettingScreen
    val language by viewModel.language.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()
    val quality by viewModel.quality.collectAsStateWithLifecycle()
    val downloadQuality by viewModel.downloadQuality.collectAsStateWithLifecycle()
    val videoQuality by viewModel.videoQuality.collectAsStateWithLifecycle()
    val videoDownloadQuality by viewModel.videoDownloadQuality.collectAsStateWithLifecycle()
    val playVideo by viewModel.playVideoInsteadOfAudio.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val sendData by viewModel.sendBackToGoogle.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val explicitContentEnabled by viewModel.explicitContentEnabled.collectAsStateWithLifecycle()
    val keepYoutubePlaylistOffline by viewModel.keepYouTubePlaylistOffline.collectAsStateWithLifecycle()
    val usingProxy by viewModel.usingProxy.collectAsStateWithLifecycle()
    val proxyType by viewModel.proxyType.collectAsStateWithLifecycle()
    val proxyHost by viewModel.proxyHost.collectAsStateWithLifecycle()
    val proxyPort by viewModel.proxyPort.collectAsStateWithLifecycle()

    // Dialog states
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showDownloadQualityDialog by remember { mutableStateOf(false) }
    var showVideoQualityDialog by remember { mutableStateOf(false) }
    var showVideoDownloadQualityDialog by remember { mutableStateOf(false) }
    var showProxyTypeDialog by remember { mutableStateOf(false) }
    var showProxyHostDialog by remember { mutableStateOf(false) }
    var showProxyPortDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAllGoogleAccount()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.ACCOUNT)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.ACCOUNT.title,
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            )
        ) {
            // Section: YouTube Account
            item {
                SettingsSectionHeader(title = "Account")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.youtube_account),
                    subtitle = stringResource(Res.string.manage_your_youtube_accounts),
                    accentColor = accentColor,
                    onClick = { navController.navigate(LoginDestination) }
                )
            }

            // Section: Language & Region
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Language & Region")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.language),
                    subtitle = SUPPORTED_LANGUAGE.getLanguageFromCode(language ?: "en-US"),
                    accentColor = accentColor,
                    onClick = { showLanguageDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.content_country),
                    subtitle = location ?: "US",
                    accentColor = accentColor,
                    onClick = { showLocationDialog = true }
                )
            }

            // Section: Quality
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Quality")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.quality),
                    subtitle = quality ?: "Auto",
                    accentColor = accentColor,
                    onClick = { showQualityDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.download_quality),
                    subtitle = downloadQuality ?: "Auto",
                    accentColor = accentColor,
                    onClick = { showDownloadQualityDialog = true }
                )
            }

            // Video settings (not on Desktop)
            if (getPlatform() != Platform.Desktop) {
                item {
                    SettingsToggleItem(
                        title = stringResource(Res.string.play_video_for_video_track_instead_of_audio_only),
                        subtitle = stringResource(Res.string.such_as_music_video_lyrics_video_podcasts_and_more),
                        checked = playVideo,
                        accentColor = accentColor,
                        onCheckedChange = { enabled ->
                            viewModel.setPlayVideoInsteadOfAudio(enabled)
                        }
                    )
                }

                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.video_quality),
                        subtitle = videoQuality ?: "Auto",
                        accentColor = accentColor,
                        onClick = { showVideoQualityDialog = true }
                    )
                }

                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.video_download_quality),
                        subtitle = videoDownloadQuality ?: "Auto",
                        accentColor = accentColor,
                        onClick = { showVideoDownloadQualityDialog = true }
                    )
                }
            }

            // Section: Privacy & Data
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Privacy & Data")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.send_back_listening_data_to_google),
                    subtitle = stringResource(Res.string.upload_your_listening_history_to_youtube_music_server_it_will_make_yt_music_recommendation_system_better_working_only_if_logged_in),
                    checked = sendData,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setSendBackToGoogle(enabled)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.play_explicit_content),
                    subtitle = stringResource(Res.string.play_explicit_content_description),
                    checked = explicitContentEnabled,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setExplicitContentEnabled(enabled)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.keep_your_youtube_playlist_offline),
                    subtitle = stringResource(Res.string.keep_your_youtube_playlist_offline_description),
                    checked = keepYoutubePlaylistOffline,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setKeepYouTubePlaylistOffline(enabled)
                    }
                )
            }

            // Section: Network / Proxy
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Network")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.proxy),
                    subtitle = stringResource(Res.string.proxy_description),
                    checked = usingProxy,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setUsingProxy(enabled)
                    }
                )
            }

            // Proxy settings only visible when proxy is enabled
            if (usingProxy) {
                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.proxy_type),
                        subtitle = when (proxyType) {
                            DataStoreManager.ProxyType.PROXY_TYPE_HTTP -> stringResource(Res.string.http)
                            DataStoreManager.ProxyType.PROXY_TYPE_SOCKS -> stringResource(Res.string.socks)
                        },
                        accentColor = accentColor,
                        onClick = { showProxyTypeDialog = true }
                    )
                }

                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.proxy_host),
                        subtitle = proxyHost.ifEmpty { "Not set" },
                        accentColor = accentColor,
                        onClick = { showProxyHostDialog = true }
                    )
                }

                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.proxy_port),
                        subtitle = proxyPort.toString(),
                        accentColor = accentColor,
                        onClick = { showProxyPortDialog = true }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        SelectionDialog(
            title = stringResource(Res.string.language),
            options = SUPPORTED_LANGUAGE.items.map { it.toString() },
            selectedOption = SUPPORTED_LANGUAGE.getLanguageFromCode(language ?: "en-US"),
            onOptionSelected = { selected ->
                val code = SUPPORTED_LANGUAGE.getCodeFromLanguage(selected)
                viewModel.changeLanguage(code)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Location Selection Dialog
    if (showLocationDialog) {
        SelectionDialog(
            title = stringResource(Res.string.content_country),
            options = SUPPORTED_LOCATION.items.map { it.toString() },
            selectedOption = location ?: "US",
            onOptionSelected = { selected ->
                viewModel.changeLocation(selected)
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    // Quality Selection Dialog
    if (showQualityDialog) {
        SelectionDialog(
            title = stringResource(Res.string.quality),
            options = QUALITY.items.map { it.toString() },
            selectedOption = quality ?: "Auto",
            onOptionSelected = { selected ->
                viewModel.changeQuality(selected)
            },
            onDismiss = { showQualityDialog = false }
        )
    }

    // Download Quality Selection Dialog
    if (showDownloadQualityDialog) {
        SelectionDialog(
            title = stringResource(Res.string.download_quality),
            options = QUALITY.items.map { it.toString() },
            selectedOption = downloadQuality ?: "Auto",
            onOptionSelected = { selected ->
                viewModel.setDownloadQuality(selected)
            },
            onDismiss = { showDownloadQualityDialog = false }
        )
    }

    // Video Quality Selection Dialog
    if (showVideoQualityDialog) {
        SelectionDialog(
            title = stringResource(Res.string.video_quality),
            options = VIDEO_QUALITY.items.map { it.toString() },
            selectedOption = videoQuality ?: "Auto",
            onOptionSelected = { selected ->
                viewModel.changeVideoQuality(selected)
            },
            onDismiss = { showVideoQualityDialog = false }
        )
    }

    // Video Download Quality Selection Dialog
    if (showVideoDownloadQualityDialog) {
        SelectionDialog(
            title = stringResource(Res.string.video_download_quality),
            options = VIDEO_QUALITY.items.map { it.toString() },
            selectedOption = videoDownloadQuality ?: "Auto",
            onOptionSelected = { selected ->
                viewModel.setVideoDownloadQuality(selected)
            },
            onDismiss = { showVideoDownloadQualityDialog = false }
        )
    }

    // Proxy Type Selection Dialog
    if (showProxyTypeDialog) {
        val httpLabel = stringResource(Res.string.http)
        val socksLabel = stringResource(Res.string.socks)
        SelectionDialog(
            title = stringResource(Res.string.proxy_type),
            options = listOf(httpLabel, socksLabel),
            selectedOption = when (proxyType) {
                DataStoreManager.ProxyType.PROXY_TYPE_HTTP -> httpLabel
                DataStoreManager.ProxyType.PROXY_TYPE_SOCKS -> socksLabel
            },
            onOptionSelected = { selected ->
                val newType = if (selected == socksLabel) {
                    DataStoreManager.ProxyType.PROXY_TYPE_SOCKS
                } else {
                    DataStoreManager.ProxyType.PROXY_TYPE_HTTP
                }
                viewModel.setProxy(newType, proxyHost, proxyPort)
            },
            onDismiss = { showProxyTypeDialog = false }
        )
    }

    // Proxy Host Input Dialog
    if (showProxyHostDialog) {
        var hostInput by remember { mutableStateOf(proxyHost) }
        InputDialog(
            title = stringResource(Res.string.proxy_host),
            value = hostInput,
            onValueChange = { hostInput = it },
            label = stringResource(Res.string.proxy_host),
            onConfirm = {
                viewModel.setProxy(proxyType, hostInput, proxyPort)
                showProxyHostDialog = false
            },
            onDismiss = { showProxyHostDialog = false }
        )
    }

    // Proxy Port Input Dialog
    if (showProxyPortDialog) {
        var portInput by remember { mutableStateOf(proxyPort.toString()) }
        InputDialog(
            title = stringResource(Res.string.proxy_port),
            value = portInput,
            onValueChange = { portInput = it },
            label = stringResource(Res.string.proxy_port),
            onConfirm = {
                portInput.toIntOrNull()?.let { port ->
                    viewModel.setProxy(proxyType, proxyHost, port)
                }
                showProxyPortDialog = false
            },
            onDismiss = { showProxyPortDialog = false }
        )
    }
}

@Composable
fun SettingsClickItem(
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = accentColor
        )
    }
}

@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(options) { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = {
                                onOptionSelected(option)
                                onDismiss()
                            }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
