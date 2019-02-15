package org.rionlabs.tatsu.ui.screen.main.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.rionlabs.tatsu.databinding.FragmentTimerBinding
import org.rionlabs.tatsu.ui.screen.main.BaseMainFragment
import timber.log.Timber

class TimerFragment : BaseMainFragment() {

    companion object {
        fun newInstance() = TimerFragment()
    }

    private lateinit var viewModel: TimerViewModel
    private lateinit var binding: FragmentTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.getDuration().observe(this, Observer {
            binding.viewTimerDigits.duration = it
        })

        viewModel.timerData.observe(this, Observer {
            it?.let { timer ->
                Timber.d("OnChanged called with NonNull Timer value")
                binding.textTimerStatus.text = timer.state.toString()
            }
        })
    }
}
