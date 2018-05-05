package de.aaronoe.seek.ui.search.results

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.aaronoe.seek.ui.collectionlist.CollectionFragment
import de.aaronoe.seek.ui.mainlist.PhotoListFragment

/**
 * Created by aaron on 19.06.17.
 *
 */
class SearchViewPager(fm: FragmentManager, val query: String) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return PhotoListFragment.createFragment(
                PhotoListFragment.MODE_SEARCH,
                "",
                "",
                query
            )
            1 -> return CollectionFragment.createFragment(CollectionFragment.MODE_SEARCH, query)
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Photos"
            1 -> return "Collections"
        }
        return ""
    }

}