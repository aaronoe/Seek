package de.aaronoe.picsplash.ui.intro

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import de.aaronoe.picsplash.R

/**
 * Created by private on 23.06.17.
 *
 */
class IntroScreen : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(AppIntroFragment.newInstance("TestTitle", "Test Description", R.drawable.test_photo, Color.BLUE))
        addSlide(AppIntroFragment.newInstance("TestTitle", "Test Description", R.drawable.test_photo, Color.BLUE))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        finish()
        super.onSkipPressed(currentFragment)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        finish()
        super.onDonePressed(currentFragment)
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
    }

}