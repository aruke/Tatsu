package org.rionlabs.tatsu.ui.screen.main.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.rionlabs.tatsu.TatsuApp
import timber.log.Timber

class TimerViewModel(val app: Application) : AndroidViewModel(app) {

    private val timerController = (app as TatsuApp).timerController

    fun startTimer() {
        timerController.startNewTimer(20).observeForever {
            it?.let { timer ->
                Timber.d("OnChanged called with NonNull Timer value")
                Timber.d("$timer")
            }
        }
    }

    fun getDuration(): LiveData<Long> {
        return MutableLiveData<Long>()
    }

    override fun onCleared() {

    }
}
