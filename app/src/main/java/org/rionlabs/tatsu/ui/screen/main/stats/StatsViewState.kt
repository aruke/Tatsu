package org.rionlabs.tatsu.ui.screen.main.stats

data class StatsViewState(
    val dataAvailable: Boolean,
    val sessionsToday: Int,
    val minutesToday: Int,
    val sessionsThisWeek: Int,
    val minutesThisWeek: Int
) {

    val metaDataAvailable: Boolean = sessionsToday != 0 || sessionsThisWeek != 0

    val statsAvailable: Boolean = false
}