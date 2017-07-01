package de.aaronoe.seek.ui.search.collections

import de.aaronoe.seek.data.model.collectionsearch.CollectionSearchReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.collectionlist.CollectionContract
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

        val call = apiService.searchForCollections(query, resultsPerPage, page)

        call.enqueue(object: Callback<CollectionSearchReply> {
            override fun onResponse(p0: Call<CollectionSearchReply>?, response: Response<CollectionSearchReply>?) {
                if (response ==  null || response.body() == null) {
                    if (firstLoad) {
                        view.showError()
                    }
                    return
                }

                if (firstLoad) {
                    view.showImages(response.body().results)
                } else {
                    view.addMoreImagesToList(response.body().results)
                }
            }

            override fun onFailure(p0: Call<CollectionSearchReply>?, p1: Throwable?) {
                if (firstLoad) {
                    view.showError()
                }
            }
        })

    }

}