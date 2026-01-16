package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.ai_provider
import simpmusic.composeapp.generated.resources.ai_api_key
import simpmusic.composeapp.generated.resources.custom_ai_model_id
import simpmusic.composeapp.generated.resources.default_models
import simpmusic.composeapp.generated.resources.use_ai_translation
import simpmusic.composeapp.generated.resources.use_ai_translation_description
import simpmusic.composeapp.generated.resources.translation_language
import simpmusic.composeapp.generated.resources.translation_language_message
import simpmusic.composeapp.generated.resources.main_lyrics_provider
import simpmusic.composeapp.generated.resources.simpmusic_lyrics
import simpmusic.composeapp.generated.resources.youtube_transcript
import simpmusic.composeapp.generated.resources.lrclib
import simpmusic.composeapp.generated.resources.youtube_subtitle_language
import simpmusic.composeapp.generated.resources.youtube_subtitle_language_message
import simpmusic.composeapp.generated.resources.help_build_lyrics_database
import simpmusic.composeapp.generated.resources.help_build_lyrics_database_description
import simpmusic.composeapp.generated.resources.openai
import simpmusic.composeapp.generated.resources.gemini
import simpmusic.composeapp.generated.resources.unknown
import simpmusic.composeapp.generated.resources.contributor_name
import simpmusic.composeapp.generated.resources.contributor_email

