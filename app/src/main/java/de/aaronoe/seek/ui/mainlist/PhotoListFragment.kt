package de.aaronoe.seek.ui.mainlist

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.sackcentury.shinebuttonlib.ShineButton
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.aaronoe.seek.R
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.auth.AuthManager
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.login.LoginActivity
import de.aaronoe.seek.ui.photodetail.PhotoDetailActivity
import de.aaronoe.seek.ui.search.photos.PhotoSearchPresenter
import de.aaronoe.seek.ui.useractions.ActionsContract
import de.aaronoe.seek.ui.useractions.ActionsPresenter
import de.aaronoe.seek.ui.userdetail.likes.UserLikedPhotosPresenter
import de.aaronoe.seek.ui.userdetail.photos.UserPhotosPresenter
import javax.inject.Inject


open class PhotoListFragment : Fragment(),
        ListContract.View,
        ActionsContract.View,
        ImageAdapter.onImageClickListener,
        DiscreteScrollView.ScrollListener<ImageAdapter.ImageViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ImageAdapter.ImageViewHolder> {

    lateinit var photoRv : DiscreteScrollView
    lateinit var errorTv : TextView
    lateinit var listContainer : FrameLayout
    lateinit var loadingPb : ProgressBar
    lateinit var presenter : ListContract.Presenter
    lateinit var actionsPresenter : ActionsContract.Presenter
    lateinit var adapter : ImageAdapter
    lateinit var evaluator: ArgbEvaluator
    var overlayColor : Int = 0
    var currentOverlayColor : Int = 0
    var currentViewHolder : ImageAdapter.ImageViewHolder? = null

    // To be used for endless scrolling
    var canDownloadMore = false
    var nextPage = 1
    var currentPosition = 1

    var presenterMode = -1
    lateinit var filter : String
    lateinit var curated : String
    lateinit var query : String

    companion object {
        val key_mode = "KEY_MODE"
        val key_filter = "KEY_FILTER"
        val key_curated = "KEY_CURATED"
        val key_query = "KEY_QUERY"

        val MODE_SEARCH = 1
        val MODE_LIST = 2
        val MODE_USER_PHOTOS = 3
        val MODE_USER_LIKES = 4

        fun createFragment(mode : Int, filter: String, curated: String, query: String) : PhotoListFragment {
            val bundle = Bundle()
            bundle.putInt(key_mode, mode)
            bundle.putString(key_filter, filter)
            bundle.putString(key_curated, curated)
            bundle.putString(key_query, query)

            val fragment = PhotoListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var apiService : UnsplashInterface
    @Inject
    lateinit var sharedPrefs : SharedPreferences
    lateinit var authManager : AuthManager

    fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            presenterMode = bundle.getInt(key_mode)
            filter = bundle.getString(key_filter)
            curated = bundle.getString(key_curated)
            query = bundle.getString(key_query)
        }
    }

    override fun onClickLike(photo: PhotosReply?, button: ShineButton) {
        if (!authManager.loggedIn) {
            LovelyStandardDialog(activity)
                    .setMessage("You have to log into your Unsplash account to use this feature")
                    .setTopColorRes(R.color.colorPrimaryDark)
                    .setIcon(R.drawable.ic_person_pin_white_36dp)
                    .setPositiveButton("Login", { startActivity(Intent(activity, LoginActivity::class.java)) })
                    .setNegativeButton("Close", null)
                    .show()
            return
        }

        if (photo == null) return
        if (button.isChecked) {
            actionsPresenter.likePicture(photo)
        } else {
            actionsPresenter.dislikePicture(photo)
        }
    }

    override fun onClickAdd(photo: PhotosReply?, button: ShineButton) {
        if (!authManager.loggedIn) {
            LovelyStandardDialog(activity)
                    .setMessage("You have to log into your Unsplash account to use this feature")
                    .setTopColorRes(R.color.colorPrimaryDark)
                    .setIcon(R.drawable.ic_person_pin_white_36dp)
                    .setPositiveButton("Login", { startActivity(Intent(activity, LoginActivity::class.java)) })
                    .setNegativeButton("Close", null)
                    .show()
            return
        }

        if (photo == null) return
        if (button.isChecked) {
            actionsPresenter.addPhotoToCollections(authManager.userName, photo.id, button)
        } else {
        }
    }

    fun initPresenter() {

        val context = this.context?.applicationContext ?: return

        when (presenterMode) {
            MODE_SEARCH -> presenter = PhotoSearchPresenter(this, apiService, context, query)
            MODE_LIST -> presenter = PhotoListPresenterImpl(this, apiService, curated, filter, context)
            MODE_USER_PHOTOS -> presenter = UserPhotosPresenter(this, apiService, context, query)
            MODE_USER_LIKES -> presenter = UserLikedPhotosPresenter(this, apiService, context, query)
        }

        actionsPresenter = ActionsPresenter(apiService, context, this)
        presenter.downloadPhotos(1, 30)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

        (activity?.application as SplashApp).netComponent?.inject(this)
        authManager = (activity?.application as SplashApp).authManager

        retainInstance = true

        currentOverlayColor = ContextCompat.getColor(inflater.context, R.color.galleryCurrentItemOverlay)
        overlayColor = ContextCompat.getColor(inflater.context, R.color.galleryItemOverlay)

        photoRv = view?.findViewById<DiscreteScrollView>(R.id.featured_rv) as DiscreteScrollView
        errorTv = view.findViewById<TextView>(R.id.error_tv) as TextView
        loadingPb = view.findViewById<ProgressBar>(R.id.loading_pb) as ProgressBar
        listContainer = view.findViewById<FrameLayout>(R.id.list_container) as FrameLayout

        evaluator = ArgbEvaluator()
        adapter = ImageAdapter(this, sharedPrefs, authManager)
        photoRv.adapter = adapter
        photoRv.addScrollListener(this)
        photoRv.addOnItemChangedListener(this)

        readBundle(arguments)
        initPresenter()
        return view
    }

    /**
     * This function is called when the first batch of images is loaded
     */
    override fun showImages(imageList: List<PhotosReply>) {
        photoRv.visibility = View.VISIBLE
        errorTv.visibility = View.INVISIBLE
        loadingPb.visibility = View.INVISIBLE

        adapter.setPhotosReplyList(imageList)
        photoRv.scrollToPosition(1)

        canDownloadMore = true
        nextPage = 2
    }

    /**
     * This function is called after another page of images has been loaded
     * it adds the new images to the adapter
     */
    override fun addMoreImagesToList(otherList: List<PhotosReply>) {
        if (otherList.isNotEmpty()) {
            adapter.addMoreItemsToList(otherList)
            nextPage++
            canDownloadMore = true
        }
    }

    override fun showSnackBarWithMessage(message: String) {
        val snackBar = Snackbar.make(listContainer, message, Snackbar.LENGTH_SHORT).apply {
            setAction(getString(R.string.dismiss), { this.dismiss() })
            setActionTextColor(Color.WHITE)
        }
        (snackBar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun showError() {
        errorTv.visibility = View.VISIBLE
        loadingPb.visibility = View.INVISIBLE
        photoRv.visibility = View.INVISIBLE
    }

    override fun showLoading() {
        loadingPb.visibility = View.VISIBLE
        errorTv.visibility = View.INVISIBLE
        photoRv.visibility = View.INVISIBLE
    }

    override fun onScroll(currentPosition: Float, currentHolder: ImageAdapter.ImageViewHolder, newCurrent: ImageAdapter.ImageViewHolder) {
        val position = Math.abs(currentPosition)
        currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor))
        newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor))
    }

    override fun onCurrentItemChanged(viewHolder: ImageAdapter.ImageViewHolder?, position: Int) {
        viewHolder?.setOverlayColor(currentOverlayColor)

        currentViewHolder = viewHolder
        currentPosition = position
        if (canDownloadMore &&(adapter.itemCount - position) < 15) {
            canDownloadMore = false
            presenter.downloadMorePhotos(nextPage, 30)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(getString(R.string.key_item_liked))) {
                val newStatus = data.getBooleanExtra(getString(R.string.key_item_liked_status), false)
                adapter.photosReplyList[currentPosition].likedByUser = newStatus
                currentViewHolder?.shineLikeButton?.setChecked(newStatus, false)
            }
        }
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }

    override fun onClickImage(photo: PhotosReply?, target: ImageView) {
        val detailIntent = Intent(activity, PhotoDetailActivity::class.java)
        detailIntent.putExtra(getString(R.string.photo_detail_key), photo)

        val options = this.activity?.let {
            ActivityOptionsCompat.
                makeSceneTransitionAnimation(it, target, getString(R.string.transition_shared_key))
        }
        startActivityForResult(detailIntent, 80, options?.toBundle())
    }

    override fun moveToPosition(position: Int) {
        if (position < 0) return
        photoRv.smoothScrollToPosition(position)
    }


}
