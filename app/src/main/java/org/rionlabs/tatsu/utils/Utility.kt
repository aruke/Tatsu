package org.rionlabs.tatsu.utils

import timber.log.Timber

object Utility {

    fun waitForOneSecond() {
        try {
            Thread.sleep(1000)
        } catch (ie: InterruptedException) {
            Timber.i(ie, "Timer InterruptedException.")
        }
    }
}