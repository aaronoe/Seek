package de.aaronoe.picsplash.ui.userdetail

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment

/**
 *
 * Created by private on 22.06.17.
 */
class UserViewPager(fm: FragmentManager, val username: String) : FragmentPagerAdapter(fm) {

    lateinit var photoFragment : PhotoListFragment
    lateinit var likesFragment : PhotoListFragment
    lateinit var collectionFragment : CollectionFragment

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                photoFragment = PhotoListFragment.createFragment(PhotoListFragment.MODE_USER_PHOTOS, "", "", username)
                return photoFragment
            }
            1 -> {
                collectionFragment = CollectionFragment.createFragment(CollectionFragment.MODE_USER, username)
                return collectionFragment
            }
            2 -> {
                likesFragment = PhotoListFragment.createFragment(PhotoListFragment.MODE_USER_LIKES, "", "", username)
                return likesFragment
            }
        }
        return null
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Photos"
            1 -> return "Collections"
            2 -> return "Likes"
        }
        return ""
    }

}