package com.example.devotio.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prayer(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val categoryId: String,
    val duration: Long = 0,
    val isFavorite: Boolean = false
) : Parcelable 