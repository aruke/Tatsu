package org.rionlabs.tatsu.ui.screen.guide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_guide.*
import org.rionlabs.tatsu.R

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        setSupportActionBar(toolbar)
    }
}
