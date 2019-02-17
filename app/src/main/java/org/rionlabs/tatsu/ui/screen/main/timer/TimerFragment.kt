package org.rionlabs.tatsu.ui.screen.main.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.databinding.FragmentTimerBinding
import org.rionlabs.tatsu.ui.screen.main.MainViewModel
import timber.log.Timber

class TimerFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTimerState().observe(this, Observer {
            it?.let { state ->
                binding.apply {
                    textTimerStatus.text = state.name
                    when (state) {
                        TimerState.IDLE -> {
                            buttonTimer.isEnabled = true
                            buttonTimer.text = "Start Timer"
                            buttonTimer.setOnClickListener {
                                viewModel.startNewTimer()
                            }
                            buttonTimer.setOnLongClickListener(null)
                        }
                        TimerState.RUNNING -> {
                            buttonTimer.isEnabled = true
                            buttonTimer.text = "Pause Timer"
                            buttonTimer.setOnClickListener {
                                viewModel.pauseTimer()
                            }
                            buttonTimer.setOnLongClickListener {
                                viewModel.cancelTimer()
                                true
                            }
                        }
                        TimerState.PAUSED -> {
                            buttonTimer.isEnabled = true
                            buttonTimer.text = "Resume Timer"
                            buttonTimer.setOnClickListener {
                                viewModel.resumeTimer()
                            }
                            buttonTimer.setOnLongClickListener {
                                viewModel.cancelTimer()
                                true
                            }
                        }
                        TimerState.STOPPED -> {
                            buttonTimer.isEnabled = true
                            buttonTimer.text = "Restart Timer"
                            buttonTimer.setOnClickListener {
                                viewModel.startNewTimer()
                            }
                            buttonTimer.setOnLongClickListener(null)
                        }
                        TimerState.CANCELLED -> {
                            buttonTimer.isEnabled = true
                            buttonTimer.text = "Start Timer"
                            buttonTimer.setOnClickListener {
                                viewModel.startNewTimer()
                            }
                            buttonTimer.setOnLongClickListener(null)
                        }
                    }
                }
            }
        })

        viewModel.getDuration().observe(this, Observer {
            it?.let { durationInSeconds ->
                binding.apply {
                    Timber.d("Duration in seconds = $durationInSeconds")
                    textTimerDivider.visibility = if (durationInSeconds % 2L == 0L) {
                        Timber.d("Divider visibility : visible")
                        View.VISIBLE
                    } else {
                        Timber.d("Divider visibility : invisible")
                        View.INVISIBLE
                    }

                    val minutes = durationInSeconds / 60
                    val hours = durationInSeconds / 3600

                    Timber.d("Duration display = $hours:$minutes")

                    textDigitMinutes.text = minutes.toString()
                    textDigitHours.text = hours.toString()
                }
            }
        })
    }
}
