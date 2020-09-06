package org.rionlabs.tatsu.work.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.rionlabs.tatsu.TatsuApp
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.utils.NotificationUtils
import org.rionlabs.tatsu.work.SettingsManager
import org.rionlabs.tatsu.work.SilentModeManager
import org.rionlabs.tatsu.work.TimerController
import org.rionlabs.tatsu.work.VibrationsManager
import timber.log.Timber

class TimerService : Service(), KoinComponent {

    private val timerController: TimerController by inject()

    private val settingManager: SettingsManager by inject()

    private val silentModeManager: SilentModeManager by inject()

    private val vibrationsManager: VibrationsManager by inject()

    private var isForeground: Boolean = false

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate() called")
        (application as TatsuApp).coroutineScope.launch {
            timerController.observeActiveTimer().collect { timer ->
                when (timer.state) {
                    TimerState.IDLE -> {
                        if (timer.isPaused) {
                            vibrationsManager.vibrate(VibrationsManager.VibeType.TIMER_PAUSED)
                            // TODO: Update notification
                        } else {
                            stopForeground(true)
                            isForeground = false
                        }
                    }
                    TimerState.FINISHED -> {
                        // TODO: Update notification
                        stopForeground(false)
                        isForeground = false
                        if (timer.type == TimerType.WORK) {
                            vibrationsManager.vibrate(VibrationsManager.VibeType.WORK_FINISHED)
                        } else {
                            vibrationsManager.vibrate(VibrationsManager.VibeType.BREAK_FINISHED)
                        }
                    }
                    TimerState.RUNNING -> {
                        if (isForeground) {
                            NotificationUtils.updateTimerNotification(this@TimerService, timer)
                        } else {
                            val notification =
                                NotificationUtils.buildForTimer(this@TimerService, timer)
                            startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notification)
                            isForeground = true
                        }

                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand() called with: intent = [$intent], flags = [$flags], startId = [$startId]")
        intent ?: run {
            Timber.i("onStartCommand: Service called with null intent")
            return START_NOT_STICKY
        }

        when (intent.action) {
            ACTION_START_WORK_TIMER -> {
                val duration = settingManager.getWorkTimerInMinutes() * 60L
                timerController.startTimer(TimerType.WORK, duration)

                if (settingManager.silentMode) {
                    silentModeManager.turnOnSilentMode()
                }
            }
            ACTION_START_BREAK_TIMER -> {
                val duration = settingManager.getBreakTimerInMinutes() * 60L
                timerController.startTimer(TimerType.BREAK, duration)
            }
            ACTION_PAUSE_TIMER -> {
                timerController.pauseTimer()
                if (settingManager.silentMode) {
                    silentModeManager.turnOffSilentMode()
                }
            }
            ACTION_RESUME_TIMER -> {
                timerController.resumeTimer()
                if (settingManager.silentMode) {
                    silentModeManager.turnOnSilentMode()
                }
            }
            ACTION_STOP_TIMER -> {
                timerController.cancelTimer()
                if (settingManager.silentMode) {
                    silentModeManager.turnOffSilentMode()
                }
                stopForeground(false)
                isForeground = false
            }
        }


        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Timber.d("onDestroy() called")
        super.onDestroy()
    }

    companion object {

        private const val ACTION_START_WORK_TIMER = "org.rionlabs.tatsu.ACTION_START_WORK_TIMER"
        private const val ACTION_START_BREAK_TIMER = "org.rionlabs.tatsu.ACTION_START_BREAK_TIMER"
        private const val ACTION_PAUSE_TIMER = "org.rionlabs.tatsu.ACTION_PAUSE_TIMER"
        private const val ACTION_RESUME_TIMER = "org.rionlabs.tatsu.ACTION_RESUME_TIMER"
        private const val ACTION_STOP_TIMER = "org.rionlabs.tatsu.ACTION_STOP_TIMER"

        fun startWorkTimer(context: Context) {
            context.startService(Intent(context, TimerService::class.java).apply {
                action = ACTION_START_WORK_TIMER
            })
        }

        fun startBreakTimer(context: Context) {
            context.startService(Intent(context, TimerService::class.java).apply {
                action = ACTION_START_BREAK_TIMER
            })
        }

        fun pauseTimer(context: Context) {
            context.startService(Intent(context, TimerService::class.java).apply {
                action = ACTION_PAUSE_TIMER
            })
        }

        fun resumeTimer(context: Context) {
            context.startService(Intent(context, TimerService::class.java).apply {
                action = ACTION_RESUME_TIMER
            })
        }

        fun stopTimer(context: Context) {
            context.startService(Intent(context, TimerService::class.java).apply {
                action = ACTION_STOP_TIMER
            })
        }
    }
}