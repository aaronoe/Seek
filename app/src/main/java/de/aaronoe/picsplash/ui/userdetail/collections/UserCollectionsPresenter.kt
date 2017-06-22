package de.aaronoe.picsplash.ui.userdetail.collections

import de.aaronoe.picsplash.data.model.collections.Collection
import de.aaronoe.picsplash.data.remote.UnsplashInterface
import de.aaronoe.picsplash.ui.collectionlist.CollectionContract
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

        val call = apiService.getCollectionsForUser(username, clientId, resultsPerPage, page)

        call.enqueue(object : Callback<List<Collection>> {
            override fun onResponse(p0: Call<List<Collection>>?, response: Response<List<Collection>>) {
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
