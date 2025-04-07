package com.example.devotio.ui.prayers

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.devotio.data.Prayer
import com.example.devotio.data.PrayerCategory
import com.example.devotio.data.PrayerRepository
import com.example.devotio.player.AudioPlayerError
import com.example.devotio.service.AudioPlayerService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrayersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PrayerRepository()
    
    private val _prayerCategories = MutableLiveData<List<PrayerCategory>>()
    val prayerCategories: LiveData<List<PrayerCategory>> = _prayerCategories

    private val _favoritePrayers = MutableLiveData<List<Prayer>>()
    val favoritePrayers: LiveData<List<Prayer>> = _favoritePrayers

    private val _dailyPrayer = MutableLiveData<Prayer>()
    val dailyPrayer: LiveData<Prayer> = _dailyPrayer

    private val _currentPrayer = MutableLiveData<Prayer>()
    val currentPrayer: LiveData<Prayer> = _currentPrayer

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentPosition = MutableLiveData<Long>()
    val currentPosition: LiveData<Long> = _currentPosition

    private val _duration = MutableLiveData<Long>()
    val duration: LiveData<Long> = _duration

    private val _error = MutableLiveData<AudioPlayerError>()
    val error: LiveData<AudioPlayerError> = _error

    private var currentPlaylist: List<Prayer> = emptyList()
    private var currentPlaylistIndex: Int = 0

    init {
        loadPrayerCategories()
        loadFavoritePrayers()
        loadDailyPrayer()
    }

    private fun loadPrayerCategories() {
        viewModelScope.launch {
            repository.getPrayerCategories().collect { categories ->
                _prayerCategories.value = categories
            }
        }
    }

    private fun loadFavoritePrayers() {
        viewModelScope.launch {
            repository.getFavoritePrayers().collect { prayers ->
                _favoritePrayers.value = prayers
            }
        }
    }

    private fun loadDailyPrayer() {
        viewModelScope.launch {
            repository.getDailyPrayer().collect { prayer ->
                _dailyPrayer.value = prayer
            }
        }
    }

    fun playDailyPrayer() {
        _dailyPrayer.value?.let { prayer ->
            _currentPrayer.value = prayer
            currentPlaylist = listOf(prayer)
            currentPlaylistIndex = 0
            startPlayback(prayer)
        }
    }

    fun togglePlayPause() {
        val intent = Intent(getApplication(), AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY_PAUSE
        }
        getApplication<Application>().startService(intent)
    }

    fun playPreviousPrayer() {
        if (currentPlaylistIndex > 0) {
            currentPlaylistIndex--
            playCurrentPrayer()
        }
    }

    fun playNextPrayer() {
        if (currentPlaylistIndex < currentPlaylist.size - 1) {
            currentPlaylistIndex++
            playCurrentPrayer()
        }
    }

    private fun playCurrentPrayer() {
        currentPlaylist.getOrNull(currentPlaylistIndex)?.let { prayer ->
            _currentPrayer.value = prayer
            startPlayback(prayer)
        }
    }

    private fun startPlayback(prayer: Prayer) {
        val intent = Intent(getApplication(), AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
            putExtra("prayer_id", prayer.id)
            putExtra("prayer_title", prayer.title)
            putExtra("prayer_description", prayer.description)
            putExtra("prayer_audio_url", prayer.audioUrl)
        }
        getApplication<Application>().startService(intent)
    }

    fun seekTo(position: Int) {
        val intent = Intent(getApplication(), AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_SEEK
            putExtra("position", position)
        }
        getApplication<Application>().startService(intent)
    }

    fun toggleFavorite(prayer: Prayer) {
        viewModelScope.launch {
            repository.toggleFavorite(prayer)
            loadFavoritePrayers()
        }
    }

    fun navigateToCategory(categoryId: String) {
        viewModelScope.launch {
            repository.getPrayersByCategory(categoryId).collect { prayers ->
                currentPlaylist = prayers
                currentPlaylistIndex = 0
                playCurrentPrayer()
            }
        }
    }

    fun handleError(error: AudioPlayerError) {
        _error.value = error
    }
} 