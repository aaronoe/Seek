package de.aaronoe.seek.ui.userdetail.photos

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
class UserPhotosPresenter(val view: ListContract.View,
                          val apiService: UnsplashInterface,
                          val clientId: String,
                          val username: String) : ListContract.Presenter {

    override fun downloadPhotos(page: Int, resultsPerPage: Int) {

        view.showLoading()
        val call = apiService.getPhotosForUser(username, clientId, "latest", resultsPerPage, page)

        call.enqueue(object  : Callback<List<PhotosReply>> {
            override fun onResponse(p0: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                view.showImages(response.body())
            }

            override fun onFailure(p0: Call<List<PhotosReply>>?, p1: Throwable?) {
                view.showError()
            }
        })

    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        val call = apiService.getPhotosForUser(username, clientId, "latest", resultsPerPage, page)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(p0: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                view.addMoreImagesToList(response.body())
            }

            override fun onFailure(p0: Call<List<PhotosReply>>?, p1: Throwable?) {
                //view.showError()
            }
        })

    }
}
