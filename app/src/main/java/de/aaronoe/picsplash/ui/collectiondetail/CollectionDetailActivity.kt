package de.aaronoe.picsplash.ui.collectiondetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.model.collections.Collection
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.util.bindView
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import javax.inject.Inject

class CollectionDetailActivity : AppCompatActivity(), CollectionDetailContract.View {

    val toolbar : Toolbar by bindView(R.id.toolbar_collection_detail)
    val progressBar : ProgressBar by bindView(R.id.collection_detail_pb)
    val errorTv : TextView by bindView(R.id.collection_detail_error_tv)
    val collectionRv : RecyclerView by bindView(R.id.collection_detail_rv)
    val collectionNameTv : TextView by bindView(R.id.collection_name_tv)
    val collectionDescriptionTv : TextView by bindView(R.id.collection_description_tv)
    val collectionUsernameTv : TextView by bindView(R.id.collection_user_tv)
    val userPhotoIv : CircleImageView by bindView(R.id.collection_user_iv)

    lateinit var collection : Collection
    lateinit var presenter : CollectionDetailPresenterImpl
    @Inject
    lateinit var apiService : UnsplashInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        (application as SplashApp).netComponent.inject(this)
        ButterKnife.bind(this)
        supportPostponeEnterTransition()
        presenter = CollectionDetailPresenterImpl(apiService, this, this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(getString(R.string.intent_key_collection))) {
            collection = intent.getParcelableExtra(getString(R.string.intent_key_collection))
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
        collectionRv.visibility = View.VISIBLE
        errorTv.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
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
}
