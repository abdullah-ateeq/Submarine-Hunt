package com.example.submarinehunt.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.submarinehunt.model.SearchFeedback

/**
 * Handles sound effects and tactile haptic feedback for sonar and search events.
 * Fully offline, requiring no external asset files.
 */
class AudioHapticFeedbackHelper(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 65)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Plays sound effect corresponding to search feedback if sound is enabled.
     */
    fun playFeedbackSound(feedback: SearchFeedback, soundEnabled: Boolean) {
        if (!soundEnabled) return

        try {
            val generator = toneGenerator ?: ToneGenerator(AudioManager.STREAM_NOTIFICATION, 65).also { toneGenerator = it }
            when (feedback) {
                SearchFeedback.CLOSER -> {
                    // Higher pitch sonar ping
                    generator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 120)
                }
                SearchFeedback.FARTHER -> {
                    // Lower pitch sonar ping
                    generator.startTone(ToneGenerator.TONE_CDMA_LOW_L, 120)
                }
                SearchFeedback.VERY_CLOSE -> {
                    // Strong alerting sonar ping
                    generator.startTone(ToneGenerator.TONE_PROP_PROMPT, 180)
                }
                SearchFeedback.CLOSE -> {
                    // Mid-tone ping
                    generator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                }
                SearchFeedback.FAR, SearchFeedback.SAME_DISTANCE -> {
                    // Soft sonar ping
                    generator.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
                }
                SearchFeedback.FOUND -> {
                    // High celebratory acknowledgement tone
                    generator.startTone(ToneGenerator.TONE_PROP_ACK, 350)
                }
            }
        } catch (_: Exception) {
            // Graceful fallback if device audio service is unavailable
        }
    }

    /**
     * Triggers appropriate haptic feedback pattern based on feedback type.
     */
    fun performHaptic(feedback: SearchFeedback, hapticEnabled: Boolean) {
        if (!hapticEnabled) return

        try {
            val vib = vibrator ?: return
            if (!vib.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (feedback) {
                    SearchFeedback.FOUND -> {
                        // Success double pulse
                        val timings = longArrayOf(0, 45, 60, 100)
                        val amplitudes = intArrayOf(0, 180, 0, 255)
                        vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    SearchFeedback.VERY_CLOSE -> {
                        // Medium strength pulse
                        vib.vibrate(VibrationEffect.createOneShot(45, 180))
                    }
                    else -> {
                        // Light subtle tick
                        vib.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (feedback) {
                    SearchFeedback.FOUND -> vib.vibrate(longArrayOf(0, 40, 50, 90), -1)
                    SearchFeedback.VERY_CLOSE -> vib.vibrate(50)
                    else -> vib.vibrate(20)
                }
            }
        } catch (_: Exception) {
            // Silently handle devices where vibrator permission or hardware is unavailable
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
