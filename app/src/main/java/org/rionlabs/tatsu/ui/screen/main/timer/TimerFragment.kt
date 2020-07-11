package org.rionlabs.tatsu.ui.screen.main.timer

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.FragmentTimerBinding
import org.rionlabs.tatsu.ui.screen.main.timer.TimerScreenState.*
import timber.log.Timber

class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding

    private val viewModel: TimerViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timerData.observe(viewLifecycleOwner, Observer {
            it?.let { timer ->
                binding.apply {
                    digitalTimer.setTimer(timer)
                    analogTimer.setTimer(timer)
                }
            }
        })

        viewModel.timerScreenState.observe(viewLifecycleOwner, Observer {
            val screenState = it ?: return@Observer
            Timber.d("ScreenState changed to $screenState")

            // Clear listeners on actionButton
            binding.actionButton.apply {
                setOnClickListener(null)
                setOnLongClickListener(null)
            }

            when (screenState) {
                WORK_TIMER_IDLE -> {
                    binding.actionButton.setImageResource(R.drawable.ic_play)
                    binding.actionButton.setOnClickListener {
                        viewModel.requestState(WORK_TIMER_RUNNING)
                    }
                }
                WORK_TIMER_RUNNING -> {
                    binding.actionButton.setImageResource(R.drawable.ic_pause)
                    binding.actionButton.setOnClickListener {
                        viewModel.requestState(WORK_TIMER_PAUSED)
                    }
                    binding.timerTypeChip.setText(R.string.timer_type_work)
                }
                WORK_TIMER_PAUSED -> {
                    binding.actionButton.setImageResource(R.drawable.ic_play)
                    binding.actionButton.setOnClickListener {
                        viewModel.requestState(WORK_TIMER_RUNNING)
                    }
                }
                WORK_TIMER_FINISHED -> {
                    showFinishWorkTimerFragment()
                }
                BREAK_TIMER_RUNNING -> {
                    binding.actionButton.setImageResource(R.drawable.ic_pause)
                    binding.actionButton.setOnClickListener {
                        viewModel.requestState(BREAK_TIMER_PAUSED)
                    }
                    binding.timerTypeChip.setText(R.string.timer_type_break)
                }
                BREAK_TIMER_PAUSED -> {
                    binding.actionButton.setImageResource(R.drawable.ic_play)
                    binding.actionButton.setOnClickListener {
                        viewModel.requestState(BREAK_TIMER_RUNNING)
                    }
                }
                BREAK_TIMER_FINISHED -> {
                    showFinishBreakTimerFragment()
                }
            }
        })
    }

    private fun showFinishWorkTimerFragment() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.work_timer_finish_title)
            .setMessage(R.string.work_timer_finish_text)
            .setPositiveButton(R.string.work_timer_finish_button_start_break) { _: DialogInterface, _: Int ->
                viewModel.requestState(BREAK_TIMER_RUNNING)
            }
            .setNegativeButton(R.string.work_timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                viewModel.requestState(WORK_TIMER_IDLE)
            }
            .show()
    }

    private fun showFinishBreakTimerFragment() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.break_timer_finish_title)
            .setMessage(R.string.break_timer_finish_text)
            .setPositiveButton(R.string.break_timer_finish_button_start_work) { _: DialogInterface, _: Int ->
                viewModel.requestState(WORK_TIMER_RUNNING)
            }
            .setNegativeButton(R.string.break_timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                viewModel.requestState(WORK_TIMER_IDLE)
            }
            .show()
    }
}