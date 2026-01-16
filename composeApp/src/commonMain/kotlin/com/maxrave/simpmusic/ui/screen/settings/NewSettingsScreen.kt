package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.maxrave.simpmusic.ui.navigation.destination.settings.PlaybackSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.MusicSourcesSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AppearanceSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AccountSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AISettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.StorageSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.AboutSettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.settings.EqualizerSettingsDestination
import org.jetbrains.compose.resources.stringResource
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.settings

/**
 * NewSettingsScreen - The redesigned settings screen with categorized sections.
 * Displays a list of ExpressiveCategoryItems that navigate to detail screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    fun navigateToCategory(category: SettingsCategory) {
        when (category) {
            SettingsCategory.ACCOUNT -> navController.navigate(AccountSettingsDestination)
            SettingsCategory.PLAYBACK -> navController.navigate(PlaybackSettingsDestination)
            SettingsCategory.MUSIC_SOURCES -> navController.navigate(MusicSourcesSettingsDestination)
            SettingsCategory.APPEARANCE -> navController.navigate(AppearanceSettingsDestination)
            SettingsCategory.AI_INTEGRATION -> navController.navigate(AISettingsDestination)
            SettingsCategory.STORAGE -> navController.navigate(StorageSettingsDestination)
            SettingsCategory.EQUALIZER -> navController.navigate(EqualizerSettingsDestination)
            SettingsCategory.ABOUT -> navController.navigate(AboutSettingsDestination)
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.settings),
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
            items(
                items = SettingsCategory.entries,
                key = { it.id }
            ) { category ->
                ExpressiveCategoryItem(
                    category = category,
                    accentColor = getCategoryAccentColor(category),
                    onClick = { navigateToCategory(category) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
