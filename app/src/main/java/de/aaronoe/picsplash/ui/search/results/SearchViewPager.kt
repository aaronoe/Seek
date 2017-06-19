package de.aaronoe.picsplash.ui.search.results

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment

/**
 * Created by aaron on 19.06.17.
 *
 */
class SearchViewPager(fm: FragmentManager,
                      val photoFragment: PhotoListFragment,
                      val collectionFragment: CollectionFragment) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return photoFragment
            1 -> return collectionFragment
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Photos"
            1 -> return  "Collections"
        }
        return ""
    }

}