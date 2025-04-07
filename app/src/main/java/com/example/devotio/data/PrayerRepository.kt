package com.example.devotio.data

import com.example.devotio.R
import com.example.devotio.models.Prayer
import com.example.devotio.models.PrayerCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PrayerRepository {
    private val prayers = mutableListOf<Prayer>()
    private val categories = mutableListOf<PrayerCategory>()
    private val favorites = mutableSetOf<String>()

    init {
        // Initialize with sample data
        initializeSampleData()
    }

    private fun initializeSampleData() {
        // Add sample categories
        categories.addAll(listOf(
            PrayerCategory(
                id = "morning",
                name = "Morning Prayers",
                iconResId = android.R.drawable.ic_menu_day
            ),
            PrayerCategory(
                id = "evening",
                name = "Evening Prayers",
                iconResId = R.drawable.ic_night
            ),
            PrayerCategory(
                id = "special",
                name = "Special Occasions",
                iconResId = R.drawable.ic_celebrate
            )
        ))

        // Add sample prayers
        prayers.addAll(listOf(
            Prayer(
                id = UUID.randomUUID().toString(),
                title = "Morning Prayer",
                description = "A prayer to start your day",
                audioUrl = "https://example.com/morning-prayer.mp3",
                categoryId = "morning"
            ),
            Prayer(
                id = UUID.randomUUID().toString(),
                title = "Evening Prayer",
                description = "A prayer to end your day",
                audioUrl = "https://example.com/evening-prayer.mp3",
                categoryId = "evening"
            ),
            Prayer(
                id = UUID.randomUUID().toString(),
                title = "Special Prayer",
                description = "A prayer for special occasions",
                audioUrl = "https://example.com/special-prayer.mp3",
                categoryId = "special"
            )
        ))
    }

    // Flow-based methods
    fun getPrayersByCategoryFlow(categoryId: String): Flow<List<Prayer>> = flow {
        emit(prayers.filter { it.categoryId == categoryId })
    }

    fun getDailyPrayerFlow(): Flow<Prayer> = flow {
        emit(prayers.first())
    }

    fun getFavoritePrayersFlow(): Flow<List<Prayer>> = flow {
        emit(prayers.filter { favorites.contains(it.id) })
    }

    fun getPrayerCategoriesFlow(): Flow<List<PrayerCategory>> = flow {
        emit(categories)
    }

    // Synchronous methods
    fun getPrayersByCategory(categoryId: String): List<Prayer> {
        return prayers.filter { it.categoryId == categoryId }
    }

    fun getDailyPrayer(): Prayer {
        return prayers.first()
    }

    fun getFavoritePrayers(): List<Prayer> {
        return prayers.filter { favorites.contains(it.id) }
    }

    fun getCategories(): List<PrayerCategory> {
        return categories
    }

    fun getPrayerById(id: String): Prayer? {
        return prayers.find { it.id == id }
    }

    fun toggleFavorite(prayerId: String) {
        if (favorites.contains(prayerId)) {
            favorites.remove(prayerId)
        } else {
            favorites.add(prayerId)
        }
    }

    suspend fun isFavorite(prayerId: String): Boolean {
        return favorites.contains(prayerId)
    }

    // Search functionality
    fun searchPrayers(query: String): List<Prayer> {
        return prayers.filter { prayer ->
            prayer.title.contains(query, ignoreCase = true) ||
            prayer.description.contains(query, ignoreCase = true)
        }
    }

    // Category-specific methods
    fun getPrayersByCategoryWithFavorites(categoryId: String): List<Prayer> {
        return prayers.filter { it.categoryId == categoryId }
    }

    fun getDailyPrayerWithCategory(categoryId: String): Prayer {
        return prayers.filter { it.categoryId == categoryId }.first()
    }

    fun getFavoritePrayersByCategory(categoryId: String): List<Prayer> {
        return prayers.filter { it.categoryId == categoryId && favorites.contains(it.id) }
    }
} 