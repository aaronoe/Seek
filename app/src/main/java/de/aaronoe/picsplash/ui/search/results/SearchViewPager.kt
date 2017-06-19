package de.aaronoe.picsplash.ui.search.results

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment

/**
 * Created by aaron on 19.06.17.
 *
 */
class SearchViewPager(fm: FragmentManager,
                   val photoFragment: PhotoListFragment) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return photoFragment
        }
        return null
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "New"
        }
        return ""
    }

}