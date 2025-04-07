package com.example.devotio.data

import java.util.Date

data class TempleEvent(
    val id: Int,
    val templeId: Int,
    val name: String,
    val description: String,
    val date: Date,
    val startTime: String,
    val endTime: String,
    val imageUrl: String? = null
) 