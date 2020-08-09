package org.rionlabs.tatsu.work

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.rionlabs.tatsu.data.model.WorkHoursType
import org.rionlabs.tatsu.utils.setExactCompat
import org.rionlabs.tatsu.work.receiver.WorkHoursAlarmReceiver
import java.util.*

class AlarmScheduler(val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleWorkHoursAlarm(workHoursType: WorkHoursType, hours: Int, minutes: Int) {
        val pendingIntent = buildPendingIntentForWorkHours(workHoursType)
        val millis = Calendar.getInstance(TimeZone.getDefault()).also {
            it.set(Calendar.HOUR, hours)
            it.set(Calendar.MINUTE, minutes)
        }.timeInMillis

        alarmManager.setExactCompat(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
    }

    fun cancelWorkHoursAlarm() {
        WorkHoursType.values().map {
            buildPendingIntentForWorkHours(it)
        }.forEach {
            alarmManager.cancel(it)
        }
    }

    private fun buildPendingIntentForWorkHours(workHoursType: WorkHoursType): PendingIntent {
        val receiverAction = when (workHoursType) {
            WorkHoursType.START -> WorkHoursAlarmReceiver.ACTION_SHOW_START_WORK_NOTIFICATION
            WorkHoursType.END -> WorkHoursAlarmReceiver.ACTION_SHOW_END_WORK_NOTIFICATION
        }
        val intent = Intent(context, WorkHoursAlarmReceiver::class.java).apply {
            action = receiverAction
        }
        return PendingIntent.getBroadcast(context, RC_WORK_HOURS, intent, 0)
    }

    companion object {
        private const val RC_WORK_HOURS = 1234
    }

}