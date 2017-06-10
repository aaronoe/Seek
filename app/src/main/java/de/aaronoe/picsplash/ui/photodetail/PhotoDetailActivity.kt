package de.aaronoe.picsplash.ui.photodetail

import android.app.DownloadManager
import android.content.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.*
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flipboard.bottomsheet.BottomSheetLayout
import com.flipboard.bottomsheet.commons.IntentPickerSheetView
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.components.SwipeBackActivity
import de.aaronoe.picsplash.components.SwipeBackLayout
import de.aaronoe.picsplash.components.SwipeScrollView
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.model.singleItem.SinglePhoto
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.util.DisplayUtils
import de.aaronoe.picsplash.util.PhotoDownloadUtils
import de.aaronoe.picsplash.util.bindView
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject


class PhotoDetailActivity : SwipeBackActivity(),
        DetailContract.View,
        SwipeScrollView.swipeScrollListener {

    lateinit var photo: PhotosReply
    lateinit var presenter : DetailPresenterImpl
    val photoImageView : ImageView by bindView(R.id.image_item_iv)
    val userProfileView : CircleImageView by bindView(R.id.detail_author_image)
    val publishedOnView : TextView by bindView(R.id.detail_publish)
    val userNameView : TextView by bindView(R.id.author_name_tv)
    val toolbar: Toolbar by bindView(R.id.detailpage_toolbar)
    val bottomSheet : BottomSheetLayout by bindView(R.id.detail_bottom_sheet)
    val sharePane : LinearLayout by bindView(R.id.share_pane)
    val downloadPane : LinearLayout by bindView(R.id.download_pane)
    val wallpaperPane : LinearLayout by bindView(R.id.wallpaper_pane)
    val progressBar : ProgressBar by bindView(R.id.detail_progress_download)
    val swipeScrollView : SwipeScrollView by bindView(R.id.container_scrollview)

    // Meta pane TextViews
    val resTv : TextView by bindView(R.id.meta_res_tv)
    val colorTv : TextView by bindView(R.id.meta_color_tv)
    val locationTv : TextView by bindView(R.id.meta_location_tv)
    val cameraTv : TextView by bindView(R.id.meta_camera_tv)
    val exposureTv : TextView by bindView(R.id.meta_exposure_tv)
    val apertureTv : TextView by bindView(R.id.meta_aperture_tv)
    val focalTv : TextView by bindView(R.id.meta_focal_tv)
    val isoTv : TextView by bindView(R.id.meta_iso_tv)
    val metaPb : ProgressBar by bindView(R.id.meta_pb)
    val metaLayout : ConstraintLayout by bindView(R.id.meta_layout)
    val metaColorIv : ImageView by bindView(R.id.meta_color_iv)

    var positionAtTop = false

    @Inject
    lateinit var apiService : UnsplashInterface
    @Inject
    lateinit var sharedPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        ButterKnife.bind(this)
        (application as SplashApp).netComponent?.inject(this)
        swipeScrollView.setSwipeScrollListener(this)

        photo = intent.getParcelableExtra(getString(R.string.photo_detail_key))
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        presenter = DetailPresenterImpl(this, apiService, this, photo)

        presenter.getDetailsForPhoto()
        initLayout()
        loadImage()
        setUpInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }

    override fun loadImage() {

        window.sharedElementExitTransition.setDuration(100).interpolator = DecelerateInterpolator()

        supportPostponeEnterTransition()

        val photoUrl = PhotoDownloadUtils.getPhotoLinkForQuality(photo,
                sharedPrefs.getString(getString(R.string.pref_key_display_quality),
                        getString(R.string.quality_regular_const)))

        Glide.with(this)
                .load(photoUrl)
                .asBitmap()
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.HIGH)
                .dontAnimate()
                .listener(object : RequestListener<String, Bitmap> {
                    override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onException(e: java.lang.Exception?, model: String?, target: com.bumptech.glide.request.target.Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                })
                .into(photoImageView)

    }

    fun initLayout() {

        val slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide)
        window.enterTransition = slide

        setDragEdge(SwipeBackLayout.DragEdge.TOP)

        setSupportActionBar(toolbar)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Make notification bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        sharePane.setOnClickListener {
            if (DisplayUtils.isStoragePermissionGranted(this)) {
                showBottomProgressBar()
                presenter.getIntentForImage((photoImageView.drawable as BitmapDrawable).bitmap)
            } }

        downloadPane.setOnClickListener {
            if (DisplayUtils.isStoragePermissionGranted(this)) {
                showBottomProgressBar()
                PhotoDownloadUtils.downloadPhoto(this, photo)
            } }

        wallpaperPane.setOnClickListener {
            if (DisplayUtils.isStoragePermissionGranted(this)) {
                presenter.setImageAsWallpaper()
            } }

        bottomSheet.addOnSheetStateChangeListener({
            state ->
            Log.e("State: ", (state.name == "HIDDEN").toString())
            setEnableSwipe((state.name == "HIDDEN") && positionAtTop)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setUpInfo() {
        userNameView.text = getString(R.string.by_user, photo.user.name)
        publishedOnView.text = getString(R.string.on_date, photo.createdAt.split("T")[0])
        Glide.with(this).load(photo.user.profileImage.large)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.HIGH).into(userProfileView)
    }

    override fun showShareBottomsheet(shareIntent: Intent) {
        hideBottomProgressBar()
        bottomSheet.showWithSheetView(IntentPickerSheetView
        (this, shareIntent, "Share with...", IntentPickerSheetView.OnIntentPickedListener { activityInfo ->
            bottomSheet.dismissSheet()
            startActivity(activityInfo.getConcreteIntent(shareIntent))
        }))
    }

    override fun showSnackBarShareError(message: String) {
        Snackbar.make(bottomSheet, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showBottomProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideBottomProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun showDownloadError() {
        Toast.makeText(this, "Download error", Toast.LENGTH_SHORT).show()
    }

    override fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float) {
        super.onViewPositionChanged(fractionAnchor, fractionScreen)
        if (fractionScreen > 0.2f) onBackPressed()
    }

    override fun showDetailPane(singlePhoto: SinglePhoto?) {
        resTv.text = getString(R.string.width_by_height, singlePhoto?.width, singlePhoto?.height)
        cameraTv.text = singlePhoto?.exif?.model
        apertureTv.text = singlePhoto?.exif?.aperture
        exposureTv.text = singlePhoto?.exif?.exposureTime
        focalTv.text = singlePhoto?.exif?.focalLength
        if (singlePhoto?.location != null) {
            locationTv.text = getString(R.string.city_country, singlePhoto.location?.city, singlePhoto.location?.country)
        } else {
            locationTv.text = getString(R.string.not_available)
        }
        isoTv.text = singlePhoto?.exif?.iso.toString()
        colorTv.text = singlePhoto?.color
        metaColorIv.setImageDrawable(ColorDrawable(Color.parseColor(photo.color)))
        resources.getDrawable(R.drawable.ic_fiber_manual_record_black_24dp, theme)
                .setColorFilter(Color.parseColor(photo.color), PorterDuff.Mode.SRC_IN)
    }

    override fun showLoading() {
        metaPb.visibility = View.VISIBLE
        metaLayout.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        metaPb.visibility = View.INVISIBLE
        metaLayout.visibility = View.VISIBLE
    }

    override fun hideMetaPane() {
        metaLayout.visibility = View.INVISIBLE
    }

    override fun scrolledToTop(atTop: Boolean) {
        Log.e("Can scroll up: ", (!ViewCompat.canScrollVertically(swipeScrollView, -1)).toString())
        Log.e("Can scroll down: ", (!ViewCompat.canScrollVertically(swipeScrollView, 1)).toString())
        setEnableSwipe(atTop)
        positionAtTop = atTop
    }

    internal var onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            hideBottomProgressBar()
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            val intentForFile = PhotoDownloadUtils.getIntentForFile(context, downloadId)
            Snackbar.make(bottomSheet, getString(R.string.img_downloaded), Snackbar.LENGTH_LONG)
                    .setActionTextColor(context.resources.getColor(R.color.Link_Water))
                    .setAction("Open Image", {startActivity(intentForFile)}).show()
        }
    }

}
