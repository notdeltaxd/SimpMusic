package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.LocalPlatformContext
import com.maxrave.common.LIMIT_CACHE_SIZE
import com.maxrave.simpmusic.extension.bytesToMB
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.player_cache
import simpmusic.composeapp.generated.resources.downloaded_cache
import simpmusic.composeapp.generated.resources.thumbnail_cache
import simpmusic.composeapp.generated.resources.spotify_canvas_cache
import simpmusic.composeapp.generated.resources.limit_player_cache
import simpmusic.composeapp.generated.resources.backup
import simpmusic.composeapp.generated.resources.restore_your_data
import simpmusic.composeapp.generated.resources.restore_your_saved_data
import simpmusic.composeapp.generated.resources.save_all_your_playlist_data
import simpmusic.composeapp.generated.resources.backup_downloaded
import simpmusic.composeapp.generated.resources.backup_downloaded_description
import simpmusic.composeapp.generated.resources.clear_player_cache
import simpmusic.composeapp.generated.resources.clear_downloaded_cache
import simpmusic.composeapp.generated.resources.clear_thumbnail_cache
import simpmusic.composeapp.generated.resources.clear_canvas_cache
import simpmusic.composeapp.generated.resources.clear

/**
 * Storage & Cache settings screen with cache management and backup options.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val platformContext = LocalPlatformContext.current

    // State from ViewModel
    val playerCache by viewModel.cacheSize.collectAsStateWithLifecycle()
    val downloadedCache by viewModel.downloadedCacheSize.collectAsStateWithLifecycle()
    val thumbnailCache by viewModel.thumbCacheSize.collectAsStateWithLifecycle()
    val canvasCache by viewModel.canvasCacheSize.collectAsStateWithLifecycle()
    val limitPlayerCache by viewModel.playerCacheLimit.collectAsStateWithLifecycle()
    val backupDownloaded by viewModel.backupDownloaded.collectAsStateWithLifecycle()

    // Dialog states
    var showClearPlayerCacheDialog by remember { mutableStateOf(false) }
    var showClearDownloadedCacheDialog by remember { mutableStateOf(false) }
    var showClearThumbnailCacheDialog by remember { mutableStateOf(false) }
    var showClearCanvasCacheDialog by remember { mutableStateOf(false) }
    var showCacheLimitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getData()
        viewModel.getThumbCacheSize(platformContext)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.STORAGE)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.STORAGE.title,
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
            // Section: Cache
            item {
                SettingsSectionHeader(title = "Cache")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.player_cache),
                    subtitle = "${playerCache.bytesToMB()} MB",
                    accentColor = accentColor,
                    onClick = { showClearPlayerCacheDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.downloaded_cache),
                    subtitle = "${downloadedCache.bytesToMB()} MB",
                    accentColor = accentColor,
                    onClick = { showClearDownloadedCacheDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.thumbnail_cache),
                    subtitle = "${thumbnailCache.bytesToMB()} MB",
                    accentColor = accentColor,
                    onClick = { showClearThumbnailCacheDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.spotify_canvas_cache),
                    subtitle = "${canvasCache.bytesToMB()} MB",
                    accentColor = accentColor,
                    onClick = { showClearCanvasCacheDialog = true }
                )
            }

            // Section: Cache Settings
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Cache Settings")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.limit_player_cache),
                    subtitle = LIMIT_CACHE_SIZE.getItemFromData(limitPlayerCache).toString(),
                    accentColor = accentColor,
                    onClick = { showCacheLimitDialog = true }
                )
            }

            // Section: Backup & Restore
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Backup & Restore")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.backup_downloaded),
                    subtitle = stringResource(Res.string.backup_downloaded_description),
                    checked = backupDownloaded,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setBackupDownloaded(enabled)
                    }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.backup),
                    subtitle = stringResource(Res.string.save_all_your_playlist_data),
                    accentColor = accentColor,
                    onClick = {
                        // Backup is handled via file picker launcher from parent
                        // This will be connected when integrated with the backup launcher
                    }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.restore_your_data),
                    subtitle = stringResource(Res.string.restore_your_saved_data),
                    accentColor = accentColor,
                    onClick = {
                        // Restore is handled via file picker launcher from parent
                        // This will be connected when integrated with the restore launcher
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Clear Player Cache Dialog
    if (showClearPlayerCacheDialog) {
        ClearCacheDialog(
            title = stringResource(Res.string.clear_player_cache),
            onConfirm = {
                viewModel.clearPlayerCache()
                showClearPlayerCacheDialog = false
            },
            onDismiss = { showClearPlayerCacheDialog = false }
        )
    }

    // Clear Downloaded Cache Dialog
    if (showClearDownloadedCacheDialog) {
        ClearCacheDialog(
            title = stringResource(Res.string.clear_downloaded_cache),
            onConfirm = {
                viewModel.clearDownloadedCache()
                showClearDownloadedCacheDialog = false
            },
            onDismiss = { showClearDownloadedCacheDialog = false }
        )
    }

    // Clear Thumbnail Cache Dialog
    if (showClearThumbnailCacheDialog) {
        ClearCacheDialog(
            title = stringResource(Res.string.clear_thumbnail_cache),
            onConfirm = {
                viewModel.clearThumbnailCache(platformContext)
                showClearThumbnailCacheDialog = false
            },
            onDismiss = { showClearThumbnailCacheDialog = false }
        )
    }

    // Clear Canvas Cache Dialog
    if (showClearCanvasCacheDialog) {
        ClearCacheDialog(
            title = stringResource(Res.string.clear_canvas_cache),
            onConfirm = {
                viewModel.clearCanvasCache()
                showClearCanvasCacheDialog = false
            },
            onDismiss = { showClearCanvasCacheDialog = false }
        )
    }

    // Cache Limit Selection Dialog
    if (showCacheLimitDialog) {
        SelectionDialog(
            title = stringResource(Res.string.limit_player_cache),
            options = LIMIT_CACHE_SIZE.items.map { it.toString() },
            selectedOption = LIMIT_CACHE_SIZE.getItemFromData(limitPlayerCache).toString(),
            onOptionSelected = { selected ->
                viewModel.setPlayerCacheLimit(LIMIT_CACHE_SIZE.getDataFromItem(selected))
            },
            onDismiss = { showCacheLimitDialog = false }
        )
    }
}

@Composable
private fun ClearCacheDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Text("This will permanently delete the cached data. Are you sure?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.clear))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
