package com.maxrave.simpmusic.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.maxrave.simpmusic.ui.navigation.destination.settings.PlaybackSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.MusicSourcesSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AppearanceSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AccountSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AISettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.StorageSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AboutSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.EqualizerSettingsDestination
import com.maxrave.simpmusic.ui.screen.settings.PlaybackSettingsScreen
import com.maxrave.simpmusic.ui.screen.settings.MusicSourcesSettingsScreen

/**
 * Navigation graph for settings category screens.
 */
fun NavGraphBuilder.settingsScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
) {
    composable<PlaybackSettingsDestination> {
        PlaybackSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    composable<MusicSourcesSettingsDestination> {
        MusicSourcesSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    // TODO: Implement remaining category screens
    composable<AppearanceSettingsDestination> {
        // AppearanceSettingsScreen(innerPadding, navController)
    }
    
    composable<AccountSettingsDestination> {
        // AccountSettingsScreen(innerPadding, navController)
    }
    
    composable<AISettingsDestination> {
        // AISettingsScreen(innerPadding, navController)
    }
    
    composable<StorageSettingsDestination> {
        // StorageSettingsScreen(innerPadding, navController)
    }
    
    composable<AboutSettingsDestination> {
        // AboutSettingsScreen(innerPadding, navController)
    }
    
    composable<EqualizerSettingsDestination> {
        // EqualizerSettingsScreen(innerPadding, navController)
    }
}
