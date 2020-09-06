package org.rionlabs.tatsu.work

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rionlabs.tatsu.data.dao.TimerDao
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.utils.Utility.NONE
import timber.log.Timber
import java.util.*

/**
 * Act as a controller and store for [Timer] objects. Do not instantiate except in application.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TimerController(
    private val coroutineScope: CoroutineScope,
    private val timerDao: TimerDao
) {

    private val activeTimerChannel = BroadcastChannel<Timer>(Channel.CONFLATED)

    private var activeTimer: Timer? = null

    private var currentCountdownTimer: CountDownTimer? = null

    init {
        coroutineScope.launch {
            // Check for active timers in database, if none, create one
            val timerList = timerDao.getActive()
            Timber.d("Received ${timerList.size} entries from database.")
            if (timerList.isEmpty()) {
                Timber.v("No active timers found.")
                activeTimerChannel.send(NONE)
            } else {
                val firstTimer = timerList.first()
                activeTimerChannel.send(firstTimer)
                Timber.v("Found active timers. Setting LiveData for $firstTimer")
                // TODO Discard other timers if there are more than one
            }

            // Continue to update activeTimer by observing activeTimerChannel
            activeTimerChannel.asFlow().collectLatest {
                activeTimer = it
            }
        }
    }

    fun createNewTimer(timerType: TimerType, durationInSeconds: Long) = coroutineScope.launch {
        Timber.v("Creating new timer...")
        val timerId = timerDao.insert(Timer(Calendar.getInstance(), durationInSeconds, timerType))
        val timer = timerDao.getWith(timerId)
        activeTimerChannel.send(timer)
    }

    /**
     * Returns [LiveData] for a [TimerState.RUNNING] or [TimerState.PAUSED] timer in store.
     * Check of there is any such timer by calling [isActiveTimerAvailable] method.
     * Throws [IllegalStateException] if no such timer in store.
     */
    fun observeActiveTimer(): Flow<Timer> {
        return activeTimerChannel.asFlow()
    }

    /**
     * Starts new timer & starts it immediately.
     */
    fun startTimer(timerType: TimerType, durationInSeconds: Long) = coroutineScope.launch {
        if (requireActiveTimer() == NONE) {
            Timber.v("Existing timer exists...")
        } else {
            createNewTimer(timerType, durationInSeconds)
        }
        Timber.v("Starting timer...")
        requireActiveTimer().apply {
            updateWithState(TimerState.RUNNING)
            startCountdownTimer(durationSecs)
        }
    }

    fun pauseTimer() = coroutineScope.launch {
        Timber.v("Pausing timer...")
        requireActiveTimer().updateWithState(newState = TimerState.IDLE)
        requireCurrentCountdownTimer().cancel()
    }

    fun resumeTimer() = coroutineScope.launch {
        Timber.v("Resuming timer...")
        val timer = requireActiveTimer()
        startCountdownTimer(timer.remainingSecs)
        timer.updateWithState(TimerState.RUNNING)
    }

    fun cancelTimer() {
        Timber.v("Cancelling timer...")
        val timer = requireActiveTimer()
        timerDao.deleteTimerWith(timer.id) // Delete from DB
        requireCurrentCountdownTimer().cancel() // Cancel countdown
        // tODO update channel
    }

    /**
     * Take an existing timer, updates [activeTimerChannel] & [timerDao].
     */
    private suspend fun Timer.updateWithState(newState: TimerState = this.state) {
        val newTimer = copy(state = newState)
        activeTimerChannel.send(newTimer)
        timerDao.insert(newTimer)
    }

    /**
     * Updates the duration and status by decrementing [Timer.remainingSecs] value by 1.
     */
    private fun updateDataAfterTick() = coroutineScope.launch {
        if (requireActiveTimer().remainingSecs == 0L) {
            requireActiveTimer().updateWithState(TimerState.FINISHED)
            return@launch
        }

        val newTimer = with(requireActiveTimer()) {
            copy(remainingSecs = remainingSecs - 1)
        }

        newTimer.updateWithState(TimerState.RUNNING)
    }

    private fun startCountdownTimer(durationInSeconds: Long) {
        currentCountdownTimer =
            object : CountDownTimer(
                durationInSeconds * 1000,
                1000
            ) {
                override fun onFinish() {
                    updateDataAfterTick()
                }

                override fun onTick(millisUntilFinished: Long) {
                    updateDataAfterTick()
                }
            }.start()
    }

    private fun requireCurrentCountdownTimer() =
        currentCountdownTimer ?: throw IllegalStateException("currentCountdownTimer is null")

    private fun requireActiveTimer() =
        activeTimer ?: throw IllegalStateException("activeTimer is null")
}