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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maxrave.simpmusic.ui.navigation.destination.home.CreditDestination
import com.maxrave.simpmusic.utils.VersionManager
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import com.maxrave.simpmusic.viewModel.SharedViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.version
import simpmusic.composeapp.generated.resources.version_format
import simpmusic.composeapp.generated.resources.check_for_update
import simpmusic.composeapp.generated.resources.last_checked_at
import simpmusic.composeapp.generated.resources.checking
import simpmusic.composeapp.generated.resources.auto_check_for_update
import simpmusic.composeapp.generated.resources.auto_check_for_update_description
import simpmusic.composeapp.generated.resources.update_channel
import simpmusic.composeapp.generated.resources.about_us
import simpmusic.composeapp.generated.resources.author
import simpmusic.composeapp.generated.resources.maxrave_dev
import simpmusic.composeapp.generated.resources.third_party_libraries
import simpmusic.composeapp.generated.resources.description_and_licenses
import simpmusic.composeapp.generated.resources.buy_me_a_coffee
import simpmusic.composeapp.generated.resources.donation

/**
 * About settings screen with app info, update checking, and credits.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AboutSettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    // State from ViewModel
    val autoCheckUpdate by viewModel.autoCheckUpdate.collectAsStateWithLifecycle()
    val updateChannel by viewModel.updateChannel.collectAsStateWithLifecycle()
    val lastCheckUpdate by viewModel.lastCheckForUpdate.collectAsStateWithLifecycle()
    val isCheckingUpdate by sharedViewModel.isCheckingUpdate.collectAsStateWithLifecycle()

    // Dialog states
    var showUpdateChannelDialog by remember { mutableStateOf(false) }
    var showThirdPartyLibraries by remember { mutableStateOf(false) }

    val checkForUpdateSubtitle by remember {
        derivedStateOf {
            if (isCheckingUpdate) {
                runBlocking { getString(Res.string.checking) }
            } else {
                val lastCheckLong = lastCheckUpdate?.toLong() ?: 0L
                if (lastCheckLong > 0) {
                    try {
                        val instant = Instant.fromEpochMilliseconds(lastCheckLong)
                        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        val formatted = "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')} ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}:${localDateTime.second.toString().padStart(2, '0')}"
                        runBlocking { getString(Res.string.last_checked_at, formatted) }
                    } catch (e: Exception) {
                        "Never"
                    }
                } else {
                    "Never"
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.ABOUT)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.ABOUT.title,
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
            // Section: App Info
            item {
                SettingsSectionHeader(title = "App Info")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.version),
                    subtitle = VersionManager.getVersionName(),
                    accentColor = accentColor,
                    onClick = { }
                )
            }

            // Section: Updates
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Updates")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.check_for_update),
                    subtitle = checkForUpdateSubtitle,
                    accentColor = accentColor,
                    onClick = { sharedViewModel.checkForUpdate() }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.auto_check_for_update),
                    subtitle = stringResource(Res.string.auto_check_for_update_description),
                    checked = autoCheckUpdate,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setAutoCheckUpdate(enabled)
                    }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.update_channel),
                    subtitle = updateChannel ?: "GitHub",
                    accentColor = accentColor,
                    onClick = { showUpdateChannelDialog = true }
                )
            }

            // Section: Credits
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Credits")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.about_us),
                    subtitle = stringResource(Res.string.author) + ": " + stringResource(Res.string.maxrave_dev),
                    accentColor = accentColor,
                    onClick = { navController.navigate(CreditDestination) }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.third_party_libraries),
                    subtitle = stringResource(Res.string.description_and_licenses),
                    accentColor = accentColor,
                    onClick = { showThirdPartyLibraries = true }
                )
            }

            // Section: Support
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Support")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.buy_me_a_coffee),
                    subtitle = stringResource(Res.string.donation),
                    accentColor = accentColor,
                    onClick = {
                        uriHandler.openUri("https://www.buymeacoffee.com/maxrave")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Update Channel Selection Dialog
    if (showUpdateChannelDialog) {
        SelectionDialog(
            title = stringResource(Res.string.update_channel),
            options = listOf("GitHub", "F-Droid", "GitHub FOSS Nightly"),
            selectedOption = updateChannel ?: "GitHub",
            onOptionSelected = { selected ->
                viewModel.setUpdateChannel(selected)
            },
            onDismiss = { showUpdateChannelDialog = false }
        )
    }
}
