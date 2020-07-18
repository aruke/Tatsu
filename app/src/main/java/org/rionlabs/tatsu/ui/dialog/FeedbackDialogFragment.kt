package org.rionlabs.tatsu.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.DialogFeedbackBinding

class FeedbackDialogFragment : FullScreenDialogFragment() {

    lateinit var binding: DialogFeedbackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFeedbackBinding.inflate(inflater, container, false).apply {
            toolbar.setTitle(R.string.settings_feedback_title)
            toolbar.setNavigationOnClickListener {
                dismissAllowingStateLoss()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendButton.isEnabled = false
    }

    companion object {

        private const val TAG = "FeedbackDialogFragment"

        fun show(activity: FragmentActivity) {
            FeedbackDialogFragment().show(activity.supportFragmentManager, TAG)
        }
    }
}