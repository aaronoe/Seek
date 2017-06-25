package de.aaronoe.seek.ui.userdetail

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.aaronoe.seek.data.model.photos.User
import de.aaronoe.seek.ui.collectionlist.CollectionFragment
import de.aaronoe.seek.ui.mainlist.PhotoListFragment

/**
 *
 * Created by private on 22.06.17.
 */
class UserViewPager(fm: FragmentManager, val user: User) : FragmentPagerAdapter(fm) {

    lateinit var photoFragment : PhotoListFragment
    lateinit var likesFragment : PhotoListFragment
    lateinit var collectionFragment : CollectionFragment

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                photoFragment = PhotoListFragment.createFragment(PhotoListFragment.MODE_USER_PHOTOS, "", "", user.username)
                return photoFragment
            }
            1 -> {
                collectionFragment = CollectionFragment.createFragment(CollectionFragment.MODE_USER, user.username)
                return collectionFragment
            }
            2 -> {
                likesFragment = PhotoListFragment.createFragment(PhotoListFragment.MODE_USER_LIKES, "", "", user.username)
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
            0 -> return "${user.totalPhotos} Photos"
            1 -> return "${user.totalCollections} Collections"
            2 -> return "${user.totalLikes} Likes"
        }
        return ""
    }

}