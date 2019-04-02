package org.rionlabs.tatsu.work

import android.app.Application
import android.content.Context
import android.media.AudioManager

class SilentModeManager(app: Application) {

    private var ringerMode: Int = -1
    private var mode: Int = -1

    private val audioManager = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun turnOnSilentMode() {
        // If there is no previous mode, set ringerMode to remember previous configuration
        if (ringerMode == -1) {
            ringerMode = audioManager.ringerMode
            mode = audioManager.mode
        }
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    fun turnOffSilentMode() {
        // Restore previous mode, if available
        if (ringerMode != -1) {
            audioManager.ringerMode = ringerMode
            audioManager.mode = mode
            ringerMode = -1
        }
    }
}