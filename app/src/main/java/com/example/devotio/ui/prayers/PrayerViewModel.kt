package com.example.devotio.ui.prayers

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devotio.data.Prayer
import com.example.devotio.data.PrayerCategory
import com.example.devotio.data.PrayerRepository

class PrayerViewModel : ViewModel() {
    private val repository = PrayerRepository()
    private var mediaPlayer: MediaPlayer? = null

    private val _categories = MutableLiveData<List<PrayerCategory>>()
    val categories: LiveData<List<PrayerCategory>> = _categories

    private val _favoritePrayers = MutableLiveData<List<Prayer>>()
    val favoritePrayers: LiveData<List<Prayer>> = _favoritePrayers

    private val _dailyPrayer = MutableLiveData<Prayer>()
    val dailyPrayer: LiveData<Prayer> = _dailyPrayer

    private val _currentPrayer = MutableLiveData<Prayer>()
    val currentPrayer: LiveData<Prayer> = _currentPrayer

    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    private val _currentPosition = MutableLiveData<Float>()
    val currentPosition: LiveData<Float> = _currentPosition

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadDailyPrayer()
        loadCategories()
        loadFavoritePrayers()
        _playbackState.value = PlaybackState.STOPPED
        _currentPosition.value = 0f
    }

    private fun loadDailyPrayer() {
        _dailyPrayer.value = repository.getDailyPrayer()
    }

    private fun loadCategories() {
        _categories.value = repository.getCategories()
    }

    private fun loadFavoritePrayers() {
        _favoritePrayers.value = repository.getFavoritePrayers()
    }

    fun playPrayer(prayer: Prayer) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(prayer.audioUrl)
                prepare()
                start()
                _currentPrayer.value = prayer
                _playbackState.value = PlaybackState.PLAYING
            }
        } catch (e: Exception) {
            _error.value = "Failed to play prayer: ${e.message}"
        }
    }

    fun playDailyPrayer() {
        _dailyPrayer.value?.let { prayer ->
            playPrayer(prayer)
        }
    }

    fun pausePrayer() {
        mediaPlayer?.pause()
        _playbackState.value = PlaybackState.PAUSED
    }

    fun resumePrayer() {
        mediaPlayer?.start()
        _playbackState.value = PlaybackState.PLAYING
    }

    fun stopPrayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _currentPrayer.value = null
        _playbackState.value = PlaybackState.STOPPED
        _currentPosition.value = 0f
    }

    fun seekTo(position: Float) {
        mediaPlayer?.seekTo((position * 1000).toInt())
        _currentPosition.value = position
    }

    fun toggleFavorite(prayer: Prayer) {
        repository.toggleFavorite(prayer.id)
        loadFavoritePrayers()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
} 