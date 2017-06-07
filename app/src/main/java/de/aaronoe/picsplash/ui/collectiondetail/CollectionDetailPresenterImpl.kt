package de.aaronoe.picsplash.ui.collectiondetail

import android.content.Context
import android.util.Log
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.model.collections.Collection
import de.aaronoe.picsplash.data.remote.UnsplashInterface
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

    val clientId = context.getString(R.string.client_id)

    override fun downloadImages(collection: Collection, page: Int, isFirstLoad: Boolean) {
        Log.e("downloadImages ", " - page: " + page)

        if (isFirstLoad) {
            view.showLoading()
        }
        val call = apiService.getPhotosForCollection(collection.id, page, 30, clientId)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>, response: Response<List<PhotosReply>>) {
                if (isFirstLoad) {
                    view.showImages(response.body())
                    Log.e("Onresponse", " - Size: " + response.body().size)
                }
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {
                view.showError()
            }
        })
    }
}