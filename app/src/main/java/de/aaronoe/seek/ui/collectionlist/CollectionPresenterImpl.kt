package de.aaronoe.seek.ui.collectionlist

import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.util.subscribeDefault

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

        apiService.getCollections(page, resultsPerPage)
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