package com.example.devotio.player

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaSessionManager(private val context: Context) {
    private var mediaSession: MediaSessionCompat? = null

    fun createSession() {
        if (mediaSession == null) {
            mediaSession = MediaSessionCompat(context, "DevotIO").apply {
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
                setCallback(object : MediaSessionCompat.Callback() {
                    override fun onPlay() {
                        // Handle play command
                    }

                    override fun onPause() {
                        // Handle pause command
                    }

                    override fun onSkipToNext() {
                        // Handle next command
                    }

                    override fun onSkipToPrevious() {
                        // Handle previous command
                    }

                    override fun onSeekTo(pos: Long) {
                        // Handle seek command
                    }
                })
                isActive = true
            }
        }
    }

    fun updatePlaybackState(isPlaying: Boolean, position: Int, speed: Float) {
        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder()
            .setState(state, position.toLong(), speed)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
        mediaSession?.setPlaybackState(playbackState)
    }

    fun updateMetadata(title: String, artist: String, duration: Int) {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
            .build()
        mediaSession?.setMetadata(metadata)
    }

    fun getSessionToken(): MediaSessionCompat.Token? {
        return mediaSession?.sessionToken
    }

    fun release() {
        mediaSession?.apply {
            isActive = false
            release()
        }
        mediaSession = null
    }

    companion object {
        private const val TAG = "MediaSessionManager"
    }
}