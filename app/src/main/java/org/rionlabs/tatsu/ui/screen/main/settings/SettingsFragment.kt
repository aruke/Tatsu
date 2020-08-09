package org.rionlabs.tatsu.ui.screen.main.settings

import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.WorkHoursType
import org.rionlabs.tatsu.ui.dialog.AboutDialogFragment
import org.rionlabs.tatsu.ui.dialog.feedback.FeedbackDialogFragment
import org.rionlabs.tatsu.utils.TimeUtils
import org.rionlabs.tatsu.work.AlarmScheduler
import org.rionlabs.tatsu.work.SettingsManager
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    private val settingManager: SettingsManager by inject()

    private val alarmScheduler: AlarmScheduler by inject()

    private lateinit var workDurationPref: ListPreference
    private lateinit var breakDurationPref: ListPreference
    private lateinit var workHoursPref: SwitchPreference
    private lateinit var workStartTimePref: Preference
    private lateinit var workEndTimePref: Preference
    private lateinit var silentModePref: SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        settingManager.apply {
            workDurationPref = findPreference(keyTimerWork)!!
            breakDurationPref = findPreference(keyTimerBreak)!!
            workStartTimePref = findPreference(keyWorkHoursStart)!!
            workEndTimePref = findPreference(keyWorkHoursEnd)!!
            workHoursPref =
                findPreference(getString(R.string.settings_key_work_hour_notifications))!!
            silentModePref = findPreference(getString(R.string.settings_key_silent_mode))!!

            workDurationPref.summaryProvider = durationSummaryProvider
            breakDurationPref.summaryProvider = durationSummaryProvider

            workDurationPref.apply {
                entries = entryValues.map {
                    TimeUtils.toDurationString(
                        requireContext(),
                        it.toString().toInt()
                    )
                }.toTypedArray()
            }

            breakDurationPref.apply {
                entries = entryValues.map {
                    TimeUtils.toDurationString(
                        requireContext(),
                        it.toString().toInt()
                    )
                }.toTypedArray()
            }

            workStartTimePref.summaryProvider = timeSummaryProvider
            workEndTimePref.summaryProvider = timeSummaryProvider
        }

        workHoursPref.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                var minutes: Int
                var hours: Int

                minutes = settingManager.getStartWorkHour()
                hours = minutes / 100
                minutes %= 100
                alarmScheduler.scheduleWorkHoursAlarm(WorkHoursType.START, hours, minutes)

                minutes = settingManager.getEndWorkHour()
                hours = minutes / 100
                minutes %= 100
                alarmScheduler.scheduleWorkHoursAlarm(WorkHoursType.END, hours, minutes)
            } else {
                // Cancel alarms
                alarmScheduler.cancelWorkHoursAlarm()
            }

            return@setOnPreferenceChangeListener true
        }

        workStartTimePref.setOnPreferenceClickListener {
            val startWorkMinutes = settingManager.getStartWorkHour()
            TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    Timber.d("onCreatePreferences() called with: hourOfDay = [$hourOfDay], minute = [$minute]")
                    val prefValue = hourOfDay * 100 + minute
                    setStartWorkHours(prefValue)
                    alarmScheduler.scheduleWorkHoursAlarm(WorkHoursType.START, hourOfDay, minute)
                },
                startWorkMinutes / 100,
                startWorkMinutes % 100,
                false
            ).show()
            true
        }

        workEndTimePref.setOnPreferenceClickListener {
            val endWorkMinutes = settingManager.getEndWorkHour()
            TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    Timber.d("onCreatePreferences() called with: hourOfDay = [$hourOfDay], minute = [$minute]")
                    val prefValue = hourOfDay * 100 + minute
                    setEndWorkHours(prefValue)
                    alarmScheduler.scheduleWorkHoursAlarm(WorkHoursType.END, hourOfDay, minute)
                },
                endWorkMinutes / 100,
                endWorkMinutes % 100,
                false
            ).show()
            true
        }

        findPreference<Preference>(getString(R.string.settings_key_about))?.setOnPreferenceClickListener {
            AboutDialogFragment.show(requireActivity())
            true
        }

        findPreference<Preference>(getString(R.string.settings_key_feedback))?.setOnPreferenceClickListener {
            FeedbackDialogFragment.show(requireActivity().supportFragmentManager)
            true
        }

        silentModePref.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                (context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.let {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        return@setOnPreferenceChangeListener true
                    }

                    if (!it.isNotificationPolicyAccessGranted) {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        startActivity(intent)
                        return@setOnPreferenceChangeListener false
                    } else {
                        return@setOnPreferenceChangeListener true
                    }
                } ?: run {
                    return@setOnPreferenceChangeListener false
                }
            }

            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val padding =
            requireContext().resources.getDimension(R.dimen.settings_bottom_padding).toInt()
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        recyclerView.apply {
            setPadding(paddingStart, paddingTop, paddingRight, paddingBottom + padding)
            clipToPadding = false
        }
        return recyclerView
    }

    private fun setStartWorkHours(workHoursInMinutes: Int) {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
        preferenceManager.sharedPreferences.edit {
            putInt(settingManager.keyWorkHoursStart, workHoursInMinutes)
        }
        listView?.adapter?.notifyDataSetChanged()
    }

    private fun setEndWorkHours(workHoursInMinutes: Int) {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
        preferenceManager.sharedPreferences.edit {
            putInt(settingManager.keyWorkHoursEnd, workHoursInMinutes)
        }
        listView?.adapter?.notifyDataSetChanged()
    }
}