package de.aaronoe.picsplash.ui.photodetail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
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
import com.bumptech.glide.request.target.BitmapImageViewTarget
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
        SwipeScrollView.swipeScrollListener,
        PhotoDownloadUtils.imageDownloadListener {

    lateinit var photo: PhotosReply
    lateinit var singlePhoto: SinglePhoto
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
    val swipeLayout : SwipeBackLayout by bindView(R.id.swipe_back_layout)
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

    @Inject
    lateinit var apiService : UnsplashInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        ButterKnife.bind(this)
        (application as SplashApp).netComponent?.inject(this)
        swipeScrollView.setSwipeScrollListener(this)

        photo = intent.getParcelableExtra(getString(R.string.photo_detail_key))

        presenter = DetailPresenterImpl(this, apiService, this, photo)

        presenter.getDetailsForPhoto()
        initLayout()
        loadImage()
        setUpInfo()
    }


    override fun loadImage() {

        window.sharedElementExitTransition.setDuration(100).interpolator = DecelerateInterpolator()

        supportPostponeEnterTransition()
        /*
        Glide.with(this)
                .load(photo.urls.regular)
                .asBitmap()
                .centerCrop()
                .fitCenter()
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
                .into(photoImageView) */

        Glide.with(this)
                .load(photo.urls.regular)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .into(object : BitmapImageViewTarget(photoImageView) {
                    override fun setResource(resource: Bitmap) {
                        // Do bitmap magic here
                        photoImageView.setImageBitmap(resource)
                        supportStartPostponedEnterTransition()
                    }
                })

    }

    fun clickShare() {
        if (DisplayUtils.isStoragePermissionGranted(this)) {
            presenter.getIntentForImage((photoImageView.drawable as BitmapDrawable).bitmap)
        }
    }

    fun initLayout() {
        setDragEdge(SwipeBackLayout.DragEdge.TOP)

        setSupportActionBar(toolbar)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Make notification bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        sharePane.setOnClickListener { clickShare() }
        downloadPane.setOnClickListener { presenter.saveImage() }
        wallpaperPane.setOnClickListener { presenter.setImageAsWallpaper() }
        bottomSheet.addOnSheetStateChangeListener({
            state ->
            Log.e("State: ", (state.name == "HIDDEN").toString())
            setEnableSwipe((state.name == "HIDDEN")) })
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
        bottomSheet.showWithSheetView(IntentPickerSheetView
        (this, shareIntent, "Share with...", IntentPickerSheetView.OnIntentPickedListener { activityInfo ->
            bottomSheet.dismissSheet()
            startActivity(activityInfo.getConcreteIntent(shareIntent))
        }))
    }

    override fun showSnackBarShareError(message: String) {
        Snackbar.make(bottomSheet, message, Snackbar.LENGTH_SHORT)
                .setAction("Try again", { clickShare() }).show()
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
        Log.e("Fractionscreen: " , fractionScreen.toString())
        Log.e("Fractionanchor: " , fractionAnchor.toString())
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
        setEnableSwipe(atTop)
    }
}
