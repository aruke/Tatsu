package org.rionlabs.tatsu.ui.screen.main.settings

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProviders
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.ui.dialog.FullScreenDialogFragment
import org.rionlabs.tatsu.ui.screen.main.MainViewModel
import org.rionlabs.tatsu.utils.TimeUtils
import org.rionlabs.tatsu.work.SettingsManager
import org.rionlabs.tatsu.work.WorkTimeAlarmReceiver
import org.rionlabs.tatsu.work.WorkTimeAlarmReceiver.Companion.ACTION_SHOW_END_WORK_NOTIFICATION
import org.rionlabs.tatsu.work.WorkTimeAlarmReceiver.Companion.ACTION_SHOW_START_WORK_NOTIFICATION
import timber.log.Timber
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: MainViewModel

    private lateinit var settingManager: SettingsManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingManager = SettingsManager(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        settingManager.apply {
            findPreference<Preference>(keyTimerWork)?.summaryProvider = durationSummaryProvider
            findPreference<Preference>(keyTimerBreak)?.summaryProvider = durationSummaryProvider

            (findPreference<Preference>(keyTimerWork) as ListPreference).apply {
                entries = entryValues.map {
                    TimeUtils.toDurationString(
                        requireContext(),
                        it.toString().toInt()
                    )
                }.toTypedArray()
            }
            (findPreference<Preference>(keyTimerBreak) as ListPreference).apply {
                entries = entryValues.map {
                    TimeUtils.toDurationString(
                        requireContext(),
                        it.toString().toInt()
                    )
                }.toTypedArray()
            }

            findPreference<Preference>(keyWorkHoursStart)?.summaryProvider = timeSummaryProvider
            findPreference<Preference>(keyWorkHoursEnd)?.summaryProvider = timeSummaryProvider
        }

        findPreference<Preference>(settingManager.keyWorkHoursStart)?.setOnPreferenceClickListener {
            val minutes = settingManager.getStartWorkHour()
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val prefValue = hourOfDay * 100 + minute
                setStartWorkHours(prefValue)
                scheduleAlarm(ACTION_SHOW_START_WORK_NOTIFICATION, hourOfDay, minute)
            }, minutes / 100, minutes % 100, false).show()
            true
        }

        findPreference<Preference>(settingManager.keyWorkHoursEnd)?.setOnPreferenceClickListener {
            val minutes = settingManager.getEndWorkHour()
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val prefValue = hourOfDay * 100 + minute
                setEndWorkHours(prefValue)
                scheduleAlarm(ACTION_SHOW_END_WORK_NOTIFICATION, hourOfDay, minute)
            }, minutes / 100, minutes % 100, false).show()
            true
        }

        findPreference<Preference>(getString(R.string.settings_key_about))?.setOnPreferenceClickListener {
            FullScreenDialogFragment.show(requireActivity(), R.string.settings_about_title, R.layout.layout_about)
            true
        }

        findPreference<Preference>(getString(R.string.settings_key_feedback))?.setOnPreferenceClickListener {
            FullScreenDialogFragment.show(requireActivity(), R.string.settings_feedback_title, R.layout.layout_feedback)
            true
        }

        findPreference<SwitchPreference>(getString(R.string.settings_key_silent_mode))
                ?.setOnPreferenceChangeListener { _, newValue ->
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

    override fun onCreateRecyclerView(inflater: LayoutInflater?, parent: ViewGroup?, savedInstanceState: Bundle?): RecyclerView {
        val padding = requireContext().resources.getDimension(R.dimen.main_screen_bottom_padding).toInt()
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

    private fun scheduleAlarm(action: String, hours: Int, minutes: Int) {

        val alarmManager = context?.let {
            it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        } ?: run {
            Timber.e("AlarmManager Not Available")
            return
        }

        val intent = Intent(context, WorkTimeAlarmReceiver::class.java)
        if (action in arrayOf(ACTION_SHOW_START_WORK_NOTIFICATION, ACTION_SHOW_END_WORK_NOTIFICATION)) {
            intent.action = action
        } else {
            throw IllegalStateException("Invalid Action for WorkTimeAlarmReceiver")
        }

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        val millis = Calendar.getInstance(TimeZone.getDefault()).also {
            it.set(Calendar.HOUR_OF_DAY, hours)
            it.set(Calendar.MINUTE, minutes)
        }.timeInMillis

        val interval = 86400000L

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, interval, pendingIntent)
    }
}
