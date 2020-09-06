package org.rionlabs.tatsu.ui.screen.main.timer

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.databinding.FragmentTimerBinding
import org.rionlabs.tatsu.work.service.TimerService
import timber.log.Timber

class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding

    private lateinit var blinkAnimation: Animation

    private val viewModel: TimerViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false).apply {
            // Need to set to make FAB icon larger
            actionButton.scaleType = ImageView.ScaleType.CENTER
        }
        blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.blink)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewStateData.observe(viewLifecycleOwner, Observer {
            val viewState = it ?: return@Observer
            Timber.d("ViewState changed to $viewState")

            // Set timer on screen
            binding.digitalTimer.setTimer(viewState.timer)

            // Clear listeners on actionButton
            binding.actionButton.apply {
                setOnClickListener(null)
                setOnLongClickListener(null)
            }

            if (viewState.chipBlinking) {
                binding.timerTypeChip.startAnimation(blinkAnimation)
            } else {
                binding.timerTypeChip.clearAnimation()
            }

            binding.timerTypeChip.setText(viewState.chipTextResId)

            when (viewState.timer.state) {
                TimerState.IDLE -> {
                    binding.actionButton.setImageResource(R.drawable.ic_play)
                    binding.actionButton.setOnClickListener {
                        if (viewState.timer.isPaused) {
                            TimerService.resumeTimer(requireContext())
                        } else {
                            if (viewState.timer.type == TimerType.WORK)
                                TimerService.startWorkTimer(requireContext())
                            else
                                TimerService.startBreakTimer(requireContext())
                        }
                    }
                }
                TimerState.RUNNING -> {
                    binding.actionButton.setImageResource(R.drawable.ic_pause)
                    binding.actionButton.setOnClickListener {
                        TimerService.pauseTimer(requireContext())
                    }
                }
                TimerState.FINISHED -> {

                }
            }

            if (viewState.workFinishedDialogShown) {
                showFinishWorkTimerFragment()
            }

            if (viewState.breakFinishedDialogShown) {
                showFinishBreakTimerFragment()
            }
        })
    }

    private fun showFinishWorkTimerFragment() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.work_timer_finish_title)
            .setMessage(R.string.work_timer_finish_text)
            .setPositiveButton(R.string.work_timer_finish_button_start_break) { _: DialogInterface, _: Int ->
                TimerService.startBreakTimer(requireContext())
            }
            .setNegativeButton(R.string.work_timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                // FixMe viewModel.requestState(WORK_TIMER_IDLE)
            }
            .show()
    }

    private fun showFinishBreakTimerFragment() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.break_timer_finish_title)
            .setMessage(R.string.break_timer_finish_text)
            .setPositiveButton(R.string.break_timer_finish_button_start_work) { _: DialogInterface, _: Int ->
                TimerService.startWorkTimer(requireContext())
            }
            .setNegativeButton(R.string.break_timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                // FixMe viewModel.requestState(WORK_TIMER_IDLE)
            }
            .show()
    }
}