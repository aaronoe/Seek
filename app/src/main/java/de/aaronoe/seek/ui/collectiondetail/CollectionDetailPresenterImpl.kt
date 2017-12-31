package de.aaronoe.seek.ui.collectiondetail

import android.content.Context
import android.util.Log
import de.aaronoe.seek.BuildConfig
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
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
        val call = apiService.getPhotosForCollection(collection.id, page, 30)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>, response: Response<List<PhotosReply>>?) {
                if (response?.body() == null) {
                    if (isFirstLoad) {
                        view.showError()
                    }
                    return
                }

                val list = response.body() ?: return

                if (isFirstLoad) {
                    view.showImages(list)
                } else {
                    view.addMoreImages(list)
                }
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {
                if (isFirstLoad) view.showError()
            }
        })
    }

    override fun deleteCollection(collection: Collection) {
        val call = apiService.deleteCollection(collection.id)
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(p0: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                view.onCollectionDeleted()
            }

            override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {
                view.showSnackbarWithMessage(context.getString(R.string.collection_could_not_be_deleted))
            }
        })
    }
}