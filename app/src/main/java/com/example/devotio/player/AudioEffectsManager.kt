package com.example.devotio.player

import android.content.Context
import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.util.Log
import com.example.devotio.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioEffectsManager(private val context: Context) {
    private var equalizer: Equalizer? = null
    private var bassBoostEffect: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    private var audioSessionId: Int = 0
    private var isEnabled = false

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _bassBoost = MutableStateFlow(0.toShort())
    val bassBoost: StateFlow<Short> = _bassBoost.asStateFlow()

    private val _trebleBoost = MutableStateFlow(0.toShort())
    val trebleBoost: StateFlow<Short> = _trebleBoost.asStateFlow()

    private val _reverbEnabled = MutableStateFlow(false)
    val reverbEnabled: StateFlow<Boolean> = _reverbEnabled.asStateFlow()

    private val _loudnessEnhancement = MutableStateFlow(false)
    val loudnessEnhancement: StateFlow<Boolean> = _loudnessEnhancement.asStateFlow()

    fun setAudioSessionId(sessionId: Int) {
        audioSessionId = sessionId
        initializeEffects()
    }

    private fun initializeEffects() {
        try {
            equalizer = Equalizer(0, audioSessionId)
            bassBoostEffect = BassBoost(0, audioSessionId)
            virtualizer = Virtualizer(0, audioSessionId)
            presetReverb = PresetReverb(0, audioSessionId)
            loudnessEnhancer = LoudnessEnhancer(audioSessionId)

            // Set default values
            bassBoostEffect?.setStrength(0.toShort())
            virtualizer?.setStrength(0.toShort())
            presetReverb?.setPreset(PresetReverb.PRESET_NONE)
            presetReverb?.setEnabled(false)
            loudnessEnhancer?.setEnabled(false)

            isEnabled = true
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing audio effects", e)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed.coerceIn(0.5f, 2.0f)
    }

    fun setBassBoost(strength: Short) {
        try {
            if (isEnabled && bassBoostEffect != null) {
                bassBoostEffect?.setStrength(strength)
                _bassBoost.value = strength
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting bass boost", e)
        }
    }

    fun setTrebleBoost(strength: Short) {
        try {
            if (isEnabled && equalizer != null) {
                // Adjust the high frequency bands for treble boost
                val bandCount = equalizer?.numberOfBands ?: 0
                for (i in (bandCount / 2) until bandCount) {
                    equalizer?.setBandLevel(i.toShort(), strength)
                }
                _trebleBoost.value = strength
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting treble boost", e)
        }
    }

    fun setReverbEnabled(enabled: Boolean) {
        try {
            if (isEnabled && presetReverb != null) {
                presetReverb?.setEnabled(enabled)
                if (enabled) {
                    presetReverb?.setPreset(PresetReverb.PRESET_LARGEHALL)
                }
                _reverbEnabled.value = enabled
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb", e)
        }
    }

    fun setLoudnessEnhancement(enabled: Boolean) {
        try {
            if (isEnabled && loudnessEnhancer != null) {
                loudnessEnhancer?.setEnabled(enabled)
                if (enabled) {
                    loudnessEnhancer?.setTargetGain(1000) // 10dB boost
                }
                _loudnessEnhancement.value = enabled
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting loudness enhancement", e)
        }
    }

    fun setEqualizerBand(band: Int, level: Short) {
        try {
            if (isEnabled && equalizer != null) {
                equalizer?.setBandLevel(band.toShort(), level)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting equalizer band", e)
        }
    }

    fun setBassBoostStrength(strength: Short) {
        try {
            if (isEnabled && bassBoostEffect != null) {
                bassBoostEffect?.setStrength(strength)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting bass boost strength", e)
        }
    }

    fun setVirtualizerStrength(strength: Short) {
        try {
            if (isEnabled && virtualizer != null) {
                virtualizer?.setStrength(strength)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting virtualizer strength", e)
        }
    }

    fun setPresetReverb(preset: Short) {
        try {
            if (isEnabled && presetReverb != null) {
                presetReverb?.setPreset(preset)
                presetReverb?.setEnabled(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting preset reverb", e)
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoostEffect?.release()
            virtualizer?.release()
            presetReverb?.release()
            loudnessEnhancer?.release()
            isEnabled = false
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing audio effects", e)
        }
    }

    companion object {
        private const val TAG = "AudioEffectsManager"
    }
} 