package de.aaronoe.seek.ui.search.photos

import de.aaronoe.seek.data.model.photosearch.PhotoSearchReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.mainlist.ListContract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 17.06.17.
 *
 */
class PhotoSearchPresenter(val view: ListContract.View,
                           val apiService: UnsplashInterface,
                           val clientId: String,
                           val query: String) : ListContract.Presenter {

    override fun downloadPhotos(page: Int, resultsPerPage: Int) {
        view.showLoading()
        val call = apiService.searchForPhotos(query, resultsPerPage, page)

        call.enqueue(object: Callback<PhotoSearchReply> {
            override fun onResponse(call: Call<PhotoSearchReply>?, response: Response<PhotoSearchReply>) {
                view.showImages(response.body().results)
            }

            override fun onFailure(p0: Call<PhotoSearchReply>?, p1: Throwable?) {
                view.showError()
            }
        })
    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        val call = apiService.searchForPhotos(query, resultsPerPage, page)

        call.enqueue(object: Callback<PhotoSearchReply> {
            override fun onResponse(call: Call<PhotoSearchReply>?, response: Response<PhotoSearchReply>) {
                view.addMoreImagesToList(response.body().results)
            }

            override fun onFailure(p0: Call<PhotoSearchReply>?, p1: Throwable?) {
                view.showError()
            }
        })
    }
}