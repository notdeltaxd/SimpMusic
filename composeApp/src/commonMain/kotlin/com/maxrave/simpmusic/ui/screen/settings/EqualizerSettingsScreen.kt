package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maxrave.simpmusic.expect.ui.openEqResult
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.open_system_equalizer
import simpmusic.composeapp.generated.resources.use_your_system_equalizer

/**
 * Equalizer settings screen with system equalizer access.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    // Get the system equalizer launcher
    val resultLauncher = openEqResult(viewModel.getAudioSessionId())

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.EQUALIZER)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.EQUALIZER.title,
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
            // Section: System Equalizer
            item {
                SettingsSectionHeader(title = "Audio Enhancement")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.open_system_equalizer),
                    subtitle = stringResource(Res.string.use_your_system_equalizer),
                    accentColor = accentColor,
                    onClick = { resultLauncher.launch() }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Use your device's built-in equalizer to customize audio playback. The equalizer provides control over frequency bands to enhance your listening experience.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
