package com.maxrave.simpmusic.ui.navigation.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Construction
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.maxrave.simpmusic.ui.screen.settings.AppearanceSettingsScreen
import com.maxrave.simpmusic.ui.screen.settings.AccountSettingsScreen
import com.maxrave.simpmusic.ui.screen.settings.AISettingsScreen
import com.maxrave.simpmusic.ui.screen.settings.StorageSettingsScreen
import com.maxrave.simpmusic.ui.screen.settings.AboutSettingsScreen
import com.maxrave.simpmusic.ui.screen.settings.EqualizerSettingsScreen

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
    
    composable<AppearanceSettingsDestination> {
        AppearanceSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    composable<AccountSettingsDestination> {
        AccountSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    composable<AISettingsDestination> {
        AISettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    composable<StorageSettingsDestination> {
        StorageSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    composable<AboutSettingsDestination> {
        AboutSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
    
    composable<EqualizerSettingsDestination> {
        EqualizerSettingsScreen(
            innerPadding = innerPadding,
            navController = navController,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonSettingsScreen(
    title: String,
    innerPadding: PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = title,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Construction,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "This section is under development",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

