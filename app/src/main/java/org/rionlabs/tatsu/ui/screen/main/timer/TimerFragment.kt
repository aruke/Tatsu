package org.rionlabs.tatsu.ui.screen.main.timer

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.databinding.FragmentTimerBinding
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.ui.screen.main.MainViewModel
import timber.log.Timber

class TimerFragment : Fragment(), TimerInteractionListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentTimerBinding

    private lateinit var actionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        actionButton = (requireActivity() as MainActivity).fab
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timerData.observe(this, Observer {
            it?.let { timer ->
                binding.apply {
                    Timber.d("Duration in seconds = $timer")
                    timer.apply {
                        Timber.d("Duration display = $hours:$minutes:$seconds")
                        textDigitSeconds.text = seconds.toString()
                        textDigitMinutes.text = minutes.toString()
                        textDigitHours.text = hours.toString()
                    }

                    textTimerStatus.text = timer.state.name
                    when (timer.state) {
                        TimerState.IDLE -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                startWorkTimer()
                            }
                            actionButton.setOnLongClickListener(null)
                        }
                        TimerState.RUNNING -> {
                            actionButton.setImageResource(R.drawable.ic_pause)
                            actionButton.setOnClickListener {
                                pauseTimer()
                            }
                            actionButton.setOnLongClickListener {
                                cancelTimer()
                                true
                            }
                        }
                        TimerState.PAUSED -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                resumeTimer()
                            }
                            actionButton.setOnLongClickListener {
                                cancelTimer()
                                true
                            }
                        }
                        TimerState.FINISHED -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                viewModel.startNewWorkTimer()
                            }
                            actionButton.setOnLongClickListener(null)

                            // Show timer dialogs
                            when (timer.type) {
                                TimerType.WORK ->
                                    showFinishBreakTimerFragment()
                                TimerType.BREAK ->
                                    showFinishWorkTimerFragment()
                            }
                        }
                        TimerState.CANCELLED -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                viewModel.startNewWorkTimer()
                            }
                            actionButton.setOnLongClickListener(null)
                        }
                    }
                }
            }
        })
    }

    override fun startWorkTimer() {
        viewModel.timerType = TimerType.BREAK
        viewModel.startNewWorkTimer()
    }

    override fun startBreakTimer() {
        viewModel.timerType = TimerType.BREAK
        viewModel.startNewBreakTimer()
    }

    override fun pauseTimer() {
        viewModel.pauseTimer()
    }

    override fun resumeTimer() {
        viewModel.resumeTimer()
    }

    override fun cancelTimer() {
        viewModel.cancelTimer()
    }

    private fun showFinishWorkTimerFragment() {
        AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
                .setTitle(R.string.work_timer_finish_title)
                .setMessage(R.string.work_timer_finish_text)
                .setPositiveButton(R.string.work_timer_finish_button_start_break) { _: DialogInterface, _: Int ->
                startBreakTimer()
            }
                .setNegativeButton(R.string.work_timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                    viewModel.timerType = TimerType.BREAK
                    viewModel.resetTimerData()
            }
            .show()
    }

    private fun showFinishBreakTimerFragment() {
        AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
                .setTitle(R.string.break_timer_finish_title)
                .setMessage(R.string.break_timer_finish_text)
                .setPositiveButton(R.string.break_timer_finish_button_start_work) { _: DialogInterface, _: Int ->
                    startWorkTimer()
                }
                .setNegativeButton(R.string.break_timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    viewModel.timerType = TimerType.WORK
                    viewModel.resetTimerData()
                }
                .show()
    }
}
