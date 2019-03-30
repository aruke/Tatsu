package org.rionlabs.tatsu.work

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import org.rionlabs.tatsu.R
import java.util.*

class SettingsManager constructor(private val context: Context) {

    val keyTimerWork: String
    val keyTimerBreak: String
    val keyWorkHoursStart: String
    val keyWorkHoursEnd: String

    private val timerWorkDefaultValue: Int
    private val timerBreakDefaultValue: Int
    private val workHoursStartDefaultValue: Int
    private val workHoursEndDefaultValue: Int

    private val sharedPreference: SharedPreferences

    init {
        val appContext = context.applicationContext
        appContext.apply {
            keyTimerWork = getString(R.string.settings_key_timer_work)
            keyTimerBreak = getString(R.string.settings_key_timer_break)
            keyWorkHoursStart = getString(R.string.settings_key_work_hour_start)
            keyWorkHoursEnd = getString(R.string.settings_key_work_hour_end)

            timerWorkDefaultValue = Integer.parseInt(getString(R.string.settings_default_value_timer_work))
            timerBreakDefaultValue = Integer.parseInt(getString(R.string.settings_default_value_timer_break))
            workHoursStartDefaultValue = Integer.parseInt(getString(R.string.settings_default_value_work_hour_start))
            workHoursEndDefaultValue = Integer.parseInt(getString(R.string.settings_default_value_work_hour_end))

            sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        }
    }

    val timeSummaryProvider = object : Preference.SummaryProvider<Preference> {
        override fun provideSummary(preference: Preference?): CharSequence {
            var time = 0
            preference?.let {
                time = when (it.key) {
                    keyWorkHoursStart -> getStartWorkHour()
                    keyWorkHoursEnd -> getEndWorkHour()
                    else ->
                        throw IllegalStateException("Preference must be managed by SettingManager")
                }
            }

            val minutes = time % 100
            var hours = (time / 100)
            if (hours < 12) {
                if (hours == 0) {
                    hours = 12
                }
                return context.getString(R.string.time_format_am, hours, minutes)
            } else {
                return context.getString(R.string.time_format_pm, (hours - 12), minutes)
            }
        }
    }

    val durationSummaryProvider = object : Preference.SummaryProvider<Preference> {
        override fun provideSummary(preference: Preference?): CharSequence {
            var minutes = 0
            preference?.let {
                minutes = when (it.key) {
                    keyTimerWork -> getWorkTimerInMinutes()
                    keyTimerBreak -> getBreakTimerInMinutes()
                    else ->
                        throw IllegalStateException("Preference must be managed by SettingManager")
                }
            }

            val hours = minutes / 60
            minutes %= 60

            var hourString = context.resources.getQuantityString(R.plurals.duration_format_hours, hours, hours)
            var minuteString = context.resources.getQuantityString(R.plurals.duration_format_minutes, minutes, minutes)

            // English Grammar doesn't honor `zero` plural. See https://stackoverflow.com/a/17261327
            if (Locale.getDefault().language == Locale.ENGLISH.language) {
                if (hours == 0)
                    hourString = ""
                if (minutes == 0)
                    minuteString = ""
            }

            return "$hourString $minuteString"
        }
    }

    fun getWorkTimerInMinutes(): Int {
        return sharedPreference.getInt(keyTimerWork, timerWorkDefaultValue)
    }

    fun getBreakTimerInMinutes(): Int {
        return sharedPreference.getInt(keyTimerBreak, timerBreakDefaultValue)
    }

    fun getStartWorkHour(): Int {
        return sharedPreference.getInt(keyWorkHoursStart, workHoursStartDefaultValue)
    }

    fun getEndWorkHour(): Int {
        return sharedPreference.getInt(keyWorkHoursEnd, workHoursEndDefaultValue)
    }
}