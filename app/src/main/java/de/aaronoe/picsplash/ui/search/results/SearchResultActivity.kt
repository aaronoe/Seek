package de.aaronoe.picsplash.ui.search.results

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import butterknife.ButterKnife
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment
import de.aaronoe.picsplash.ui.preferences.PrefActivity
import de.aaronoe.picsplash.ui.search.collections.CollectionSearchPresenterImpls
import de.aaronoe.picsplash.ui.search.photos.PhotoSearchPresenter
import de.aaronoe.picsplash.util.bindView
import javax.inject.Inject

class SearchResultActivity : AppCompatActivity() {

    val fragmentManager: FragmentManager = supportFragmentManager

    lateinit var collectionFragment : CollectionFragment
    lateinit var collectionPresenter : CollectionSearchPresenterImpls
    lateinit var photoFragment : PhotoListFragment
    lateinit var photoPresenter : PhotoSearchPresenter
    lateinit var pagerAdapter : SearchViewPager

    val mToolbar: Toolbar by bindView(R.id.toolbar)
    val mTabs: TabLayout by bindView(R.id.main_nav_tabs)
    val mViewPager: ViewPager by bindView(R.id.main_nav_viewpager)

    @Inject
    lateinit var apiService : UnsplashInterface

    lateinit var searchQuery : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        (application as SplashApp).netComponent.inject(this)
        ButterKnife.bind(this)

        searchQuery = intent.extras.getString(getString(R.string.intent_key_search_query))

        photoFragment = PhotoListFragment()
        collectionFragment = CollectionFragment()

        photoPresenter = PhotoSearchPresenter(photoFragment, apiService, getString(R.string.client_id), searchQuery)
        collectionPresenter = CollectionSearchPresenterImpls(collectionFragment, apiService, getString(R.string.client_id), searchQuery)
        photoFragment.presenter = photoPresenter
        collectionFragment.presenter = collectionPresenter

        pagerAdapter = SearchViewPager(fragmentManager, photoFragment, collectionFragment)
        mViewPager.adapter = pagerAdapter
        mTabs.setupWithViewPager(mViewPager)

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.app_name)

    }

    override fun onBackPressed() {

        when (mTabs.selectedTabPosition) {
            0 -> {
                if (photoFragment.currentPosition == 0) {
                    super.onBackPressed()
                    return
                }
                photoFragment.moveToPosition(0)
                return
            }
            1 -> {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.only_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_item_settings -> {
                startActivity(Intent(this, PrefActivity::class.java))
            }
        }
        return true
    }
}
