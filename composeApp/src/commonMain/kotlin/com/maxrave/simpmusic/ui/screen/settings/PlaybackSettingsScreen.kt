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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import com.maxrave.domain.manager.DataStoreManager
import org.koin.compose.koinInject

/**
 * Playback settings category screen with crossfade and other playback options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val dataStoreManager: DataStoreManager = koinInject()

    val crossfadeEnabled by dataStoreManager.crossfadeEnabled.collectAsState(initial = DataStoreManager.Values.FALSE)
    val crossfadeDuration by dataStoreManager.crossfadeDuration.collectAsState(initial = 3)
    val normalizeVolume by dataStoreManager.normalizeVolume.collectAsState(initial = DataStoreManager.Values.FALSE)
    val skipSilent by dataStoreManager.skipSilent.collectAsState(initial = DataStoreManager.Values.FALSE)
    val saveStateOfPlayback by dataStoreManager.saveStateOfPlayback.collectAsState(initial = DataStoreManager.Values.TRUE)

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
                    checked = crossfadeEnabled == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        // TODO: Call viewModel or coroutine scope to update
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
                    enabled = crossfadeEnabled == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onValueChange = { newValue ->
                        // TODO: Call viewModel or coroutine scope to update
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
                    title = "Normalize volume",
                    subtitle = "Balance audio levels across tracks",
                    checked = normalizeVolume == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        // TODO: Call viewModel or coroutine scope to update
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = "Skip silent sections",
                    subtitle = "Automatically skip silent parts of audio",
                    checked = skipSilent == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        // TODO: Call viewModel or coroutine scope to update
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
                    title = "Save playback state",
                    subtitle = "Remember position when closing app",
                    checked = saveStateOfPlayback == DataStoreManager.Values.TRUE,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        // TODO: Call viewModel or coroutine scope to update
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
