package de.aaronoe.picsplash.ui.mainlist

import android.animation.ArgbEvaluator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.model.photos.PhotosReply
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.photodetail.PhotoDetailActivity
import de.aaronoe.picsplash.ui.search.photos.PhotoSearchPresenter
import de.aaronoe.picsplash.ui.userdetail.likes.UserLikedPhotosPresenter
import de.aaronoe.picsplash.ui.userdetail.photos.UserPhotosPresenter
import javax.inject.Inject


open class PhotoListFragment : Fragment(), ListContract.View,
        ImageAdapter.onImageClickListener,
        DiscreteScrollView.ScrollListener<ImageAdapter.ImageViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ImageAdapter.ImageViewHolder> {

    lateinit var photoRv : DiscreteScrollView
    lateinit var errorTv : TextView
    lateinit var loadingPb : ProgressBar
    lateinit var presenter : ListContract.Presenter
    lateinit var adapter : ImageAdapter
    lateinit var evaluator: ArgbEvaluator
    var overlayColor : Int = 0
    var currentOverlayColor : Int = 0

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


    fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            presenterMode = bundle.getInt(key_mode)
            filter = bundle.getString(key_filter)
            curated = bundle.getString(key_curated)
            query = bundle.getString(key_query)
        }
    }

    fun initPresenter() {

        when (presenterMode) {
            MODE_SEARCH -> presenter = PhotoSearchPresenter(this, apiService, getString(R.string.client_id), query)
            MODE_LIST -> presenter = PhotoListPresenterImpl(this, apiService, curated, filter, getString(R.string.client_id))
            MODE_USER_PHOTOS -> presenter = UserPhotosPresenter(this, apiService, getString(R.string.client_id), query)
            MODE_USER_LIKES -> presenter = UserLikedPhotosPresenter(this, apiService, getString(R.string.client_id), query)
        }

        presenter.downloadPhotos(1, 30)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.activity_main, container, false)

        Log.e("photoListFragment - ", "onCreateView")
        (activity.application as SplashApp).netComponent?.inject(this)
        retainInstance = true

        currentOverlayColor = ContextCompat.getColor(context, R.color.galleryCurrentItemOverlay)
        overlayColor = ContextCompat.getColor(context, R.color.galleryItemOverlay)

        photoRv = view?.findViewById(R.id.featured_rv) as DiscreteScrollView
        errorTv = view.findViewById(R.id.error_tv) as TextView
        loadingPb = view.findViewById(R.id.loading_pb) as ProgressBar

        evaluator = ArgbEvaluator()
        adapter = ImageAdapter(this, sharedPrefs)
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

        currentPosition = position
        Log.e("onCurrentItemChanged", "  - Position : " + position)
        Log.e("onCurrentItemChanged", "  - delta : " + (adapter.itemCount - position))
        if (canDownloadMore &&(adapter.itemCount - position) < 15) {
            canDownloadMore = false
            presenter.downloadMorePhotos(nextPage, 30)
        }
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }

    override fun onClickImage(photo: PhotosReply?, target: ImageView) {
        val detailIntent = Intent(activity, PhotoDetailActivity::class.java)
        detailIntent.putExtra(getString(R.string.photo_detail_key), photo)

        val options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, target, getString(R.string.transition_shared_key))
        startActivity(detailIntent, options.toBundle())
    }

    override fun moveToPosition(position: Int) {
        if (position < 0) return
        photoRv.smoothScrollToPosition(position)
    }


}
