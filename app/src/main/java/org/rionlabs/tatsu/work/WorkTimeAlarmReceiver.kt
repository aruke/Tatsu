package org.rionlabs.tatsu.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import timber.log.Timber

class WorkTimeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Pending Intent Received with action ${intent.action}")
        Toast.makeText(context, intent.action, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val ACTION_SHOW_START_WORK_NOTIFICATION = "org.rionlabs.tatsu.start_work"
        const val ACTION_SHOW_END_WORK_NOTIFICATION = "org.rionlabs.tatsu.end_work"
    }
}