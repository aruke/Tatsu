package org.rionlabs.tatsu.ui.screen.main.settings

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.ui.screen.main.MainViewModel
import org.rionlabs.tatsu.work.SettingsManager

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
            findPreference<Preference>(keyTimerWork).summaryProvider = durationSummaryProvider
            findPreference<Preference>(keyTimerBreak).summaryProvider = durationSummaryProvider
            findPreference<Preference>(keyWorkHoursStart).summaryProvider = timeSummaryProvider
            findPreference<Preference>(keyWorkHoursEnd).summaryProvider = timeSummaryProvider
        }
    }
}
