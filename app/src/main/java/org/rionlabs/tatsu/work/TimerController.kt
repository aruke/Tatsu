package org.rionlabs.tatsu.work

import android.app.Application
import androidx.lifecycle.*
import org.rionlabs.tatsu.data.AppDatabase
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.utils.*
import timber.log.Timber
import java.lang.ref.SoftReference

/**
 * Act as a controller and store for [Timer] objects. Do not instantiate except in application.
 */
class TimerController(app: Application) : LifecycleObserver {

    private val activeTimerData = MutableLiveData<Timer>()

    private var timerDao = AppDatabase.getInstance(app.applicationContext).timerDao()

    private var timerTask: TimerTask? = null

    /**
     * Initializes the store with values from database.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Timber.v("onCreate() called")
        val timerList = timerDao.getActive()
        Timber.d("Received ${timerList.size} entries from database.")
        if (timerList.isEmpty()) {
            Timber.v("No active timers found.")
        } else {
            val firstTimer = timerList.first()
            activeTimerData.value = firstTimer
            Timber.v("Found active timers. Setting LiveData for $firstTimer")
            // TODO Discard other timers if there are more than one
        }
    }

    /**
     * Returns true if there is any [TimerState.RUNNING] or [TimerState.PAUSED] timer in store.
     */
    fun isActiveTimerAvailable(): Boolean {
        Timber.v("ActiveTimerData.value ${activeTimerData.value?.state.toString()}")
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

    fun startNewTimer(timerType: TimerType, durationInSeconds: Long): LiveData<Timer> {
        if (isActiveTimerAvailable()) {
            throw IllegalStateException("Already running or paused timer detected.")
        } else {
            Timber.v("Starting new timer...")
            val timerId = timerDao.insert(Timer(TimeUtils.currentTimeEpoch(), durationInSeconds, timerType))
            val timer = timerDao.getWith(timerId).copy(state = TimerState.RUNNING)
            activeTimerData.value = timer
            return activeTimerData.also {
                timerTask = TimerTask(this).also {
                    it.execute()
                }
            }
        }
    }

    fun pauseTimer() {
        Timber.v("Pausing timer...")
        updateStateAndDump(TimerState.PAUSED)
        timerTask?.cancel(true)
    }

    fun resumeTimer() {
        Timber.v("Resuming timer...")
        updateStateAndDump(TimerState.RUNNING)
        timerTask = TimerTask(this).also {
            it.execute()
        }
    }

    fun cancelTimer() {
        Timber.v("Cancelling timer...")
        updateStateAndDump(TimerState.CANCELLED)
    }

    /**
     * Clears the store and dumps data into database.
     * This method will be rarely called, so never rely on it.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Timber.v("onDestroy() called")
        dumpToDatabase()
        activeTimerData.value = null
        timerTask?.cancel(true)
    }

    /**
     * Updates [activeTimerData] and dumps the value to database.
     */
    private fun updateStateAndDump(state: TimerState) {
        val oldTimer = activeTimerData.value
        oldTimer?.let {
            it.updateLiveData(activeTimerData, state = state)
            dumpToDatabase()
        } ?: run {
            throw IllegalStateException("No active timer is not available in store.")
        }
    }

    /**
     * Dumps [activeTimerData] value to database.
     */
    private fun dumpToDatabase() {
        activeTimerData.value?.let {
            timerDao.insert(it)
        }
    }

    /**
     * Updates the duration and status by decrementing [Timer.duration] value by 1.
     */
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

            // Cancel timerTask for this timer, if it is not to be ticked
            if (it.state == TimerState.STOPPED || it.state == TimerState.CANCELLED) {
                timerTask?.cancel(true)
                timerTask = null
            }
        }
    }

    private class TimerTask(timerController: TimerController) : OpenAsyncTask() {

        val reference = SoftReference<TimerController>(timerController)

        override fun performInBackground() {
            while (!isCancelled) {
                Timber.v("Tick")
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