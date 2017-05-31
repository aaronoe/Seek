package de.aaronoe.picsplash.ui.mainnav

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
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

    val featuredFragment = FeaturedFragment(FILTER_POPULAR, "curated")
    val newFragment = FeaturedFragment(FILTER_LATEST, "")

    lateinit var mToolbar: Toolbar
    lateinit var navigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        title = "Unsplash"

        navigation = findViewById(R.id.bottom_navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_featured -> {
                fragmentManager.beginTransaction().replace(R.id.nav_frame, featuredFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_new -> {
                fragmentManager.beginTransaction().replace(R.id.nav_frame, newFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_collection -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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
        when(navigation.selectedItemId) {
            R.id.navigation_featured -> {
                if (filter == featuredFragment.filter) return
                featuredFragment.filter = filter
                featuredFragment.presenter.downloadPhotos(1, 30, filter)
            }
            R.id.navigation_new -> {
                if (filter == newFragment.filter) return
                featuredFragment.filter = filter
                newFragment.presenter.downloadPhotos(1, 30, filter)
            }
        }
    }

}
