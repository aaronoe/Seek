package de.aaronoe.picsplash.ui.mainnav

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.ui.mainlist.FeaturedFragment


class NavigationActivity : AppCompatActivity() {

    val fragmentManager: FragmentManager = supportFragmentManager

    val FILTER_POPULAR = "popular"
    val FILTER_LATEST = "latest"
    val FILTER_OLDEST = "oldest"

    lateinit var featuredFragment : FeaturedFragment
    lateinit var newFragment : FeaturedFragment
    lateinit var pagerAdapter : NavViewPager

    lateinit var mToolbar: Toolbar
    lateinit var mTabs: TabLayout
    lateinit var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        mTabs = findViewById(R.id.main_nav_tabs) as TabLayout
        mViewPager = findViewById(R.id.main_nav_viewpager) as ViewPager

        featuredFragment = FeaturedFragment(FILTER_POPULAR, "curated")
        newFragment = FeaturedFragment(FILTER_LATEST, "")
        pagerAdapter = NavViewPager(fragmentManager, featuredFragment, newFragment)

        mViewPager.adapter = pagerAdapter
        mTabs.setupWithViewPager(mViewPager)

        setSupportActionBar(mToolbar)
        title = "Unsplash"

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_item__popular -> {
                updateWithFilter(FILTER_POPULAR)
                return true
            }
            R.id.menu_item__latest -> {
                updateWithFilter(FILTER_LATEST)
                return true
            }
            R.id.menu_item__oldest -> {
                updateWithFilter(FILTER_OLDEST)
                return true
            }
        }
        return true
    }

    fun updateWithFilter(filter: String) {
        when(mTabs.selectedTabPosition) {
            0 -> {
                if (filter == featuredFragment.filter) return
                featuredFragment.filter = filter
                featuredFragment.presenter.downloadPhotos(1, 30, filter)
            }
            1 -> {
                if (filter == newFragment.filter) return
                featuredFragment.filter = filter
                newFragment.presenter.downloadPhotos(1, 30, filter)
            }
        }
    }

}
