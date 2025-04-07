package com.example.devotio.ui.home

data class Prayer(
    val id: String,
    val title: String,
    val category: PrayerCategory,
    val lyrics: String,
    val audioUrl: String,
    val duration: Long,
    val isFavorite: Boolean = false
)

enum class PrayerCategory {
    MORNING,
    EVENING,
    MANTRA,
    STOTRA,
    AARTI,
    BHAJAN
} 