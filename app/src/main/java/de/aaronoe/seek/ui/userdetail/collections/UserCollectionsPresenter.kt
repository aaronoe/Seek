package de.aaronoe.seek.ui.userdetail.collections

import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.collectionlist.CollectionContract
import de.aaronoe.seek.util.subscribeDefault
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

        apiService.getCollectionsForUser(username, resultsPerPage, page)
                .subscribeDefault(onSuccess = {
                    if (firstLoad) {
                        view.showImages(it)
                    } else {
                        view.addMoreImagesToList(it)
                    }
                }, onError = {
                    if (firstLoad) {
                        view.showError()
                    }
                })
    }
}
