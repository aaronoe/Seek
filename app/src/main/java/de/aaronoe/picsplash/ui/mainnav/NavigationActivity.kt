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
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment
import de.aaronoe.picsplash.ui.intro.IntroScreen
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

        pagerAdapter = NavViewPager(fragmentManager)

        mViewPager.adapter = pagerAdapter
        mTabs.setupWithViewPager(mViewPager)

        setSupportActionBar(mToolbar)
        title = getString(R.string.app_name)

        startActivity(Intent(this, IntroScreen::class.java))

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
            R.id.menu_item_about -> {
                LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT)
                        .withActivityTitle(getString(R.string.about_licences))
                        .withAboutAppName(getString(R.string.app_name))
                        .withAboutIconShown(true)
                        .withAboutDescription(getString(R.string.about_description))
                        .withAboutVersionShown(true)
                        .start(this)
            }
        }
        return true
    }

    fun updateWithFilter(filter: String) {
        when(mTabs.selectedTabPosition) {
            0 -> {
                newFragment = pagerAdapter.newFragment
                newPresenter = (newFragment.presenter as PhotoListPresenterImpl)

                if (filter == newPresenter.filter) return
                newPresenter.filter = filter
                newFragment.presenter.downloadPhotos(1, 30)
            }
            1 -> {
                featuredFragment = pagerAdapter.featuredFragment
                featuredPresenter = (featuredFragment.presenter as PhotoListPresenterImpl)

                if (filter == featuredPresenter.filter) return
                featuredPresenter.filter = filter
                featuredFragment.presenter.downloadPhotos(1, 30)
            }
        }
    }

    override fun onBackPressed() {

        when (mTabs.selectedTabPosition) {
            0 -> {
                newFragment = pagerAdapter.newFragment
                newPresenter = (newFragment.presenter as PhotoListPresenterImpl)
                if (newFragment.currentPosition == 1) {
                    super.onBackPressed()
                    return
                }
                newFragment.moveToPosition(1)
                return
            }
            1 -> {
                featuredFragment = pagerAdapter.featuredFragment
                featuredPresenter = (featuredFragment.presenter as PhotoListPresenterImpl)
                if (featuredFragment.currentPosition == 1) {
                    super.onBackPressed()
                    return
                }
                featuredFragment.moveToPosition(1)
                return
            }
            2 -> {
                collectionFragment = pagerAdapter.collectionFragment
                if (collectionFragment.currentPosition == 1) {
                    super.onBackPressed()
                    return
                }
                collectionFragment.moveToPosition(1)
                return
            }
        }

        super.onBackPressed()
    }
}
