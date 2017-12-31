package de.aaronoe.seek.ui.search.collections

import de.aaronoe.seek.data.model.collectionsearch.CollectionSearchReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.collectionlist.CollectionContract
import de.aaronoe.seek.util.subscribeDefault
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 19.06.17.
 *
 */
class CollectionSearchPresenterImpls(val view: CollectionContract.View,
                                     val apiService: UnsplashInterface,
                                     val clientId: String,
                                     val query: String) : CollectionContract.Presenter {


    override fun downloadCollections(page: Int, resultsPerPage: Int, firstLoad: Boolean) {

        if (firstLoad) {
            view.showLoading()
        }

        apiService.searchForCollections(query, resultsPerPage, page)
                .subscribeDefault(onSuccess = {
                    if (firstLoad) {
                        view.showImages(it.results)
                    } else {
                        view.addMoreImagesToList(it.results)
                    }
                }, onError = {
                    if (firstLoad) {
                        view.showError()
                    }
                })
    }

}