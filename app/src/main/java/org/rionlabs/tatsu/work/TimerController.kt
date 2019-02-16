package org.rionlabs.tatsu.work

import android.app.Application
import androidx.lifecycle.*
import org.rionlabs.tatsu.data.AppDatabase
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.utils.*
import timber.log.Timber
import java.lang.ref.SoftReference

/**
 * Act as a controller and store for [Timer] objects. Do not instantiate except in application.
 */
class TimerController(app: Application) : LifecycleObserver {

    private val activeTimerData = MutableLiveData<Timer>()

    private lateinit var timerTask: TimerTask

    private var timerDao = AppDatabase.getInstance(app.applicationContext).timerDao()

    /**
     * Initializes the store with values from database.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val timerList = timerDao.getActive()

        Timber.d("Received ${timerList.size} entries from database.")

        // TODO Discard other timers if there are more than one
        if (timerList.isEmpty()) {
            Timber.i("No active timers found.")
        } else {
            val firstTimer = timerList.first()
            activeTimerData.value = firstTimer
            Timber.d("Found active timers. Setting LiveData for $firstTimer")
        }
    }

    /**
     * Starts timer. Do not call directly, use [startNewTimer] method.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        timerTask = TimerTask(this)
        timerTask.execute()
    }

    /**
     * Returns true if there is any [TimerState.RUNNING] or [TimerState.PAUSED] timer in store.
     */
    fun isActiveTimerAvailable(): Boolean {
        return activeTimerData.value?.isActive() ?: false
    }

    /**
     * Returns [LiveData] for a [TimerState.RUNNING] or [TimerState.PAUSED] timer in store.
     * Check of there is any such timer by calling [isActiveTimerAvailable] method.
     * Throws [IllegalStateException] if no such timer in store.
     */
    fun getActiveTimer(): LiveData<Timer> {
        if (isActiveTimerAvailable()) {
            return activeTimerData
        } else {
            throw IllegalStateException("No running or paused timer available.")
        }
    }

    fun startNewTimer(durationInSeconds: Long): LiveData<Timer> {
        if (isActiveTimerAvailable()) {
            throw IllegalStateException("Already running or paused timer detected.")
        } else {
            val timerId = timerDao.insert(Timer(TimeUtils.currentTimeEpoch(), durationInSeconds))
            timerDao.getWith(timerId).updateLiveData(activeTimerData, state = TimerState.RUNNING)
            return activeTimerData
        }
    }

    fun pauseTimer() {
        updateStateAndDump(TimerState.PAUSED)
    }

    fun resumeTimer() {
        updateStateAndDump(TimerState.RUNNING)
    }

    fun cancelTimer() {
        updateStateAndDump(TimerState.CANCELLED)
    }

    /**
     * Stops the timer. Do not call directly.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        timerTask.cancel(true)
    }

    /**
     * Clears the store and dumps data into database.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        dumpToDatabase()
        activeTimerData.value = null
    }

    private fun updateStateAndDump(state: TimerState) {
        val oldTimer = activeTimerData.value
        oldTimer?.let {
            it.updateLiveData(activeTimerData, state = state)
            dumpToDatabase()
        } ?: run {
            throw IllegalStateException("No active timer is not available in store.")
        }
    }

    private fun dumpToDatabase() {
        activeTimerData.value?.let {
            timerDao.insert(it)
        }
    }

    private fun updateDataAfterTick() {
        val oldTimer = activeTimerData.value
        oldTimer?.let {
            if (it.state == TimerState.RUNNING) {
                if (it.duration == 0L) {
                    updateStateAndDump(TimerState.STOPPED)
                } else {
                    it.updateLiveData(activeTimerData, duration = it.duration - 1)
                }
            }
        }
    }

    private class TimerTask(timerController: TimerController) : OpenAsyncTask() {

        val reference = SoftReference<TimerController>(timerController)

        override fun performInBackground() {
            while (!isCancelled) {
                Utility.waitForOneSecond()
                // Update progress
                publishProgress()
            }
        }

        override fun performOnProgressUpdate() {
            reference.get()?.updateDataAfterTick()
        }

        override fun performOnCancelled() {
            reference.get()?.dumpToDatabase()
        }
    }
}