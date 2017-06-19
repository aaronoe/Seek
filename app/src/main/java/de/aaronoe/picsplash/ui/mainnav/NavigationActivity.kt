package de.aaronoe.picsplash.ui.mainnav

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment
import de.aaronoe.picsplash.ui.mainlist.PhotoListPresenterImpl
import de.aaronoe.picsplash.ui.preferences.PrefActivity
import de.aaronoe.picsplash.ui.search.SearchActivity
import javax.inject.Inject


class NavigationActivity : AppCompatActivity() {

    companion object {
        const val FILTER_POPULAR = "popular"
        const val FILTER_LATEST = "latest"
        const val FILTER_OLDEST = "oldest"
        const val FILTER_CURATED = "curated"
        const val FILTER_NOT_CURATED = ""
    }

    val fragmentManager: FragmentManager = supportFragmentManager

    lateinit var newFragment : PhotoListFragment
    lateinit var featuredFragment: PhotoListFragment
    lateinit var collectionFragment : CollectionFragment

    lateinit var newPresenter : PhotoListPresenterImpl
    lateinit var featuredPresenter: PhotoListPresenterImpl

    lateinit var pagerAdapter : NavViewPager

    lateinit var mToolbar: Toolbar
    lateinit var mTabs: TabLayout
    lateinit var mViewPager: ViewPager

    @Inject
    lateinit var apiService : UnsplashInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        (application as SplashApp).netComponent.inject(this)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        mTabs = findViewById(R.id.main_nav_tabs) as TabLayout
        mViewPager = findViewById(R.id.main_nav_viewpager) as ViewPager

        newFragment = PhotoListFragment()
        featuredFragment = PhotoListFragment()
        collectionFragment = CollectionFragment()

        newPresenter = PhotoListPresenterImpl(newFragment, apiService,
                FILTER_NOT_CURATED, FILTER_LATEST, getString(R.string.client_id))
        featuredPresenter = PhotoListPresenterImpl(featuredFragment, apiService,
                FILTER_CURATED, FILTER_LATEST, getString(R.string.client_id))

        newFragment.presenter = newPresenter
        featuredFragment.presenter = featuredPresenter

        pagerAdapter = NavViewPager(fragmentManager, featuredFragment, newFragment, collectionFragment)

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
            R.id.menu_item_settings -> {
                startActivity(Intent(this, PrefActivity::class.java))
            }
            R.id.search_action -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
        }
        return true
    }

    fun updateWithFilter(filter: String) {
        when(mTabs.selectedTabPosition) {
            0 -> {
                if (filter == newPresenter.filter) return
                newPresenter.filter = filter
                newFragment.presenter.downloadPhotos(1, 30)
            }
            1 -> {
                if (filter == featuredPresenter.filter) return
                featuredPresenter.filter = filter
                featuredFragment.presenter.downloadPhotos(1, 30)
            }
        }
    }

}
