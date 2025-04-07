package com.example.devotio.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.devotio.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

sealed class AudioPlayerError : Exception() {
    data class PrepareError(override val message: String) : AudioPlayerError()
    data class PlaybackError(override val message: String) : AudioPlayerError()
    data class AudioFocusError(override val message: String) : AudioPlayerError()
}

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var isPrepared = false
    private var currentAudioUrl: String? = null
    private var progressJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _error = MutableStateFlow<AudioPlayerError?>(null)
    val error: StateFlow<AudioPlayerError?> = _error.asStateFlow()

    private var onCompletionListener: (() -> Unit)? = null
    private var onErrorListener: ((AudioPlayerError) -> Unit)? = null
    private var onProgressListener: ((Int, Int) -> Unit)? = null
    private var onPreparedListener: ((Int) -> Unit)? = null

    init {
        setupMediaPlayer()
        setupAudioFocus()
    }

    fun getAudioSessionId(): Int {
        return mediaPlayer?.audioSessionId ?: 0
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                isPrepared = true
                _duration.value = duration
                _error.value = null
                onPreparedListener?.invoke(duration)
            }
            setOnCompletionListener {
                onCompletionListener?.invoke()
                _isPlaying.value = false
                _currentPosition.value = 0
                stopProgressUpdates()
            }
            setOnErrorListener { _, _, _ ->
                handleError()
                true
            }
        }
    }

    private fun setupAudioFocus() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { focusChange ->
                handleAudioFocusChange(focusChange)
            }
            .build()
    }

    private fun handleAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPrepared) {
                    mediaPlayer?.start()
                    _isPlaying.value = true
                    startProgressUpdates()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    _isPlaying.value = false
                    stopProgressUpdates()
                }
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        return audioFocusRequest?.let { request ->
            audioManager?.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } ?: false
    }

    private fun abandonAudioFocus() {
        audioFocusRequest?.let { request ->
            audioManager?.abandonAudioFocusRequest(request)
        }
    }

    fun setDataSource(audioUrl: String) {
        currentAudioUrl = audioUrl
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(audioUrl))
                setOnCompletionListener {
                    onCompletionListener?.invoke()
                    _isPlaying.value = false
                    _currentPosition.value = 0
                    stopProgressUpdates()
                }
                setOnErrorListener { _, _, _ ->
                    onErrorListener?.invoke(AudioPlayerError.PlaybackError("Media player error"))
                    _isPlaying.value = false
                    stopProgressUpdates()
                    true
                }
                setOnPreparedListener {
                    isPrepared = true
                    _duration.value = duration
                    onPreparedListener?.invoke(duration)
                }
            }
        } catch (e: IOException) {
            onErrorListener?.invoke(AudioPlayerError.PrepareError("Error setting data source: ${e.message}"))
        }
    }

    fun prepare() {
        try {
            mediaPlayer?.prepare()
            isPrepared = true
        } catch (e: IOException) {
            onErrorListener?.invoke(AudioPlayerError.PrepareError("Error preparing media player: ${e.message}"))
            isPrepared = false
        }
    }

    fun play(): Boolean {
        if (!isPrepared) {
            onErrorListener?.invoke(AudioPlayerError.PrepareError("Media player not prepared"))
            return false
        }

        if (!requestAudioFocus()) {
            onErrorListener?.invoke(AudioPlayerError.AudioFocusError("Failed to gain audio focus"))
            return false
        }

        return try {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressUpdates()
            true
        } catch (e: Exception) {
            onErrorListener?.invoke(AudioPlayerError.PlaybackError("Error starting playback: ${e.message}"))
            _isPlaying.value = false
            stopProgressUpdates()
            false
        }
    }

    fun pause(): Boolean {
        return try {
            mediaPlayer?.pause()
            _isPlaying.value = false
            stopProgressUpdates()
            abandonAudioFocus()
            true
        } catch (e: Exception) {
            onErrorListener?.invoke(AudioPlayerError.PlaybackError("Error pausing playback: ${e.message}"))
            false
        }
    }

    fun resume(): Boolean {
        if (!isPrepared) {
            onErrorListener?.invoke(AudioPlayerError.PrepareError("Media player not prepared"))
            return false
        }

        if (!requestAudioFocus()) {
            onErrorListener?.invoke(AudioPlayerError.AudioFocusError("Failed to gain audio focus"))
            return false
        }

        return try {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressUpdates()
            true
        } catch (e: Exception) {
            onErrorListener?.invoke(AudioPlayerError.PlaybackError("Error resuming playback: ${e.message}"))
            _isPlaying.value = false
            stopProgressUpdates()
            false
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            _isPlaying.value = false
            _currentPosition.value = 0
            stopProgressUpdates()
            abandonAudioFocus()
        } catch (e: Exception) {
            onErrorListener?.invoke(AudioPlayerError.PlaybackError("Error stopping playback: ${e.message}"))
        }
    }

    fun seekTo(position: Int) {
        try {
            mediaPlayer?.seekTo(position)
            _currentPosition.value = position
        } catch (e: Exception) {
            onErrorListener?.invoke(AudioPlayerError.PlaybackError("Error seeking: ${e.message}"))
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = coroutineScope.launch(Dispatchers.IO) {
            while (isActive && _isPlaying.value) {
                try {
                    mediaPlayer?.let { player ->
                        val currentPos = player.currentPosition
                        val totalDuration = player.duration
                        withContext(Dispatchers.Main) {
                            _currentPosition.value = currentPos
                            _duration.value = totalDuration
                            onProgressListener?.invoke(currentPos, totalDuration)
                        }
                    }
                    delay(1000)
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating progress", e)
                    break
                }
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    fun setOnErrorListener(listener: (AudioPlayerError) -> Unit) {
        onErrorListener = listener
    }

    fun setOnProgressListener(listener: (Int, Int) -> Unit) {
        onProgressListener = listener
    }

    fun setOnPreparedListener(listener: (Int) -> Unit) {
        onPreparedListener = listener
    }

    private fun handleError() {
        onErrorListener?.invoke(AudioPlayerError.PlaybackError("Media player error"))
        _isPlaying.value = false
        stopProgressUpdates()
    }

    fun release() {
        try {
            stopProgressUpdates()
            mediaPlayer?.release()
            mediaPlayer = null
            abandonAudioFocus()
            isPrepared = false
            _isPlaying.value = false
            _currentPosition.value = 0
            _duration.value = 0
            _error.value = null
            coroutineScope.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing media player", e)
        }
    }

    companion object {
        private const val TAG = "AudioPlayer"
    }
} 