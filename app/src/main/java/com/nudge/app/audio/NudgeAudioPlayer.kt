package com.nudge.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

/**
 * Native PCM Waveform Synthesizer using Android AudioTrack
 * Generates custom synthesized sounds without external MP3 dependencies.
 */
object NudgeAudioPlayer {

    suspend fun playNudgeSound(type: String) = withContext(Dispatchers.Default) {
        val sampleRate = 44100
        when (type) {
            "dinner_bell" -> playDinnerBell(sampleRate)
            "yahoo_buzz" -> playYahooBuzz(sampleRate)
            else -> playStandardTone(sampleRate)
        }
    }

    private fun playDinnerBell(sampleRate: Int) {
        val durationMs = 1800
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        val bellHarmonics = listOf(
            1568.0 to 0.5f,
            2093.0 to 0.3f,
            2637.0 to 0.15f
        )

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = Math.exp(-t * 2.8)
            var sampleVal = 0.0

            for ((freq, amp) in bellHarmonics) {
                sampleVal += sin(2.0 * Math.PI * freq * t) * amp
            }
            buffer[i] = (sampleVal * decay * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeToTrack(buffer, sampleRate)
    }

    private fun playYahooBuzz(sampleRate: Int) {
        val durationMs = 450
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val osc1 = if ((t * 140.0 % 1.0) < 0.5) 1.0 else -1.0
            val osc2 = if ((t * 144.0 % 1.0) < 0.5) 1.0 else -1.0
            val v = (osc1 + osc2) * 0.4
            buffer[i] = (v * Short.MAX_VALUE).toInt().toShort()
        }
        writeToTrack(buffer, sampleRate)
    }

    private fun playStandardTone(sampleRate: Int) {
        val durationMs = 300
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            buffer[i] = (sin(2.0 * Math.PI * 880.0 * t) * 0.4 * Short.MAX_VALUE).toInt().toShort()
        }
        writeToTrack(buffer, sampleRate)
    }

    private fun writeToTrack(buffer: ShortArray, sampleRate: Int) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
    }
}