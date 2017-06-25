package de.aaronoe.seek.ui.collectionlist

import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 05.06.17.
 *
 */
class CollectionPresenterImpl(val view: CollectionFragment,
                              val apiService: UnsplashInterface,
                              val clientId: String) : CollectionContract.Presenter {


    override fun downloadCollections(page: Int, resultsPerPage: Int, firstLoad: Boolean) {

        if (firstLoad) {
            view.showLoading()
        }

        val call: Call<List<Collection>> = apiService.getCollections(page, resultsPerPage, clientId)

        call.enqueue(object: Callback<List<Collection>> {

            override fun onResponse(call: Call<List<Collection>>?, response: Response<List<Collection>>) {
                if (firstLoad) {
                    view.showImages(response.body())
                } else {
                    view.addMoreImagesToList(response.body())
                }
            }

            override fun onFailure(call: Call<List<Collection>>?, t: Throwable?) {
                if (firstLoad) {
                    view.showError()
                }
            }
        })
    }

}