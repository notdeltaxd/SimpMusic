package com.maxrave.simpmusic.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Settings categories for the redesigned settings screen.
 * Each category has an id, title, subtitle, and icon.
 */
enum class SettingsCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
) {
    ACCOUNT(
        id = "account",
        title = "Account & Content",
        subtitle = "YouTube account, language, location, quality",
        icon = Icons.Rounded.AccountCircle
    ),
    PLAYBACK(
        id = "playback",
        title = "Playback",
        subtitle = "Normalize, skip silent, save state",
        icon = Icons.Rounded.MusicNote
    ),
    MUSIC_SOURCES(
        id = "music_sources",
        title = "Music Sources",
        subtitle = "JioSaavn integration",
        icon = Icons.AutoMirrored.Rounded.QueueMusic
    ),
    APPEARANCE(
        id = "appearance",
        title = "Appearance",
        subtitle = "Blur effects, navigation bar, themes",
        icon = Icons.Rounded.Palette
    ),
    AI_INTEGRATION(
        id = "ai",
        title = "AI Integration",
        subtitle = "Translation, lyrics, AI provider",
        icon = Icons.Rounded.SmartToy
    ),
    STORAGE(
        id = "storage",
        title = "Storage & Cache",
        subtitle = "Player cache, downloads, thumbnails",
        icon = Icons.Rounded.Storage
    ),
    EQUALIZER(
        id = "equalizer",
        title = "Equalizer",
        subtitle = "Audio frequencies and presets",
        icon = Icons.Rounded.GraphicEq
    ),
    ABOUT(
        id = "about",
        title = "About",
        subtitle = "Version, credits, libraries",
        icon = Icons.Rounded.Info
    );

    companion object {
        fun fromId(id: String): SettingsCategory? = entries.find { it.id == id }
    }
}
