package de.aaronoe.seek.ui.mainnav

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.yarolegovich.slidingrootnav.SlidingRootNavLayout
import de.aaronoe.seek.R
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.collectionlist.CollectionFragment
import de.aaronoe.seek.ui.login.LoginActivity
import de.aaronoe.seek.ui.mainlist.PhotoListFragment
import de.aaronoe.seek.ui.mainlist.PhotoListPresenterImpl
import de.aaronoe.seek.ui.mainnav.menu.DrawerAdapter
import de.aaronoe.seek.ui.mainnav.menu.DrawerItem
import de.aaronoe.seek.ui.mainnav.menu.SimpleItem
import de.aaronoe.seek.ui.mainnav.menu.SpaceItem
import de.aaronoe.seek.ui.preferences.PrefActivity
import de.aaronoe.seek.ui.search.SearchActivity
import javax.inject.Inject


class NavigationActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener {

    companion object {
        const val FILTER_POPULAR = "popular"
        const val FILTER_LATEST = "latest"
        const val FILTER_OLDEST = "oldest"
        const val FILTER_CURATED = "curated"
        const val FILTER_NOT_CURATED = ""

        const val POS_PHOTOS = 0
        const val POS_SEARCH = 1
        const val POS_ACCOUNT = 2
        const val POS_SETTINGS = 4
        const val POS_ABOUT = 5
    }

    val fragmentManager: FragmentManager = supportFragmentManager
    var menuOpen = false

    lateinit var newFragment : PhotoListFragment
    lateinit var featuredFragment: PhotoListFragment
    lateinit var collectionFragment : CollectionFragment

    lateinit var newPresenter : PhotoListPresenterImpl
    lateinit var featuredPresenter: PhotoListPresenterImpl

    lateinit var pagerAdapter : NavViewPager

    lateinit var mToolbar: Toolbar
    lateinit var mTabs: TabLayout
    lateinit var mViewPager: ViewPager
    lateinit var mContainer : CoordinatorLayout
    lateinit var menuRv : RecyclerView
    lateinit var slidingNavLayout : SlidingRootNavLayout

    @Inject
    lateinit var apiService : UnsplashInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        (application as SplashApp).netComponent.inject(this)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        mTabs = findViewById(R.id.main_nav_tabs) as TabLayout
        mViewPager = findViewById(R.id.main_nav_viewpager) as ViewPager
        mContainer = findViewById(R.id.nav_container) as CoordinatorLayout

        pagerAdapter = NavViewPager(fragmentManager)

        mViewPager.adapter = pagerAdapter
        mTabs.setupWithViewPager(mViewPager)

        setSupportActionBar(mToolbar)
        title = getString(R.string.app_name)

        setUpDrawer(savedInstanceState)

    }

    fun setUpDrawer(savedInstanceState: Bundle?) {
        val layout = SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(mToolbar)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject()

        menuOpen = false
        menuRv = layout.layout.findViewById(R.id.menu_recycler) as RecyclerView
        slidingNavLayout = layout.layout

        val itemList = arrayListOf(
                createItemFor(getDrawable(R.drawable.ic_photo_camera_black_24dp), getString(R.string.photos)).setChecked(true),
                createItemFor(getDrawable(R.drawable.ic_search_black_24dp), getString(R.string.search_action)),
                createItemFor(getDrawable(R.drawable.ic_person_black_24dp), getString(R.string.my_account)),
                SpaceItem(36),
                createItemFor(getDrawable(R.drawable.ic_settings_black_24dp), getString(R.string.action_settings)),
                createItemFor(getDrawable(R.drawable.ic_info_outline_black_24dp), getString(R.string.about))
        )

        val adapter = DrawerAdapter(itemList)

        menuRv.isNestedScrollingEnabled = false
        menuRv.adapter = adapter
        menuRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.setSelected(POS_PHOTOS)
        adapter.setListener(this)

    }

    private fun createItemFor(icon: Drawable, title: String): DrawerItem<*> {
        return SimpleItem(icon, title)
                .withIconTint(color(R.color.Old_Link_Water))
                .withTextTint(color(R.color.Old_Link_Water))
                .withSelectedIconTint(color(R.color.menuAccent))
                .withSelectedTextTint(color(R.color.menuAccent))
    }

    override fun onItemSelected(position: Int) {
        when (position) {
            POS_PHOTOS -> slidingNavLayout.closeMenu(true)
            POS_SEARCH -> startActivity(Intent(this, SearchActivity::class.java))
            POS_SETTINGS -> startActivity(Intent(this, PrefActivity::class.java))
            POS_ABOUT -> {
                LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT)
                        .withActivityTitle(getString(R.string.about_licences))
                        .withAboutAppName(getString(R.string.app_name))
                        .withAboutIconShown(true)
                        .withAboutDescription(getString(R.string.about_description))
                        .withAboutVersionShown(true)
                        .start(this)
            }
            POS_ACCOUNT -> startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    @ColorInt
    private fun color(@ColorRes res: Int): Int {
        return ContextCompat.getColor(this, res)
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
            R.id.search_action -> {
                startActivity(Intent(this, SearchActivity::class.java))
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
                if (newFragment.currentPosition == 0) {
                    super.onBackPressed()
                    return
                }
                newFragment.moveToPosition(0)
                return
            }
            1 -> {
                featuredFragment = pagerAdapter.featuredFragment
                featuredPresenter = (featuredFragment.presenter as PhotoListPresenterImpl)
                if (featuredFragment.currentPosition == 0) {
                    super.onBackPressed()
                    return
                }
                featuredFragment.moveToPosition(0)
                return
            }
            2 -> {
                collectionFragment = pagerAdapter.collectionFragment
                if (collectionFragment.currentPosition == 0) {
                    super.onBackPressed()
                    return
                }
                collectionFragment.moveToPosition(0)
                return
            }
        }

        super.onBackPressed()
    }
}
