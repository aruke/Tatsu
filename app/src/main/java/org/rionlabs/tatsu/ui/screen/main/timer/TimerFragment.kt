package org.rionlabs.tatsu.ui.screen.main.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class TimerFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentTimerBinding

    private lateinit var actionButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        actionButton = (requireActivity() as MainActivity).fab
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTimerState().observe(this, Observer {
            it?.let { state ->
                binding.apply {
                    textTimerStatus.text = state.name
                    when (state) {
                        TimerState.IDLE -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                viewModel.startNewTimer()
                            }
                            actionButton.setOnLongClickListener(null)
                        }
                        TimerState.RUNNING -> {
                            actionButton.setImageResource(R.drawable.ic_pause)
                            actionButton.setOnClickListener {
                                viewModel.pauseTimer()
                            }
                            actionButton.setOnLongClickListener {
                                viewModel.cancelTimer()
                                true
                            }
                        }
                        TimerState.PAUSED -> {
                            actionButton.setImageResource(R.drawable.ic_play)
                            actionButton.setOnClickListener {
                                viewModel.resumeTimer()
                            }
                            actionButton.setOnLongClickListener {
                                viewModel.cancelTimer()
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

        viewModel.getDuration().observe(this, Observer {
            it?.let { durationInSeconds ->
                binding.apply {
                    Timber.d("Duration in seconds = $durationInSeconds")

                    val hours = durationInSeconds / 3600
                    val minutes = (durationInSeconds % 3600) / 60
                    val seconds = (durationInSeconds % 3600) % 60

                    Timber.d("Duration display = $hours:$minutes:$seconds")

                    textDigitSeconds.text = seconds.toString()
                    textDigitMinutes.text = minutes.toString()
                    textDigitHours.text = hours.toString()
                }
            }
        })
    }
}
