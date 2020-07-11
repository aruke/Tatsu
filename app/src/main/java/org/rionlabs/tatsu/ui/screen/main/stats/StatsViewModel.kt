package org.rionlabs.tatsu.ui.screen.main.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.rionlabs.tatsu.data.dao.TimerDao
import org.rionlabs.tatsu.data.model.Timer

class StatsViewModel(private val timerDao: TimerDao) : ViewModel() {

    val timerListData: LiveData<List<Timer>>
        get() = timerDao.getAll()
}