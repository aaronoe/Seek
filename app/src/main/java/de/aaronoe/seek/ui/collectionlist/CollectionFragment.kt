package de.aaronoe.seek.ui.collectionlist

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
import com.yarolegovich.discretescrollview.DiscreteScrollView
import de.aaronoe.seek.BuildConfig
import de.aaronoe.seek.R
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.collectiondetail.CollectionDetailActivity
import de.aaronoe.seek.ui.search.collections.CollectionSearchPresenterImpls
import de.aaronoe.seek.ui.userdetail.UserDetailActivity
import de.aaronoe.seek.ui.userdetail.collections.UserCollectionsPresenter
import de.hdodenhof.circleimageview.CircleImageView
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
    var currentPosition = 1
    lateinit var query: String
    var presenterMode = -1

    @Inject
    lateinit var apiService : UnsplashInterface
    @Inject
    lateinit var sharedPrefs : SharedPreferences


    companion object {
        val key_mode = "KEY_MODE"
        val key_query = "KEY_QUERY"

        val MODE_SEARCH = 1
        val MODE_LIST = 2
        val MODE_USER = 3

        fun createFragment(mode: Int, query: String) : CollectionFragment {
            val bundle = Bundle()
            bundle.putString(key_query, query)
            bundle.putInt(key_mode, mode)

            val fragment = CollectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            query = bundle.getString(key_query)
            presenterMode = bundle.getInt(key_mode)
        }
    }

    fun initPresenter() {

        when (presenterMode) {
            MODE_LIST -> presenter = CollectionPresenterImpl(this, apiService, BuildConfig.UNSPLASH_API_KEY)
            MODE_SEARCH -> presenter = CollectionSearchPresenterImpls(this, apiService, BuildConfig.UNSPLASH_API_KEY, query)
            MODE_USER -> presenter = UserCollectionsPresenter(this, apiService, BuildConfig.UNSPLASH_API_KEY, query)
        }

        presenter.downloadCollections(1, 30, true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

        (activity?.application as SplashApp).netComponent?.inject(this)
        retainInstance = true

        currentOverlayColor = ContextCompat.getColor(inflater.context, R.color.galleryCurrentItemOverlay)
        overlayColor = ContextCompat.getColor(inflater.context, R.color.galleryItemOverlay)

        photoRv = view?.findViewById<DiscreteScrollView>(R.id.featured_rv) as DiscreteScrollView
        errorTv = view.findViewById<TextView>(R.id.error_tv) as TextView
        loadingPb = view.findViewById<ProgressBar>(R.id.loading_pb) as ProgressBar

        evaluator = ArgbEvaluator()
        adapter = CollectionAdapter(this, sharedPrefs)
        photoRv.adapter = adapter
        
        photoRv.addScrollListener(this)
        photoRv.addOnItemChangedListener(this)

        readBundle(arguments)
        initPresenter()

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
        if (position < 0 || adapter.itemCount == 0) return
        photoRv.smoothScrollToPosition(position)
    }

    override fun deleteCurrentItem() {
        adapter.deleteItemAtPosition(currentPosition)
    }

    override fun onClickCollection(collection: Collection?, authorImageView: CircleImageView, authorNameTextView: TextView?) {
        val intent = Intent(activity, CollectionDetailActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_collection), collection)
        val options = activity?.parent?.let {
            ActivityOptionsCompat.
                makeSceneTransitionAnimation(it, authorImageView, getString(R.string.collection_photo_transition_key))
        }
        if (activity is UserDetailActivity) {
            Log.e("Start activity for rest", " - ")
            startActivityForResult(intent, 80, options?.toBundle())
        } else {
            startActivity(intent, options?.toBundle())
        }
    }

    override fun onScroll(currentPosition: Float, currentHolder: CollectionAdapter.CollectionViewHolder, newCurrent: CollectionAdapter.CollectionViewHolder) {
        val position = Math.abs(currentPosition)
        currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor))
        newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor))
    }

    override fun onCurrentItemChanged(viewHolder: CollectionAdapter.CollectionViewHolder?, position: Int) {
        viewHolder?.setOverlayColor(currentOverlayColor)

        currentPosition = position

        if (canDownloadMore && (adapter.itemCount - position) < 15) {
            canDownloadMore = false
            presenter.downloadCollections(nextPage, 30, false)
        }
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }
}