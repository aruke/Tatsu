package org.rionlabs.tatsu.work

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.utils.TimeUtils

class SettingsManager constructor(private val context: Context) {

    val keyTimerWork: String
    val keyTimerBreak: String
    val keyWorkHoursNotifications: String
    val keyWorkHoursStart: String
    val keyWorkHoursEnd: String

    private val keySilentMode: String

    var silentMode: Boolean = false
        get() = sharedPreference.getBoolean(keySilentMode, silentModeDefaultValue)
        set(value) {
            field = value
            sharedPreference.edit(true) {
                putBoolean(keySilentMode, value)
            }
        }

    val workHourNotificationsEnabled: Boolean
        get() = sharedPreference.getBoolean(keyWorkHoursNotifications, workHoursNotificationsDefaultValue)

    private val timerWorkDefaultValue: String
    private val timerBreakDefaultValue: String
    private val workHoursNotificationsDefaultValue: Boolean
    private val workHoursStartDefaultValue: Int
    private val workHoursEndDefaultValue: Int
    private val silentModeDefaultValue: Boolean

    private val sharedPreference: SharedPreferences

    init {
        val appContext = context.applicationContext
        appContext.apply {
            keyTimerWork = getString(R.string.settings_key_timer_work)
            keyTimerBreak = getString(R.string.settings_key_timer_break)
            keyWorkHoursNotifications = getString(R.string.settings_key_work_hour_notifications)
            keyWorkHoursStart = getString(R.string.settings_key_work_hour_start)
            keyWorkHoursEnd = getString(R.string.settings_key_work_hour_end)
            keySilentMode = getString(R.string.settings_key_silent_mode)

            timerWorkDefaultValue = getString(R.string.settings_default_value_timer_work)
            timerBreakDefaultValue = getString(R.string.settings_default_value_timer_break)
            workHoursNotificationsDefaultValue = getString(R.string.settings_default_value_work_hour_notifications) == true.toString()
            workHoursStartDefaultValue =
                Integer.parseInt(getString(R.string.settings_default_value_work_hour_start))
            workHoursEndDefaultValue =
                Integer.parseInt(getString(R.string.settings_default_value_work_hour_end))
            silentModeDefaultValue =
                getString(R.string.settings_default_value_silent_mode) == true.toString()

            sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        }
    }

    val timeSummaryProvider = Preference.SummaryProvider<Preference> { preference ->
        var time = 0
        preference?.let {
            time = when (it.key) {
                keyWorkHoursStart -> getStartWorkHour()
                keyWorkHoursEnd -> getEndWorkHour()
                else ->
                    throw IllegalStateException("Preference must be managed by SettingManager")
            }
        }

        TimeUtils.toTimeString(context, time)
    }

    val durationSummaryProvider = Preference.SummaryProvider<Preference> { preference ->
        var minutes = 0
        preference?.let {
            minutes = when (it.key) {
                keyTimerWork -> getWorkTimerInMinutes()
                keyTimerBreak -> getBreakTimerInMinutes()
                else ->
                    throw IllegalStateException("Preference must be managed by SettingManager")
            }
        }
        TimeUtils.toDurationString(context, minutes)
    }

    fun getWorkTimerInMinutes(): Int {
        return sharedPreference.getString(keyTimerWork, timerWorkDefaultValue)?.toInt()
            ?: timerWorkDefaultValue.toInt()
    }

    fun getBreakTimerInMinutes(): Int {
        return sharedPreference.getString(keyTimerBreak, timerBreakDefaultValue)?.toInt()
            ?: timerBreakDefaultValue.toInt()
    }

    fun getStartWorkHour(): Int {
        return sharedPreference.getInt(keyWorkHoursStart, workHoursStartDefaultValue)
    }

    fun getEndWorkHour(): Int {
        return sharedPreference.getInt(keyWorkHoursEnd, workHoursEndDefaultValue)
    }
}