/**
 * AI Integration settings screen with AI provider, lyrics, and translation settings.
 * Uses SettingsViewModel like the old SettingScreen for proper state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISettingsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    // State from ViewModel - using same pattern as old SettingScreen
    val aiProvider by viewModel.aiProvider.collectAsStateWithLifecycle()
    val isHasApiKey by viewModel.isHasApiKey.collectAsStateWithLifecycle()
    val useAITranslation by viewModel.useAITranslation.collectAsStateWithLifecycle()
    val translationLanguage by viewModel.translationLanguage.collectAsStateWithLifecycle()
    val customModelId by viewModel.customModelId.collectAsStateWithLifecycle()
    val mainLyricsProvider by viewModel.mainLyricsProvider.collectAsStateWithLifecycle()
    val youtubeSubtitleLanguage by viewModel.youtubeSubtitleLanguage.collectAsStateWithLifecycle()
    val helpBuildLyricsDatabase by viewModel.helpBuildLyricsDatabase.collectAsStateWithLifecycle()
    val contributor by viewModel.contributor.collectAsStateWithLifecycle()

    // Dialog states
    var showProviderDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showCustomModelDialog by remember { mutableStateOf(false) }
    var showTranslationLanguageDialog by remember { mutableStateOf(false) }
    var showLyricsProviderDialog by remember { mutableStateOf(false) }
    var showYoutubeSubtitleDialog by remember { mutableStateOf(false) }
    var showContributorNameDialog by remember { mutableStateOf(false) }
    var showContributorEmailDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val accentColor = getCategoryAccentColor(SettingsCategory.AI_INTEGRATION)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = SettingsCategory.AI_INTEGRATION.title,
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
            // Section: AI Provider
            item {
                SettingsSectionHeader(title = "AI Provider")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.ai_provider),
                    subtitle = when (aiProvider) {
                        DataStoreManager.AI_PROVIDER_OPENAI -> stringResource(Res.string.openai)
                        DataStoreManager.AI_PROVIDER_GEMINI -> stringResource(Res.string.gemini)
                        else -> stringResource(Res.string.unknown)
                    },
                    accentColor = accentColor,
                    onClick = { showProviderDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.ai_api_key),
                    subtitle = if (isHasApiKey) "••••••••••" else "Not configured",
                    accentColor = accentColor,
                    onClick = { showApiKeyDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.custom_ai_model_id),
                    subtitle = customModelId.ifEmpty { stringResource(Res.string.default_models) },
                    accentColor = accentColor,
                    onClick = { showCustomModelDialog = true }
                )
            }

            // Section: Translation
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Translation")
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.use_ai_translation),
                    subtitle = stringResource(Res.string.use_ai_translation_description),
                    checked = useAITranslation,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setAITranslation(enabled)
                    }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.translation_language),
                    subtitle = translationLanguage ?: "en",
                    accentColor = accentColor,
                    onClick = { showTranslationLanguageDialog = true }
                )
            }

            // Section: Lyrics
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Lyrics")
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.main_lyrics_provider),
                    subtitle = when (mainLyricsProvider) {
                        DataStoreManager.SIMPMUSIC -> stringResource(Res.string.simpmusic_lyrics)
                        DataStoreManager.YOUTUBE -> stringResource(Res.string.youtube_transcript)
                        DataStoreManager.LRCLIB -> stringResource(Res.string.lrclib)
                        else -> stringResource(Res.string.unknown)
                    },
                    accentColor = accentColor,
                    onClick = { showLyricsProviderDialog = true }
                )
            }

            item {
                SettingsClickItem(
                    title = stringResource(Res.string.youtube_subtitle_language),
                    subtitle = youtubeSubtitleLanguage,
                    accentColor = accentColor,
                    onClick = { showYoutubeSubtitleDialog = true }
                )
            }

            item {
                SettingsToggleItem(
                    title = stringResource(Res.string.help_build_lyrics_database),
                    subtitle = stringResource(Res.string.help_build_lyrics_database_description),
                    checked = helpBuildLyricsDatabase,
                    accentColor = accentColor,
                    onCheckedChange = { enabled ->
                        viewModel.setHelpBuildLyricsDatabase(enabled)
                    }
                )
            }

            // Section: Contributor Info (shown when helping build database)
            if (helpBuildLyricsDatabase) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionHeader(title = "Contributor Info")
                }

                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.contributor_name),
                        subtitle = contributor?.name ?: "Not set",
                        accentColor = accentColor,
                        onClick = { showContributorNameDialog = true }
                    )
                }

                item {
                    SettingsClickItem(
                        title = stringResource(Res.string.contributor_email),
                        subtitle = contributor?.email ?: "Not set",
                        accentColor = accentColor,
                        onClick = { showContributorEmailDialog = true }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Provider Selection Dialog
    if (showProviderDialog) {
        val geminiLabel = stringResource(Res.string.gemini)
        val openaiLabel = stringResource(Res.string.openai)
        SelectionDialog(
            title = stringResource(Res.string.ai_provider),
            options = listOf(geminiLabel, openaiLabel),
            selectedOption = when (aiProvider) {
                DataStoreManager.AI_PROVIDER_OPENAI -> openaiLabel
                else -> geminiLabel
            },
            onOptionSelected = { selected ->
                viewModel.setAIProvider(
                    when (selected) {
                        openaiLabel -> DataStoreManager.AI_PROVIDER_OPENAI
                        else -> DataStoreManager.AI_PROVIDER_GEMINI
                    }
                )
            },
            onDismiss = { showProviderDialog = false }
        )
    }

    // API Key Input Dialog
    if (showApiKeyDialog) {
        var apiKeyInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text(stringResource(Res.string.ai_api_key)) },
            text = {
                Column {
                    Text(
                        "Enter your API key for the selected AI provider",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setAIApiKey(apiKeyInput)
                        showApiKeyDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Custom Model ID Dialog
    if (showCustomModelDialog) {
        var modelInput by remember { mutableStateOf(customModelId) }
        InputDialog(
            title = stringResource(Res.string.custom_ai_model_id),
            value = modelInput,
            onValueChange = { modelInput = it },
            label = "Model ID",
            onConfirm = {
                viewModel.setCustomModelId(modelInput)
                showCustomModelDialog = false
            },
            onDismiss = { showCustomModelDialog = false }
        )
    }

    // Translation Language Dialog
    if (showTranslationLanguageDialog) {
        var languageInput by remember { mutableStateOf(translationLanguage ?: "en") }
        AlertDialog(
            onDismissRequest = { showTranslationLanguageDialog = false },
            title = { Text(stringResource(Res.string.translation_language)) },
            text = {
                Column {
                    Text(
                        stringResource(Res.string.translation_language_message),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = languageInput,
                        onValueChange = { if (it.length <= 2) languageInput = it },
                        label = { Text("Language Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setTranslationLanguage(languageInput)
                        showTranslationLanguageDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTranslationLanguageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Lyrics Provider Selection Dialog
    if (showLyricsProviderDialog) {
        val simpmusicLabel = stringResource(Res.string.simpmusic_lyrics)
        val youtubeLabel = stringResource(Res.string.youtube_transcript)
        val lrclibLabel = stringResource(Res.string.lrclib)
        SelectionDialog(
            title = stringResource(Res.string.main_lyrics_provider),
            options = listOf(simpmusicLabel, youtubeLabel, lrclibLabel),
            selectedOption = when (mainLyricsProvider) {
                DataStoreManager.SIMPMUSIC -> simpmusicLabel
                DataStoreManager.YOUTUBE -> youtubeLabel
                DataStoreManager.LRCLIB -> lrclibLabel
                else -> simpmusicLabel
            },
            onOptionSelected = { selected ->
                viewModel.setLyricsProvider(
                    when (selected) {
                        youtubeLabel -> DataStoreManager.YOUTUBE
                        lrclibLabel -> DataStoreManager.LRCLIB
                        else -> DataStoreManager.SIMPMUSIC
                    }
                )
            },
            onDismiss = { showLyricsProviderDialog = false }
        )
    }

    // YouTube Subtitle Language Dialog
    if (showYoutubeSubtitleDialog) {
        var subtitleLanguageInput by remember { mutableStateOf(youtubeSubtitleLanguage) }
        AlertDialog(
            onDismissRequest = { showYoutubeSubtitleDialog = false },
            title = { Text(stringResource(Res.string.youtube_subtitle_language)) },
            text = {
                Column {
                    Text(
                        stringResource(Res.string.youtube_subtitle_language_message),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = subtitleLanguageInput,
                        onValueChange = { if (it.length <= 2) subtitleLanguageInput = it },
                        label = { Text("Language Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setYoutubeSubtitleLanguage(subtitleLanguageInput)
                        showYoutubeSubtitleDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showYoutubeSubtitleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Contributor Name Dialog
    if (showContributorNameDialog) {
        var nameInput by remember { mutableStateOf(contributor?.name ?: "") }
        InputDialog(
            title = stringResource(Res.string.contributor_name),
            value = nameInput,
            onValueChange = { nameInput = it },
            label = "Your Name",
            onConfirm = {
                viewModel.setContributorName(nameInput)
                showContributorNameDialog = false
            },
            onDismiss = { showContributorNameDialog = false }
        )
    }

    // Contributor Email Dialog
    if (showContributorEmailDialog) {
        var emailInput by remember { mutableStateOf(contributor?.email ?: "") }
        InputDialog(
            title = stringResource(Res.string.contributor_email),
            value = emailInput,
            onValueChange = { emailInput = it },
            label = "Your Email",
            onConfirm = {
                viewModel.setContributorEmail(emailInput)
                showContributorEmailDialog = false
            },
            onDismiss = { showContributorEmailDialog = false }
        )
    }
}
