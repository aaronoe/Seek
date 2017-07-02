package de.aaronoe.seek.ui.photodetail

import android.app.DownloadManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.transition.TransitionInflater
import android.util.Log
import android.view.MenuItem
import android.view.View
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
import com.github.chrisbanes.photoview.PhotoView
import com.sackcentury.shinebuttonlib.ShineButton
import de.aaronoe.seek.R
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.auth.AuthManager
import de.aaronoe.seek.components.SwipeBackActivity
import de.aaronoe.seek.components.SwipeBackLayout
import de.aaronoe.seek.components.SwipeScrollView
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.singleItem.SinglePhoto
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.userdetail.UserDetailActivity
import de.aaronoe.seek.util.DisplayUtils
import de.aaronoe.seek.util.PhotoDownloadUtils
import de.aaronoe.seek.util.bindView
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject


class PhotoDetailActivity : SwipeBackActivity(),
        DetailContract.View,
        SwipeScrollView.swipeScrollListener {

    lateinit var photo: PhotosReply
    lateinit var presenter : DetailPresenterImpl
    val photoImageView : PhotoView by bindView(R.id.image_item_iv)
    val userProfileView : CircleImageView by bindView(R.id.detail_author_image)
    val publishedOnView : TextView by bindView(R.id.detail_publish)
    val userNameView : TextView by bindView(R.id.author_name_tv)
    val toolbar: Toolbar by bindView(R.id.detailpage_toolbar)
    val bottomSheet : BottomSheetLayout by bindView(R.id.detail_bottom_sheet)
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
    val userContainer : LinearLayout by bindView(R.id.user_container_group)

    val sharePane : ShineButton by bindView(R.id.shine_share)
    val downloadPane : ShineButton by bindView(R.id.shine_download)
    val wallpaperPane : ShineButton by bindView(R.id.shine_wallpaper)
    val likeButton : ShineButton by bindView(R.id.shine_like)
    val favoriteButton : ShineButton by bindView(R.id.shine_add)
    val likeCaption : TextView by bindView(R.id.like_caption)
    val addCaption : TextView by bindView(R.id.add_caption)

    var positionAtTop = false

    @Inject
    lateinit var apiService : UnsplashInterface
    @Inject
    lateinit var sharedPrefs : SharedPreferences
    @Inject
    lateinit var authManager : AuthManager

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

    override fun showDialog(dialog: AlertDialog) {
        dialog.show()
    }



    fun initLayout() {

        downloadPane.init(this)
        sharePane.init(this)
        wallpaperPane.init(this)

        if (authManager.loggedIn) {
            likeButton.visibility = View.VISIBLE
            favoriteButton.visibility = View.VISIBLE
            likeCaption.visibility = View.VISIBLE
            addCaption.visibility = View.VISIBLE

            likeButton.init(this)
            favoriteButton.init(this)

            likeButton.setChecked(photo.likedByUser, false)
            likeButton.setOnCheckStateChangeListener { _, b ->
                if (b) {
                    presenter.likePicture(photo.id)
                } else {
                    presenter.dislikePicture(photo.id)
                }
            }

            favoriteButton.setOnCheckStateChangeListener { _, b ->
                presenter.addPhotoToCollections(authManager.userName, photo.id)
            }


        }

        val slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide)
        window.enterTransition = slide

        setDragEdge(SwipeBackLayout.DragEdge.TOP)
        setSupportActionBar(toolbar)
        
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        userContainer.setOnClickListener {
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_user), photo.user)
            val options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, userProfileView, getString(R.string.user_photo_transition_key))
            startActivity(intent, options.toBundle())
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
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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

    override fun showSnackBarWithMessage(message: String) {
        val snackBar = Snackbar.make(bottomSheet, message, Snackbar.LENGTH_SHORT).apply {
            setAction(getString(R.string.dismiss), { this.dismiss() })
            setActionTextColor(Color.WHITE)
        }
        (snackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackBar.show()
    }

    fun showLocationSnackBar(singlePhoto: SinglePhoto?, message: String) {
        val snackBar = Snackbar.make(bottomSheet, message, Snackbar.LENGTH_LONG)
        if (singlePhoto?.location?.position != null) {

            snackBar.run {
                setAction(getString(R.string.open_in_maps), {

                    val latitude = singlePhoto.location?.position?.latitude
                    val longitude = singlePhoto.location?.position?.longitude
                    val label = getString(R.string.users_photo, photo.user.firstName)


                    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude$label")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.`package` = "com.google.android.apps.maps"
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(mapIntent)
                    }
                })
                setActionTextColor(Color.WHITE)
            }

        }

        (snackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackBar.show()
    }

    fun showCameraSnackbar(singlePhoto: SinglePhoto?) {
        val snackBar = Snackbar.make(bottomSheet, getString(R.string.camera_model, singlePhoto?.exif?.model), Snackbar.LENGTH_LONG)
        if (singlePhoto?.exif?.model != null && !TextUtils.isEmpty(singlePhoto.exif?.model)) {

            snackBar.run {
                setAction(getString(R.string.google_search), {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/#q=${singlePhoto.exif?.model}")))
                })
                setActionTextColor(Color.WHITE)
            }
        }
        (snackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackBar.show()
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
        if (singlePhoto?.exif?.model == null) cameraTv.text = getString(R.string.not_available)

        apertureTv.text = singlePhoto?.exif?.aperture
        if (singlePhoto?.exif?.aperture == null) apertureTv.text = getString(R.string.not_available)

        exposureTv.text = singlePhoto?.exif?.exposureTime
        if (singlePhoto?.exif?.exposureTime == null) exposureTv.text = getString(R.string.not_available)

        focalTv.text = singlePhoto?.exif?.focalLength
        if (singlePhoto?.exif?.focalLength == null) focalTv.text = getString(R.string.not_available)

        val locationString : String?
        if (singlePhoto?.location?.city != null && singlePhoto.location.country != null) {
            locationString = getString(R.string.city_country, singlePhoto.location?.city, singlePhoto.location?.country)
            locationTv.text = locationString
        } else {
            if (singlePhoto?.location?.country != null) {
                locationString = singlePhoto.location.country
                locationTv.text = locationString
            } else {
                locationString = getString(R.string.not_available)
                locationTv.text = locationString
            }
        }

        isoTv.text = singlePhoto?.exif?.iso.toString()
        if (singlePhoto?.exif?.iso == null) isoTv.text = getString(R.string.not_available)

        colorTv.text = singlePhoto?.color
        if (singlePhoto?.color == null) colorTv.text = getString(R.string.not_available)
        metaColorIv.setImageDrawable(ColorDrawable(Color.parseColor(photo.color)))
        resources.getDrawable(R.drawable.ic_fiber_manual_record_black_24dp, theme)
                .setColorFilter(Color.parseColor(photo.color), PorterDuff.Mode.SRC_IN)

        resTv.setOnClickListener { showSnackBarWithMessage(getString(R.string.res_tip, singlePhoto?.width, singlePhoto?.height)) }
        cameraTv.setOnClickListener { showCameraSnackbar(singlePhoto) }
        apertureTv.setOnClickListener { showSnackBarWithMessage(getString(R.string.aperture_tip, singlePhoto?.exif?.aperture)) }
        exposureTv.setOnClickListener { showSnackBarWithMessage(getString(R.string.exposure_tip, singlePhoto?.exif?.exposureTime)) }
        focalTv.setOnClickListener { showSnackBarWithMessage(getString(R.string.focal_tip, singlePhoto?.exif?.focalLength)) }
        locationTv.setOnClickListener { showLocationSnackBar(singlePhoto, getString(R.string.location_tip, locationString)) }
        isoTv.setOnClickListener { showSnackBarWithMessage(getString(R.string.iso_tip, singlePhoto?.exif?.iso)) }
        colorTv.setOnClickListener { showSnackBarWithMessage(getString(R.string.color_tip, singlePhoto?.color)) }

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
            val snackBar = Snackbar.make(bottomSheet, getString(R.string.img_downloaded), Snackbar.LENGTH_LONG)
                    .setActionTextColor(ContextCompat.getColor(context, R.color.Link_Water))
                    .setAction("Open Image", {startActivity(intentForFile)})
            (snackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
            snackBar.show()
        }
    }

}
