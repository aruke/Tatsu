package org.rionlabs.tatsu.ui.screen.main.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.rionlabs.tatsu.data.dao.TimerDao
import timber.log.Timber

class StatsViewModel(timerDao: TimerDao) : ViewModel() {

    private val _viewStateData = MutableLiveData<StatsViewState>()
    val viewStateData: LiveData<StatsViewState>
        get() = _viewStateData

    init {
        val statsMeta = timerDao.getStatsMeta()
        Timber.d("StatsMeta: $statsMeta")
        _viewStateData.postValue(
            StatsViewState(
                dataAvailable = statsMeta.daySessionCount == 0 && statsMeta.weekSessionCount == 0,
                sessionsToday = statsMeta.daySessionCount,
                minutesToday = statsMeta.dayDurationSecs / 60,
                sessionsThisWeek = statsMeta.weekSessionCount,
                minutesThisWeek = statsMeta.weekDurationSecs / 60
            )
        )
    }
}