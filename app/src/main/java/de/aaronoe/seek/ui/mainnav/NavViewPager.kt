package de.aaronoe.seek.ui.mainnav

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import de.aaronoe.seek.ui.collectionlist.CollectionFragment
import de.aaronoe.seek.ui.mainlist.PhotoListFragment

/**
 * Created by aaron on 31.05.17.
 *
 */
class NavViewPager(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    lateinit var newFragment : PhotoListFragment
    lateinit var featuredFragment : PhotoListFragment
    lateinit var collectionFragment : CollectionFragment

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                newFragment = PhotoListFragment.createFragment(PhotoListFragment.MODE_LIST, NavigationActivity.FILTER_LATEST, NavigationActivity.FILTER_NOT_CURATED, "")
                return newFragment
            }
            1 -> {
                featuredFragment = PhotoListFragment.createFragment(PhotoListFragment.MODE_LIST, NavigationActivity.FILTER_LATEST, NavigationActivity.FILTER_CURATED, "")
                return featuredFragment
            }
            2 -> {
                collectionFragment = CollectionFragment.createFragment(CollectionFragment.MODE_LIST, "")
                return collectionFragment
            }
        }
        return null
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "New"
            1 -> return "Featured"
            2 -> return "Collections"
        }
        return ""
    }

    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }
}