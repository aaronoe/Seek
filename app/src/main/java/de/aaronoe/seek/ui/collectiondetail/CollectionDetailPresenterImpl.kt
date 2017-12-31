package de.aaronoe.seek.ui.collectiondetail

import android.content.Context
import android.util.Log
import de.aaronoe.seek.BuildConfig
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.util.subscribeDefault
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 07.06.17.
 *
 */
class CollectionDetailPresenterImpl(val apiService: UnsplashInterface,
                                    val view: CollectionDetailContract.View,
                                    val context: Context) : CollectionDetailContract.Presenter {


    override fun downloadImages(collection: Collection, page: Int, isFirstLoad: Boolean) {
        Log.e("downloadImages ", " - page: " + page)

        if (isFirstLoad) {
            view.showLoading()
        }

        apiService.getPhotosForCollection(collection.id, page, 30)
                .subscribeDefault(onSuccess = {
                    if (isFirstLoad) {
                        view.showImages(it)
                    } else {
                        view.addMoreImages(it)
                    }
                }, onError = {
                    if (isFirstLoad) view.showError()
                })

    }

    override fun deleteCollection(collection: Collection) {
        apiService.deleteCollection(collection.id)
                .subscribeDefault(onSuccess = {
                    view.onCollectionDeleted()
                }, onError = {
                    view.showSnackbarWithMessage(context.getString(R.string.collection_could_not_be_deleted))
                })
    }
}