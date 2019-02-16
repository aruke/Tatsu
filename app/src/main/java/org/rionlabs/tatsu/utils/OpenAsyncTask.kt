package org.rionlabs.tatsu.utils

import android.os.AsyncTask

abstract class OpenAsyncTask : AsyncTask<Boolean, Boolean, Boolean>() {

    override fun doInBackground(vararg params: Boolean?): Boolean {
        performInBackground()
        return true
    }

    override fun onProgressUpdate(vararg values: Boolean?) {
        performOnProgressUpdate()
    }

    override fun onCancelled() {
        performOnCancelled()
    }

    abstract fun performInBackground()

    abstract fun performOnProgressUpdate()

    abstract fun performOnCancelled()
}