package com.example.devotio.data

import java.util.*

data class Temple(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val openingTime: String,
    val closingTime: String,
    val description: String,
    val imageUrl: String?,
    val events: List<TempleEvent>,
    val deity: String,
    val timing: String,
    val isFavorite: Boolean = false,
    val isNearby: Boolean = false
) 