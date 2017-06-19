package de.aaronoe.picsplash.ui.search.results

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.ButterKnife
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment
import de.aaronoe.picsplash.ui.search.photos.PhotoSearchPresenter
import de.aaronoe.picsplash.util.bindView
import javax.inject.Inject

class SearchResultActivity : AppCompatActivity() {

    val fragmentManager: FragmentManager = supportFragmentManager

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
        photoPresenter = PhotoSearchPresenter(photoFragment, apiService, getString(R.string.client_id), searchQuery)
        photoFragment.presenter = photoPresenter

        pagerAdapter = SearchViewPager(fragmentManager, photoFragment)
        mViewPager.adapter = pagerAdapter
        setSupportActionBar(mToolbar)
        title = getString(R.string.app_name)

    }
}
