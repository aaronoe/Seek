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
import android.widget.Toast
import com.yarolegovich.discretescrollview.DiscreteScrollView
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.photodetail.PhotoDetailActivity
import javax.inject.Inject

class FeaturedFragment(var filter: String, var curated: String) : Fragment(), ListContract.View,
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

    @Inject
    lateinit var apiService : UnsplashInterface
    @Inject
    lateinit var sharedPrefs : SharedPreferences

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.activity_main, container, false)

        Log.e("onCreateView:", " FeaturedFragment")

        (activity.application as SplashApp).netComponent?.inject(this)

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

        presenter = FeaturedPresenterImpl(this, apiService, curated)
        presenter.downloadPhotos(1, 30, filter)
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

        Log.e("onCurrentItemChanged", "  - Position : " + position)
        Log.e("onCurrentItemChanged", "  - delta : " + (adapter.itemCount - position))
        if (canDownloadMore &&(adapter.itemCount - position) < 15) {
            canDownloadMore = false
            presenter.downloadMorePhotos(nextPage, 30, filter)
        }
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }

    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
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
