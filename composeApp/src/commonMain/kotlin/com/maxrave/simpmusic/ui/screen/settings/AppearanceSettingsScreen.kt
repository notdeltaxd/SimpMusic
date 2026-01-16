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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maxrave.domain.manager.DataStoreManager.Values.TRUE
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.translucent_bottom_navigation_bar
import simpmusic.composeapp.generated.resources.you_can_see_the_content_below_the_bottom_bar
import simpmusic.composeapp.generated.resources.blur_fullscreen_lyrics
import simpmusic.composeapp.generated.resources.blur_fullscreen_lyrics_description
import simpmusic.composeapp.generated.resources.blur_player_background
import simpmusic.composeapp.generated.resources.blur_player_background_description
import simpmusic.composeapp.generated.resources.enable_liquid_glass_effect
import simpmusic.composeapp.generated.resources.enable_liquid_glass_effect_description

/**
 * Appearance settings screen with UI customization options.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    // State from ViewModel - using same pattern as old SettingScreen
    val enableTranslucentNavBar by viewModel.translucentBottomBar.map { it == TRUE }.collectAsStateWithLifecycle(initialValue = false)
    val blurFullscreenLyrics by viewModel.blurFullscreenLyrics.collectAsStateWithLifecycle()
    val blurPlayerBackground by viewModel.blurPlayerBackground.collectAsStateWithLifecycle()
    val enableLiquidGlass by viewModel.enableLiquidGlass.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.APPEARANCE)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.APPEARANCE.title,
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
            // Section: Navigation
            item {
                SettingsSectionHeader(title = "Navigation")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.translucent_bottom_navigation_bar),
                    subtitle = stringResource(Res.string.you_can_see_the_content_below_the_bottom_bar),
                    checked = enableTranslucentNavBar,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setTranslucentBottomBar(enabled)
                    }
                )
            }

            // Section: Blur Effects
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Blur Effects")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.blur_fullscreen_lyrics),
                    subtitle = stringResource(Res.string.blur_fullscreen_lyrics_description),
                    checked = blurFullscreenLyrics,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setBlurFullscreenLyrics(enabled)
                    }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.blur_player_background),
                    subtitle = stringResource(Res.string.blur_player_background_description),
                    checked = blurPlayerBackground,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setBlurPlayerBackground(enabled)
                    }
                )
            }

            // Section: Visual Effects (Android only)
            if (getPlatform() == Platform.Android) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionHeader(title = "Visual Effects")
                }

                item {
                    SettingsToggleItem(
                        title = stringResource(Res.string.enable_liquid_glass_effect),
                        subtitle = stringResource(Res.string.enable_liquid_glass_effect_description),
                        checked = enableLiquidGlass,
                        accentColor = accentColor,
                        onCheckedChange = { enabled ->
                            viewModel.setEnableLiquidGlass(enabled)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
