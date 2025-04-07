package com.example.devotio.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devotio.models.Prayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val audioPlayer = AudioPlayer(application)
    private val audioEffectsManager = AudioEffectsManager(application)
    private val mediaSessionManager = MediaSessionManager(application)

    private val _currentPrayer = MutableStateFlow<Prayer?>(null)
    val currentPrayer: StateFlow<Prayer?> = _currentPrayer.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        setupAudioPlayer()
        setupMediaSession()
        setupAudioEffects()
    }

    private fun setupAudioPlayer() {
        audioPlayer.setOnPreparedListener { duration ->
            _duration.value = duration
            updateMediaSession()
        }

        audioPlayer.setOnCompletionListener {
            _isPlaying.value = false
            _currentPosition.value = 0
            updateMediaSession()
        }

        audioPlayer.setOnProgressListener { position, duration ->
            _currentPosition.value = position
            _duration.value = duration
            updateMediaSession()
        }

        audioPlayer.setOnErrorListener { error ->
            _error.value = error.message
            _isPlaying.value = false
            updateMediaSession()
        }
    }

    private fun setupMediaSession() {
        try {
            mediaSessionManager.createSession()
        } catch (e: Exception) {
            _error.value = "Failed to create media session: ${e.message}"
        }
    }

    private fun setupAudioEffects() {
        try {
            audioEffectsManager.setAudioSessionId(audioPlayer.getAudioSessionId())
        } catch (e: Exception) {
            _error.value = "Failed to setup audio effects: ${e.message}"
        }
    }

    fun playPrayer(prayer: Prayer) {
        viewModelScope.launch {
            try {
                if (prayer.audioUrl.isBlank()) {
                    _error.value = "Invalid audio URL"
                    return@launch
                }

                try {
                    Uri.parse(prayer.audioUrl)
                } catch (e: Exception) {
                    _error.value = "Invalid audio URL format"
                    return@launch
                }

                _currentPrayer.value = prayer
                _error.value = null
                audioPlayer.setDataSource(prayer.audioUrl)
                if (audioPlayer.play()) {
                    _isPlaying.value = true
                    updateMediaSession()
                } else {
                    _error.value = "Failed to start playback"
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isPlaying.value = false
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    private fun play() {
        viewModelScope.launch {
            try {
                if (audioPlayer.play()) {
                    _isPlaying.value = true
                    _error.value = null
                    updateMediaSession()
                } else {
                    _error.value = "Failed to start playback"
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isPlaying.value = false
            }
        }
    }

    private fun pause() {
        viewModelScope.launch {
            try {
                if (audioPlayer.pause()) {
                    _isPlaying.value = false
                    _error.value = null
                    updateMediaSession()
                } else {
                    _error.value = "Failed to pause playback"
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isPlaying.value = false
            }
        }
    }

    fun seekTo(position: Int) {
        viewModelScope.launch {
            try {
                audioPlayer.seekTo(position)
                _currentPosition.value = position
                _error.value = null
                updateMediaSession()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getAudioEffectsManager(): AudioEffectsManager = audioEffectsManager

    private fun updateMediaSession() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    mediaSessionManager.updatePlaybackState(
                        isPlaying = _isPlaying.value,
                        position = _currentPosition.value,
                        speed = audioEffectsManager.playbackSpeed.value
                    )

                    _currentPrayer.value?.let { prayer ->
                        mediaSessionManager.updateMetadata(
                            title = prayer.title,
                            artist = prayer.description,
                            duration = _duration.value
                        )
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to update media session: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        audioEffectsManager.release()
        mediaSessionManager.release()
    }
} 