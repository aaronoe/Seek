package de.aaronoe.seek.ui.userdetail.likes

import android.util.Log
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.mainlist.ListContract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by private on 22.06.17.
 *
 */
class UserLikedPhotosPresenter(val view: ListContract.View,
                               val apiService: UnsplashInterface,
                               val clientId: String,
                               val username: String) : ListContract.Presenter {

    override fun downloadPhotos(page: Int, resultsPerPage: Int) {

        view.showLoading()
        val call = apiService.getLikesForUser(username, "latest", resultsPerPage, page)

        call.enqueue(object  : Callback<List<PhotosReply>> {
            override fun onResponse(p0: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>?) {
                if (response == null || response.body() == null) {
                    view.showError()
                    return
                }
                view.showImages(response.body())
            }

            override fun onFailure(p0: Call<List<PhotosReply>>?, p1: Throwable?) {
                view.showError()
            }
        })

    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        val call = apiService.getLikesForUser(username, "latest", resultsPerPage, page)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(p0: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                if (response.body() == null) {
                    return
                }
                view.addMoreImagesToList(response.body())
            }

            override fun onFailure(p0: Call<List<PhotosReply>>?, p1: Throwable?) {
                //view.showError()
            }
        })

    }
}