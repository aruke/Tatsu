package org.rionlabs.tatsu.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.databinding.ViewDigitalTimerBinding
import timber.log.Timber
import java.util.*

class DigitalTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewDigitalTimerBinding.inflate(LayoutInflater.from(context), this, true)

    private val zeroPaddedFormat: String = "%02d"
    private val normalFormat: String = "%d"

    fun setTimer(timer: Timer) {
        binding.apply {
            Timber.d("Duration in seconds = $timer")
            timer.apply {
                Timber.d("Duration display = $hours:$minutes:$seconds")
                var minuteFormat = zeroPaddedFormat
                var secondFormat = zeroPaddedFormat

                if (hours <= 0) {
                    groupHour.visibility = GONE
                    minuteFormat = normalFormat
                } else {
                    groupHour.visibility = View.VISIBLE
                }
                textDigitHours.text = hours.toString()

                if (minutes <= 0) {
                    groupMinutes.visibility = GONE
                    secondFormat = normalFormat
                } else {
                    groupMinutes.visibility = View.VISIBLE
                }

                textDigitMinutes.text = String.format(Locale.US, minuteFormat, minutes)

                textDigitSeconds.text = String.format(Locale.US, secondFormat, seconds)
            }
        }
        binding.executePendingBindings()
    }
}