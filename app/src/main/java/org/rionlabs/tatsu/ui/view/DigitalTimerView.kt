package org.rionlabs.tatsu.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.databinding.ViewDigitalTimerBinding
import timber.log.Timber

class DigitalTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewDigitalTimerBinding.inflate(LayoutInflater.from(context), this, true)

    fun setTimer(timer: Timer) {
        binding.apply {
            Timber.d("Duration in seconds = $timer")
            timer.apply {
                Timber.d("Duration display = $hours:$minutes:$seconds")

                if (hours <= 0) {
                    groupHour.visibility = GONE
                }
                textDigitHours.text = hours.toString()

                if (minutes <= 0) {
                    groupMinutes.visibility = GONE
                }

                textDigitMinutes.text = minutes.toString()

                textDigitSeconds.text = seconds.toString()
            }
        }
        binding.executePendingBindings()
    }
}