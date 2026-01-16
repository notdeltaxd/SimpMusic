package com.maxrave.simpmusic.ui.screen.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.maxrave.domain.manager.DataStoreManager.Values.TRUE
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.balance_media_loudness
import simpmusic.composeapp.generated.resources.normalize_volume
import simpmusic.composeapp.generated.resources.save_last_played
import simpmusic.composeapp.generated.resources.save_last_played_track_and_queue
import simpmusic.composeapp.generated.resources.save_playback_state
import simpmusic.composeapp.generated.resources.save_shuffle_and_repeat_mode
import simpmusic.composeapp.generated.resources.skip_no_music_part
import simpmusic.composeapp.generated.resources.skip_silent
import simpmusic.composeapp.generated.resources.kill_service_on_exit
import simpmusic.composeapp.generated.resources.kill_service_on_exit_description
import simpmusic.composeapp.generated.resources.keep_service_alive
import simpmusic.composeapp.generated.resources.keep_service_alive_description
import simpmusic.composeapp.generated.resources.sponsorBlock
import simpmusic.composeapp.generated.resources.enable_sponsor_block
import simpmusic.composeapp.generated.resources.skip_sponsor_part_of_video

/**
 * Playback settings category screen with crossfade and other playback options.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    // State from ViewModel - using same pattern as old SettingScreen
    val crossfadeEnabled by viewModel.crossfadeEnabled.collectAsStateWithLifecycle()
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsStateWithLifecycle()
    val normalizeVolume by viewModel.normalizeVolume.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val skipSilent by viewModel.skipSilent.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val savePlaybackState by viewModel.savedPlaybackState.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val saveLastPlayed by viewModel.saveRecentSongAndQueue.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val killServiceOnExit by viewModel.killServiceOnExit.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = true)
    val keepServiceAlive by viewModel.keepServiceAlive.collectAsStateWithLifecycle()
    val sponsorBlockEnabled by viewModel.sponsorBlockEnabled.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.PLAYBACK)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.PLAYBACK.title,
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
            // Section: Crossfade
            item {
                SettingsSectionHeader(title = "Crossfade")
            }

            item {
                SettingsToggleItem(
                    title = "Enable crossfade",
                    subtitle = "Smoothly transition between songs",
                    checked = crossfadeEnabled,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setCrossfadeEnabled(enabled)
                    }
                )
            }

            item {
                SettingsSliderItem(
                    title = "Crossfade duration",
                    subtitle = "${crossfadeDuration} seconds",
                    value = crossfadeDuration.toFloat(),
                    valueRange = 1f..10f,
                    steps = 8,
                    enabled = crossfadeEnabled,
                    accentColor = accentColor,
                    onValueChange = { newValue ->
                        viewModel.setCrossfadeDuration(newValue.toInt())
                    }
                )
            }

            // Section: Audio Processing
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Audio Processing")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.normalize_volume),
                    subtitle = stringResource(Res.string.balance_media_loudness),
                    checked = normalizeVolume,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setNormalizeVolume(enabled)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.skip_silent),
                    subtitle = stringResource(Res.string.skip_no_music_part),
                    checked = skipSilent,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setSkipSilent(enabled)
                    }
                )
            }

            // Section: Playback State
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Playback State")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.save_playback_state),
                    subtitle = stringResource(Res.string.save_shuffle_and_repeat_mode),
                    checked = savePlaybackState,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setSavedPlaybackState(enabled)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.save_last_played),
                    subtitle = stringResource(Res.string.save_last_played_track_and_queue),
                    checked = saveLastPlayed,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setSaveLastPlayed(enabled)
                    }
                )
            }

            // Section: Background Service
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Background Service")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.kill_service_on_exit),
                    subtitle = stringResource(Res.string.kill_service_on_exit_description),
                    checked = killServiceOnExit,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setKillServiceOnExit(enabled)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.keep_service_alive),
                    subtitle = stringResource(Res.string.keep_service_alive_description),
                    checked = keepServiceAlive,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setKeepServiceAlive(enabled)
                    }
                )
            }

            // Section: SponsorBlock
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = stringResource(Res.string.sponsorBlock))
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.enable_sponsor_block),
                    subtitle = stringResource(Res.string.skip_sponsor_part_of_video),
                    checked = sponsorBlockEnabled,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setSponsorBlockEnabled(enabled)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
            )
        )
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    enabled: Boolean,
    accentColor: Color,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChange(sliderValue) },
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
