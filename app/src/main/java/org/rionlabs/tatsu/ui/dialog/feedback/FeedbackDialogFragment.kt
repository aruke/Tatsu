package org.rionlabs.tatsu.ui.dialog.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.DialogFeedbackBinding
import org.rionlabs.tatsu.ui.dialog.FullScreenDialogFragment
import org.rionlabs.tatsu.ui.dialog.feedback.FeedbackScreen.ViewEffect.*
import timber.log.Timber

class FeedbackDialogFragment : FullScreenDialogFragment() {

    private lateinit var binding: DialogFeedbackBinding

    private val viewModel: FeedbackViewModel by viewModel()

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
        viewModel.viewStateData.observe(viewLifecycleOwner, Observer {
            binding.viewState = it
        })

        viewModel.viewEffectData.observe(viewLifecycleOwner, Observer {
            Timber.d("viewEffectData.observe: $it")
            when (it) {
                is Success -> {
                }
                is NetworkError -> {
                }
                is ReCaptchaError -> {
                }
                is ServerError -> {
                }
            }
        })

        binding.apply {
            feelingGroup.setOnCheckedChangeListener { _, id ->
                val emotion = when (id) {
                    R.id.sentimentHappy -> Emotion.HAPPY
                    R.id.sentimentNeutral -> Emotion.NEUTRAL
                    R.id.sentimentSad -> Emotion.SAD
                    else -> throw IllegalArgumentException("Unknown RadioButton ID")
                }
                viewModel.setEmotion(emotion)
            }
            messageInput.addTextChangedListener {
                val text = it?.toString()?.trim() ?: return@addTextChangedListener
                viewModel.updateMessage(text)
            }
            sendButton.setOnClickListener {
                viewModel.trySendingFeedback()
            }
        }
    }

    companion object {

        private const val TAG = "FeedbackDialogFragment"

        fun show(fragmentManager: FragmentManager) {
            FeedbackDialogFragment().show(fragmentManager, TAG)
        }
    }
}