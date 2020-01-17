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
import org.rionlabs.tatsu.databinding.FragmentTimerBinding
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.ui.screen.main.MainViewModel
import org.rionlabs.tatsu.ui.screen.main.timer.TimerScreenState.*
import timber.log.Timber

class TimerFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentTimerBinding

    private lateinit var actionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        actionButton = (requireActivity() as MainActivity).fab
    }

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
            actionButton.apply {
                setOnClickListener(null)
                setOnLongClickListener(null)
            }

            when (screenState) {
                WORK_TIMER_IDLE -> {
                    actionButton.setImageResource(R.drawable.ic_play)
                    actionButton.setOnClickListener {
                        viewModel.requestState(WORK_TIMER_RUNNING)
                    }
                }
                WORK_TIMER_RUNNING -> {
                    actionButton.setImageResource(R.drawable.ic_pause)
                    actionButton.setOnClickListener {
                        viewModel.requestState(WORK_TIMER_PAUSED)
                    }
                }
                WORK_TIMER_PAUSED -> {
                    actionButton.setImageResource(R.drawable.ic_play)
                    actionButton.setOnClickListener {
                        viewModel.requestState(WORK_TIMER_RUNNING)
                    }
                }
                WORK_TIMER_FINISHED -> {
                    showFinishWorkTimerFragment()
                }
                BREAK_TIMER_RUNNING -> {
                    actionButton.setImageResource(R.drawable.ic_pause)
                    actionButton.setOnClickListener {
                        viewModel.requestState(BREAK_TIMER_PAUSED)
                    }
                }
                BREAK_TIMER_PAUSED -> {
                    actionButton.setImageResource(R.drawable.ic_play)
                    actionButton.setOnClickListener {
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
        AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
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
        AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
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
