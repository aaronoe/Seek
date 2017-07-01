package de.aaronoe.seek.ui.collectiondetail

import android.content.Context
import android.util.Log
import de.aaronoe.seek.BuildConfig
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
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

    val clientId = BuildConfig.UNSPLASH_API_KEY

    override fun downloadImages(collection: Collection, page: Int, isFirstLoad: Boolean) {
        Log.e("downloadImages ", " - page: " + page)

        if (isFirstLoad) {
            view.showLoading()
        }
        val call = apiService.getPhotosForCollection(collection.id, page, 30)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>, response: Response<List<PhotosReply>>?) {
                if (response == null || response.body() == null) {
                    if (isFirstLoad) {
                        view.showError()
                    }
                    return
                }

                if (isFirstLoad) {
                    view.showImages(response.body())
                } else {
                    view.addMoreImages(response.body())
                }
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {
                if (isFirstLoad) view.showError()
            }
        })
    }
}