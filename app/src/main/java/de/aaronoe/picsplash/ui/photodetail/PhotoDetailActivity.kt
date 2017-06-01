package de.aaronoe.picsplash.ui.photodetail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flipboard.bottomsheet.BottomSheetLayout
import com.flipboard.bottomsheet.commons.IntentPickerSheetView
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.util.DisplayUtils
import de.aaronoe.picsplash.util.bindView
import de.hdodenhof.circleimageview.CircleImageView




class PhotoDetailActivity : AppCompatActivity(), DetailContract.View {

    lateinit var photo: PhotosReply
    lateinit var presenter : DetailPresenterImpl
    val photoImageView : ImageView by bindView(R.id.image_item_iv)
    val userProfileView : CircleImageView by bindView(R.id.detail_author_image)
    val publishedOnView : TextView by bindView(R.id.detail_publish)
    val userNameView : TextView by bindView(R.id.author_name_tv)
    val toolbar: Toolbar by bindView(R.id.detailpage_toolbar)
    val bottomSheet : BottomSheetLayout by bindView(R.id.detail_bottom_sheet)
    val sharePane : LinearLayout by bindView(R.id.share_pane)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Make notification bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        photo = intent.getParcelableExtra(getString(R.string.photo_detail_key))

        presenter = DetailPresenterImpl(this, this, photo)

        loadImage()
        setUpInfo()

        sharePane.setOnClickListener { clickShare() }
    }

    override fun loadImage() {

        supportPostponeEnterTransition()
        Glide.with(this)
                .load(photo.urls.regular).asBitmap()
                .centerCrop()
                .fitCenter()
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

    fun clickShare() {
        if (DisplayUtils.isStoragePermissionGranted(this)) {
            presenter.getIntentForImage((photoImageView.drawable as BitmapDrawable).bitmap)
        }
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
        Glide.with(this).load(photo.user.profileImage.medium).into(userProfileView)
    }

    override fun showShareBottomsheet(shareIntent: Intent) {
        bottomSheet.showWithSheetView(IntentPickerSheetView(this, shareIntent, "Share with...", IntentPickerSheetView.OnIntentPickedListener { activityInfo ->
            bottomSheet.dismissSheet()
            startActivity(activityInfo.getConcreteIntent(shareIntent))
        }))
    }

    override fun showSnackBarShareError(message: String) {
        Snackbar.make(bottomSheet, message, Snackbar.LENGTH_SHORT)
                .setAction("Try again", { clickShare() }).show()
    }
}
