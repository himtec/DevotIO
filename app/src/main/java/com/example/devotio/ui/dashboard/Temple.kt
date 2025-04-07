package com.example.devotio.ui.dashboard

import java.util.Date

data class Temple(
    val id: String,
    val name: String,
    val deity: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val contact: String,
    val website: String?,
    val photos: List<String>,
    val timings: TempleTimings,
    val events: List<TempleEvent>,
    val isFavorite: Boolean = false
)

data class TempleTimings(
    val openingTime: String,
    val closingTime: String,
    val specialTimings: Map<String, String> = emptyMap()
)

data class TempleEvent(
    val id: String,
    val title: String,
    val description: String,
    val date: Date,
    val startTime: String,
    val endTime: String,
    val isRecurring: Boolean = false
) 