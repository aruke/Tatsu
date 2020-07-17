package org.rionlabs.tatsu.ui.screen.guide

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroPageTransformerType
import org.koin.android.ext.android.get
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.ui.screen.main.MainActivity
import org.rionlabs.tatsu.work.PreferenceManager

class GuideActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isSkipButtonEnabled = false

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_guide_1))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_guide_2))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.slide_guide_3))

        setTransformer(
            AppIntroPageTransformerType.Parallax(
                titleParallaxFactor = -1.0,
                imageParallaxFactor = 100.0,
                descriptionParallaxFactor = -1.0
            )
        )
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Set flag
        val preferenceManager = get<PreferenceManager>()
        preferenceManager.setGuideShown()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}