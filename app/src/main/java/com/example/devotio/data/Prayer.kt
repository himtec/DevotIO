package com.example.devotio.data

data class Prayer(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String? = null,
    val categoryId: String,
    val isFavorite: Boolean = false
) 