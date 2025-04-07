package com.example.devotio.data

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val favoriteTemples: List<Int> = emptyList(),
    val favoritePrayers: List<Int> = emptyList(),
    val dailyPrayerReminder: Boolean = false,
    val reminderTime: String? = null
) 