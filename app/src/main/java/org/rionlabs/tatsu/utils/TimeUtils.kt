package org.rionlabs.tatsu.utils

import android.content.Context
import org.rionlabs.tatsu.R
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun currentTimeEpoch(): Long {
        return System.currentTimeMillis() / 1000
    }

    fun toDurationString(context: Context, totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        var hourString =
            context.resources.getQuantityString(R.plurals.duration_format_hours, hours, hours)
        var minuteString =
            context.resources.getQuantityString(R.plurals.duration_format_minutes, minutes, minutes)

        // English Grammar doesn't honor `zero` plural. See https://stackoverflow.com/a/17261327
        if (Locale.getDefault().language == Locale.ENGLISH.language) {
            if (hours == 0)
                hourString = ""
            if (minutes == 0)
                minuteString = ""
        }

        return "$hourString $minuteString"
    }

    fun toTimeString(context: Context, time: Int): String {
        var (hours, minutes) = toHourMinutePair(time)
        return if (hours < 12) {
            if (hours == 0) {
                hours = 12
            }
            context.getString(R.string.time_format_am, hours, minutes)
        } else {
            if (hours == 12) {
                hours = 24
            }
            context.getString(R.string.time_format_pm, (hours - 12), minutes)
        }
    }

    /**
     * Returns a [Pair] of hour and minutes.
     * [Pair.first]: Hour is in 24 hours format [Integer] from 0-24.
     * [Pair.second]: Minute is [Integer] from 0-60.
     */
    private fun toHourMinutePair(time: Int): Pair<Int, Int> {
        return Pair(time / 100, time % 100)
    }

    fun toTimeString(context: Context, startEpoch: Long, endEpoch: Long): String {
        val startDate = Date(startEpoch * 1000L)
        val endDate = Date(endEpoch * 1000L)
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val startTime = timeFormat.format(startDate)
        val endTime = timeFormat.format(endDate)
        val formattedDate = dateFormat.format(startDate)

        return context.getString(R.string.stats_time_format, formattedDate, startTime, endTime)
    }
}