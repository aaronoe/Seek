package de.aaronoe.seek.ui.userdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.photos.User
import de.aaronoe.seek.ui.preferences.PrefActivity
import de.aaronoe.seek.util.bindView
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class UserDetailActivity : AppCompatActivity() {

    val userPhotoIv : CircleImageView by bindView(R.id.user_photo_iv)
    val userNameTv : TextView by bindView(R.id.user_name_tv)
    val userLocationTv : TextView by bindView(R.id.user_location_tv)
    val userBioTv : TextView by bindView(R.id.user_bio_tv)
    val toolbar : android.support.v7.widget.Toolbar by bindView(R.id.user_toolbar)
    val mViewPager : ViewPager by bindView(R.id.user_viewpager)
    val mTabs : TabLayout by bindView(R.id.user_tabs)

    lateinit var pagerAdapter : UserViewPager
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        ButterKnife.bind(this)

        if (intent.hasExtra(getString(R.string.intent_key_user))) {
            user = intent.extras.getParcelable(getString(R.string.intent_key_user))
            initUserInfoView()
            setUpViewPager()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }


    fun setUpViewPager() {
        pagerAdapter = UserViewPager(supportFragmentManager, user)
        mViewPager.adapter = pagerAdapter
        mTabs.setupWithViewPager(mViewPager)
    }


    fun initUserInfoView() {

        invalidateOptionsMenu()

        Glide.with(this).load(user.profileImage.large)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .listener(object : RequestListener<String, GlideDrawable> {

                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                }).into(userPhotoIv)

        userNameTv.text = user.name

        if (user.location != null && !TextUtils.isEmpty(user.location)) {
            userLocationTv.text = user.location
        } else {
            userLocationTv.visibility = View.GONE
        }

        if (user.bio != null && !TextUtils.isEmpty(user.bio)) {
            userBioTv.text = user.bio
        } else {
            userBioTv.visibility = View.GONE
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (user.portfolioUrl == null || TextUtils.isEmpty(user.portfolioUrl)) {
            menu?.findItem(R.id.user_portfolio_action)?.isEnabled = false
            menu?.findItem(R.id.user_portfolio_action)?.isVisible = false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_item_settings -> startActivity(Intent(this, PrefActivity::class.java))
            R.id.user_website_action -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.links.html + getString(R.string.utm_params)))
                startActivity(intent)
            }
            R.id.user_portfolio_action -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.portfolioUrl + getString(R.string.utm_params)))
                startActivity(intent)
            }
        }
        return true
    }
}
