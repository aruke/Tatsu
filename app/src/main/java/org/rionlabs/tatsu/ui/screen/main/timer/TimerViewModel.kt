package org.rionlabs.tatsu.ui.screen.main.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.rionlabs.tatsu.data.model.Timer

class TimerViewModel(val app: Application) : AndroidViewModel(app) {

    private val mTimerData: MutableLiveData<Timer> = MutableLiveData()
    val timerData: LiveData<Timer>
        get() = mTimerData

    fun getDuration(): LiveData<Long> {
        return MutableLiveData<Long>()
    }

    override fun onCleared() {

    }
}
