package org.rionlabs.tatsu.ui.screen.begin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import org.rionlabs.tatsu.ui.screen.guide.GuideActivity
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.work.PreferenceManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = get<PreferenceManager>()
        val intent = if (preferenceManager.shouldShowGuide()) {
            Intent(this, GuideActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}