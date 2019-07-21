package org.rionlabs.tatsu.ui.screen.guide

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.work.PreferenceManager

class GuideActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showSkipButton(false)

        addSlide(
            AppIntroFragment.newInstance(
                "Guide Title 1", null,
                "Guide Description 1", null,
                R.mipmap.ic_launcher_round,
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.textColorLightPrimary),
                ContextCompat.getColor(this, R.color.textColorLightSecondary)
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Guide Title 2", null,
                "Guide Description 2", null,
                R.mipmap.ic_launcher_round,
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.textColorLightPrimary),
                ContextCompat.getColor(this, R.color.textColorLightSecondary)
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Guide Title 3", null,
                "Guide Description 3", null,
                R.mipmap.ic_launcher_round,
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.textColorLightPrimary),
                ContextCompat.getColor(this, R.color.textColorLightSecondary)
            )
        )
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Set flag
        val preferenceManager = PreferenceManager(application)
        preferenceManager.setGuideShown()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}