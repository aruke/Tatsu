package org.rionlabs.tatsu.work

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import org.rionlabs.tatsu.work.VibrationsManager.VibeType.*
import timber.log.Timber

class VibrationsManager(app: Application) {

    private val vibrator: Vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        if (!vibrator.hasVibrator()) {
            Timber.e("Devices doesn't have a vibrator")
        }
    }

    fun vibrate(vibeType: VibeType) {
        if (!vibrator.hasVibrator()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(getVibrationEffectForO(vibeType))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(getVibrationEffect(vibeType))
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun getVibrationEffectForO(vibeType: VibeType): VibrationEffect {
        return when (vibeType) {
            WORK_FINISHED -> VibrationEffect.createOneShot(2000L, VibrationEffect.DEFAULT_AMPLITUDE)
            BREAK_FINISHED -> VibrationEffect.createOneShot(
                1500L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            TIMER_PAUSED -> VibrationEffect.createOneShot(500L, VibrationEffect.DEFAULT_AMPLITUDE)
        }
    }

    private fun getVibrationEffect(vibeType: VibeType): Long {
        return when (vibeType) {
            WORK_FINISHED -> 2000L
            BREAK_FINISHED -> 1500L
            TIMER_PAUSED -> 500L
        }
    }

    enum class VibeType {
        WORK_FINISHED,
        BREAK_FINISHED,
        TIMER_PAUSED
    }
}