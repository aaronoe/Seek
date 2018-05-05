package de.aaronoe.seek.ui.search.results

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import butterknife.ButterKnife
import de.aaronoe.seek.R
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.preferences.PrefActivity
import de.aaronoe.seek.util.bindView
import javax.inject.Inject

class SearchResultActivity : AppCompatActivity() {

    val fragmentManager: FragmentManager = supportFragmentManager

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

        pagerAdapter = SearchViewPager(fragmentManager, searchQuery)
        mViewPager.adapter = pagerAdapter
        mTabs.setupWithViewPager(mViewPager)

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = searchQuery.capitalize()

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
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}
