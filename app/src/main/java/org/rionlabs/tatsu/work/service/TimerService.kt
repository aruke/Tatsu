package org.rionlabs.tatsu.work.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Observer
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState.*
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

    private val binder: Binder = Binder(this)

    private val timerObserver = Observer<Timer> {
        it?.let { timer ->
            NotificationUtils.updateTimerNotification(this, timer)
            when (timer.state) {
                IDLE -> {
                    // TODO: Update notification
                    stopForeground(false)
                }
                FINISHED -> {
                    // TODO: Update notification
                    stopForeground(false)
                    if (timer.type == TimerType.WORK) {
                        vibrationsManager.vibrate(VibrationsManager.VibeType.WORK_FINISHED)
                    } else {
                        vibrationsManager.vibrate(VibrationsManager.VibeType.BREAK_FINISHED)
                    }
                }
                RUNNING -> {
                    val notification = NotificationUtils.buildForTimer(this, timer)
                    startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notification)
                }
                PAUSED -> {
//                    val notification = NotificationUtils.buildForTimer(this, timer)
//                    startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notification)
                    // TODO: Update notification
                    vibrationsManager.vibrate(VibrationsManager.VibeType.TIMER_PAUSED)
                }
                CANCELLED -> {
                    stopForeground(true)
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
                timerController.startNewTimer(TimerType.WORK, duration)
                    .observeForever(timerObserver)

                //val timer = activeTimer.value!!
                //val notification = NotificationUtils.buildForTimer(this, timer)
                //startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notification)

                if (settingManager.silentMode) {
                    silentModeManager.turnOnSilentMode()
                }
            }
            ACTION_START_BREAK_TIMER -> {
                val duration = settingManager.getBreakTimerInMinutes() * 60L
                timerController.startNewTimer(TimerType.BREAK, duration)
                    .observeForever(timerObserver)

                //val timer = activeTimer.value!!
                //val notification = NotificationUtils.buildForTimer(this, timer)
                //startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notification)
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
            }
        }


        return START_NOT_STICKY
    }

    override fun onRebind(intent: Intent?) {
        Timber.d("onRebind() called with: intent = [$intent]")
        super.onRebind(intent)
    }

    override fun onCreate() {
        Timber.d("onCreate() called")
        super.onCreate()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("onUnbind() called with: intent = [$intent]")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Timber.d("onDestroy() called")
        if (timerController.isActiveTimerAvailable()) {
            val activeTimer = timerController.getActiveTimer()
            activeTimer.removeObserver(timerObserver)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.d("onBind() called with: intent = [$intent]")
        return binder
    }

    inner class Binder(private val context: Context) : android.os.Binder()

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