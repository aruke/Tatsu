package org.rionlabs.tatsu.ui.screen.guide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.ActivityGuideBinding
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.work.PreferenceManager

class GuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide)
        setSupportActionBar(binding.toolbar)

        binding.buttonFinishGuide.setOnClickListener {
            // Set flag
            val preferenceManager = PreferenceManager(application)
            preferenceManager.setGuideShown()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}