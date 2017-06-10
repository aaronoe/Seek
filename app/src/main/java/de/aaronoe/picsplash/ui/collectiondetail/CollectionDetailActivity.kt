package de.aaronoe.picsplash.ui.collectiondetail

import android.animation.ArgbEvaluator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yarolegovich.discretescrollview.DiscreteScrollView
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.model.collections.Collection
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.mainlist.ImageAdapter
import de.aaronoe.picsplash.ui.photodetail.PhotoDetailActivity
import de.aaronoe.picsplash.util.bindView
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import javax.inject.Inject

class CollectionDetailActivity : AppCompatActivity(),
        CollectionDetailContract.View,
        ImageAdapter.onImageClickListener,
        DiscreteScrollView.ScrollListener<ImageAdapter.ImageViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ImageAdapter.ImageViewHolder> {

    val toolbar : Toolbar by bindView(R.id.toolbar)
    val progressBar : ProgressBar by bindView(R.id.collection_detail_pb)
    val errorTv : TextView by bindView(R.id.collection_detail_error_tv)
    val collectionRv : DiscreteScrollView by bindView(R.id.collection_detail_rv)
    val collectionNameTv : TextView by bindView(R.id.collection_name_tv)
    val collectionDescriptionTv : TextView by bindView(R.id.collection_description_tv)
    val collectionUsernameTv : TextView by bindView(R.id.collection_user_tv)
    val userPhotoIv : CircleImageView by bindView(R.id.collection_user_iv)

    var overlayColor : Int = 0
    var currentOverlayColor : Int = 0

    // To be used for endless scrolling
    var canDownloadMore = false
    var nextPage = 1

    lateinit var evaluator: ArgbEvaluator
    lateinit var collection : Collection
    lateinit var adapter: ImageAdapter
    lateinit var presenter : CollectionDetailPresenterImpl
    @Inject
    lateinit var apiService : UnsplashInterface
    @Inject
    lateinit var sharedPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        (application as SplashApp).netComponent.inject(this)
        ButterKnife.bind(this)
        supportPostponeEnterTransition()

        presenter = CollectionDetailPresenterImpl(apiService, this, this)
        adapter = ImageAdapter(this, sharedPrefs)
        collectionRv.adapter = adapter

        collectionRv.addScrollListener(this)
        collectionRv.addOnItemChangedListener(this)
        evaluator = ArgbEvaluator()
        currentOverlayColor = ContextCompat.getColor(this, R.color.galleryCurrentItemOverlay)
        overlayColor = ContextCompat.getColor(this, R.color.galleryItemOverlay)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(getString(R.string.intent_key_collection))) {
            collection = intent.getParcelableExtra(getString(R.string.intent_key_collection))
            presenter.downloadImages(collection, 1, true)
            initViews()
        }
    }

    fun initViews() {

        title = ""

        collectionNameTv.text = collection.title
        collectionUsernameTv.text = collection.user.name

        if (collection.description == null || collection.description == "") {
            collectionDescriptionTv.visibility = View.GONE
            collectionNameTv.setPadding(0, 0, 0, 10)

        } else {
            collectionDescriptionTv.text = collection.description
        }

        Glide.with(this)
                .load(collection.user.profileImage.medium)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
                })
                .into(userPhotoIv)
    }

    override fun showImages(photosList: List<PhotosReply>) {
        Log.e("ShowImages: ", "Size: "+photosList.size)
        collectionRv.visibility = View.VISIBLE
        errorTv.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        adapter.setPhotosReplyList(photosList)
        collectionRv.scrollToPosition(1)

        canDownloadMore = true
        nextPage = 2
    }

    override fun showError() {
        collectionRv.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        errorTv.visibility = View.VISIBLE
    }

    override fun showLoading() {
        collectionRv.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        errorTv.visibility = View.INVISIBLE
    }

    override fun addMoreImages(otherList: List<PhotosReply>) {
        if (otherList.isNotEmpty()) {
            adapter.addMoreItemsToList(otherList)
            nextPage++
            canDownloadMore = true
        }
    }

    override fun onClickImage(photo: PhotosReply?, target: ImageView) {
        val detailIntent = Intent(this, PhotoDetailActivity::class.java)
        detailIntent.putExtra(getString(R.string.photo_detail_key), photo)

        val options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, target, getString(R.string.transition_shared_key))
        startActivity(detailIntent, options.toBundle())
    }

    override fun onScroll(currentPosition: Float, currentHolder: ImageAdapter.ImageViewHolder, newCurrent: ImageAdapter.ImageViewHolder) {
        val position = Math.abs(currentPosition)
        currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor))
        newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor))
    }

    override fun onCurrentItemChanged(viewHolder: ImageAdapter.ImageViewHolder?, position: Int) {
        viewHolder?.setOverlayColor(currentOverlayColor)
        if (canDownloadMore &&(adapter.itemCount - position) < 15) {
            canDownloadMore = false
            presenter.downloadImages(collection, nextPage, false)
        }
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }
}
