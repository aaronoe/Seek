package de.aaronoe.picsplash.ui.mainlist

import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 30.05.17.
 *
 */
class FeaturedPresenterImpl(val view: FeaturedFragment,
                            val apiService: UnsplashInterface,
                            val curated: String) : ListContract.Presenter {


    val clientId = view.getString(R.string.client_id)

    override fun downloadPhotos(page: Int, resultsPerPage: Int, filter: String) {
        view.showLoading()

        val call: Call<List<PhotosReply>> = apiService.getPhotos(
                curated, clientId,
                resultsPerPage,
                page, filter)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                view.showImages(response.body())
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {
                view.showError()
            }

        })
    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int, filter: String) {
        val call: Call<List<PhotosReply>> = apiService.getPhotos(
                curated, clientId,
                resultsPerPage,
                page, filter)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                view.addMoreImagesToList(response.body())
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {

            }

        })
    }

    override fun showToast(text: String) {
        view.makeToast(text)
    }


}