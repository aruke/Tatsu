package org.rionlabs.tatsu.ui.screen.main.timer

interface TimerInteractionListener {

    fun startWorkTimer()

    fun startBreakTimer()

    fun pauseTimer()

    fun resumeTimer()

    fun cancelTimer()
}