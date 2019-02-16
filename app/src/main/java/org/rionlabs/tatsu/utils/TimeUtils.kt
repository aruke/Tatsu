package org.rionlabs.tatsu.utils

object TimeUtils {

    fun currentTimeEpoch(): Long {
        return System.currentTimeMillis() / 1000
    }
}