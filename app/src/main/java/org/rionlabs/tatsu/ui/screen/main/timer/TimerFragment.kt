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
        showFinishTimerFragment()
        viewModel.stateData.observe(this, Observer {
            it?.let { state ->
                binding.apply {
                    textTimerStatus.text = state.name
                    when (state) {
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
                        TimerState.STOPPED -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                viewModel.startNewTimer()
                            }
                            actionButton.setOnLongClickListener(null)
                        }
                        TimerState.CANCELLED -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                viewModel.startNewTimer()
                            }
                            actionButton.setOnLongClickListener(null)
                        }
                    }
                }
            }
        })

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
                }
            }
        })
    }

    override fun startWorkTimer() {
        viewModel.startNewTimer()
    }

    override fun startBreakTimer() {
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

    private fun showFinishTimerFragment() {
        AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
            .setTitle(R.string.timer_finish_title)
            .setMessage(R.string.timer_finish_text)
            .setPositiveButton(R.string.timer_finish_button_start_break) { _: DialogInterface, _: Int ->
                startBreakTimer()

            }
            .setNegativeButton(R.string.timer_finish_button_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }
}
