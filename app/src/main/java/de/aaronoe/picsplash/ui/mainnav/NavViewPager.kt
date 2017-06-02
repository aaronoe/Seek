package de.aaronoe.picsplash.ui.mainnav

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.aaronoe.picsplash.ui.mainlist.FeaturedFragment

/**
 * Created by aaron on 31.05.17.
 *
 */
class NavViewPager(fm: FragmentManager,
                   val featuredFragment: FeaturedFragment,
                   val newFragment: FeaturedFragment) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return newFragment
            1 -> return featuredFragment
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "New"
            1 -> return "Featured"
        }
        return ""
    }

}