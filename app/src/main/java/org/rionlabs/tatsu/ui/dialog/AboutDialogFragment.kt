package org.rionlabs.tatsu.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import org.rionlabs.tatsu.BuildConfig
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.DialogAboutBinding

class AboutDialogFragment : FullScreenDialogFragment() {

    lateinit var binding: DialogAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAboutBinding.inflate(inflater, container, false).apply {
            toolbar.setTitle(R.string.settings_about_title)
            toolbar.setNavigationOnClickListener {
                dismissAllowingStateLoss()
            }
            versionName = BuildConfig.VERSION_NAME
        }
        return binding.root
    }

    companion object {

        private const val TAG = "AboutDialogFragment"

        fun show(activity: FragmentActivity) {
            AboutDialogFragment().show(activity.supportFragmentManager, TAG)
        }
    }
}