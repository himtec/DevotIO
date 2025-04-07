package com.example.devotio.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devotio.R
import com.example.devotio.data.Prayer
import com.example.devotio.data.PrayerCategory

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to DevotIO"
    }
    val text: LiveData<String> = _text

    private val _dailyPrayer = MutableLiveData<Prayer>()
    val dailyPrayer: LiveData<Prayer> = _dailyPrayer

    private val _prayerCategories = MutableLiveData<List<PrayerCategory>>()
    val prayerCategories: LiveData<List<PrayerCategory>> = _prayerCategories

    private val _favoritePrayers = MutableLiveData<List<Prayer>>()
    val favoritePrayers: LiveData<List<Prayer>> = _favoritePrayers

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    init {
        // Initialize with sample data
        _dailyPrayer.value = Prayer(
            id = "1",
            title = "Morning Prayer",
            description = "Start your day with this beautiful morning prayer",
            categoryId = "1"
        )

        _prayerCategories.value = listOf(
            PrayerCategory("1", "Morning Prayers", R.drawable.ic_prayer_black_24dp),
            PrayerCategory("2", "Evening Prayers", R.drawable.ic_night),
            PrayerCategory("3", "Festival Prayers", R.drawable.ic_celebrate)
        )

        _favoritePrayers.value = emptyList()
        _isPlaying.value = false
    }

    fun togglePlayPause() {
        _isPlaying.value = !(_isPlaying.value ?: false)
    }

    fun toggleFavorite(prayer: Prayer) {
        val currentFavorites = _favoritePrayers.value?.toMutableList() ?: mutableListOf()
        if (prayer.isFavorite) {
            currentFavorites.removeIf { it.id == prayer.id }
        } else {
            currentFavorites.add(prayer.copy(isFavorite = true))
        }
        _favoritePrayers.value = currentFavorites
    }
}