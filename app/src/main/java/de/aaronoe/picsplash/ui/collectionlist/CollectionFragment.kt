package de.aaronoe.picsplash.ui.collectionlist

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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.yarolegovich.discretescrollview.DiscreteScrollView
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.model.collections.Collection
import de.aaronoe.picsplash.data.model.photosearch.PhotoSearchReply
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.collectiondetail.CollectionDetailActivity
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by aaron on 05.06.17.
 *
 */
class CollectionFragment: Fragment(),
        CollectionContract.View,
        CollectionAdapter.onCollectionClickListener,
        DiscreteScrollView.ScrollListener<CollectionAdapter.CollectionViewHolder>,
        DiscreteScrollView.OnItemChangedListener<CollectionAdapter.CollectionViewHolder>{

    lateinit var photoRv : DiscreteScrollView
    lateinit var errorTv : TextView
    lateinit var loadingPb : ProgressBar
    lateinit var presenter : CollectionContract.Presenter
    lateinit var adapter : CollectionAdapter
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

        (activity.application as SplashApp).netComponent?.inject(this)

        currentOverlayColor = ContextCompat.getColor(context, R.color.galleryCurrentItemOverlay)
        overlayColor = ContextCompat.getColor(context, R.color.galleryItemOverlay)

        photoRv = view?.findViewById(R.id.featured_rv) as DiscreteScrollView
        errorTv = view.findViewById(R.id.error_tv) as TextView
        loadingPb = view.findViewById(R.id.loading_pb) as ProgressBar

        evaluator = ArgbEvaluator()
        adapter = CollectionAdapter(this, sharedPrefs)
        photoRv.adapter = adapter
        
        photoRv.addScrollListener(this)
        photoRv.addOnItemChangedListener(this)

        presenter = CollectionPresenterImpl(this, apiService)
        presenter.downloadCollections(1, 30, true)

        val call = apiService.searchForPhotos("water", getString(R.string.client_id), 30, 1)
        call.enqueue(object: Callback<PhotoSearchReply> {
            override fun onResponse(call: Call<PhotoSearchReply>, response: Response<PhotoSearchReply>) {
                Toast.makeText(context, "" + response.body().results.size, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<PhotoSearchReply>, t: Throwable?) {
                t?.printStackTrace()
            }
        })
        return view
    }

    override fun showImages(collectionList: List<Collection>) {
        photoRv.visibility = View.VISIBLE
        errorTv.visibility = View.INVISIBLE
        loadingPb.visibility = View.INVISIBLE

        adapter.setCollectionList(collectionList)
        photoRv.scrollToPosition(1)

        canDownloadMore = true
        nextPage = 2
    }

    override fun addMoreImagesToList(otherList: List<Collection>) {
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

    override fun moveToPosition(position: Int) {
        if (position < 0) return
        photoRv.smoothScrollToPosition(position)
    }


    override fun onClickCollection(collection: Collection?, authorImageView: CircleImageView?, authorNameTextView: TextView?) {
        val intent = Intent(activity, CollectionDetailActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_collection), collection)
        val options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, authorImageView, getString(R.string.collections_transition_key))
        startActivity(intent, options.toBundle())
    }

    override fun onScroll(currentPosition: Float, currentHolder: CollectionAdapter.CollectionViewHolder, newCurrent: CollectionAdapter.CollectionViewHolder) {
        val position = Math.abs(currentPosition)
        currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor))
        newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor))
    }

    override fun onCurrentItemChanged(viewHolder: CollectionAdapter.CollectionViewHolder?, position: Int) {
        viewHolder?.setOverlayColor(currentOverlayColor)

        Log.e("onCurrentItemChanged", "  - Position : " + position)
        Log.e("onCurrentItemChanged", "  - delta : " + (adapter.itemCount - position))

        if (canDownloadMore && (adapter.itemCount - position) < 15) {
            canDownloadMore = false
            presenter.downloadCollections(nextPage, 30, false)
        }
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }
}