package com.example.devotio.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.devotio.MainActivity
import com.example.devotio.R
import com.example.devotio.models.Prayer
import com.example.devotio.player.AudioPlayer
import com.example.devotio.player.AudioPlayerError
import com.example.devotio.player.MediaSessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerService : android.app.Service() {
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var notificationManager: NotificationManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPrayer = MutableStateFlow<Prayer?>(null)
    val currentPrayer: StateFlow<Prayer?> = _currentPrayer.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        audioPlayer = AudioPlayer(this)
        mediaSessionManager = MediaSessionManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        setupAudioPlayer()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for audio playback"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupAudioPlayer() {
        audioPlayer.setOnCompletionListener {
            serviceScope.launch {
                _isPlaying.value = false
                _currentPosition.value = 0
                updateNotification()
            }
        }

        audioPlayer.setOnErrorListener { error ->
            when (error) {
                is AudioPlayerError.PrepareError -> {
                    // Handle prepare error
                }
                is AudioPlayerError.PlaybackError -> {
                    // Handle playback error
                }
                is AudioPlayerError.AudioFocusError -> {
                    // Handle audio focus error
                }
            }
        }

        audioPlayer.setOnProgressListener { position, duration ->
            serviceScope.launch {
                _currentPosition.value = position
                _duration.value = duration
                updateNotification()
            }
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val prayer = _currentPrayer.value
        val isPlaying = _isPlaying.value
        val currentPosition = _currentPosition.value
        val duration = _duration.value

        val contentIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = NotificationCompat.Action(
            if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
            if (isPlaying) "Pause" else "Play",
            createPendingIntent(if (isPlaying) ACTION_PAUSE else ACTION_PLAY)
        )

        val stopAction = NotificationCompat.Action(
            R.drawable.ic_media_stop,
            "Stop",
            createPendingIntent(ACTION_STOP)
        )

        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSessionManager.getSessionToken())
            .setShowActionsInCompactView(0, 1)
            .setShowCancelButton(true)
            .setCancelButtonIntent(createPendingIntent(ACTION_STOP))

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(prayer?.title ?: "No prayer selected")
            .setContentText(prayer?.description ?: "")
            .setContentIntent(pendingIntent)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, AudioPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                audioPlayer.play()
            }
            ACTION_PAUSE -> {
                audioPlayer.pause()
            }
            ACTION_STOP -> {
                audioPlayer.stop()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
        mediaSessionManager.release()
    }

    companion object {
        private const val CHANNEL_ID = "audio_player_channel"
        private const val NOTIFICATION_ID = 1

        const val ACTION_PLAY = "com.example.devotio.action.PLAY"
        const val ACTION_PAUSE = "com.example.devotio.action.PAUSE"
        const val ACTION_STOP = "com.example.devotio.action.STOP"
    }
} 