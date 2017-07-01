package de.aaronoe.seek.ui.userdetail.collections

import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.collectionlist.CollectionContract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by private on 22.06.17.
 *
 *
 */

class UserCollectionsPresenter(val view: CollectionContract.View,
                               val apiService: UnsplashInterface,
                               val clientId: String,
                               val username: String) : CollectionContract.Presenter {

    override fun downloadCollections(page: Int, resultsPerPage: Int, firstLoad: Boolean) {

        if (firstLoad) {
            view.showLoading()
        }

        val call = apiService.getCollectionsForUser(username, resultsPerPage, page)

        call.enqueue(object : Callback<List<Collection>> {
            override fun onResponse(p0: Call<List<Collection>>?, response: Response<List<Collection>>?) {

                if (response == null || response.body() == null) {
                    if (firstLoad) {
                        view.showError()
                    }
                    return
                }

                if (firstLoad) {
                    view.showImages(response.body())
                } else {
                    view.addMoreImagesToList(response.body())
                }
            }

            override fun onFailure(p0: Call<List<Collection>>?, p1: Throwable?) {
                if (firstLoad) {
                    view.showError()
                }
            }
        })

    }
}
