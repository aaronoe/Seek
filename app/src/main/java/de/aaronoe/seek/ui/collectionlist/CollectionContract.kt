package de.aaronoe.seek.ui.collectionlist

import de.aaronoe.seek.data.model.collections.Collection

/**
 * Created by aaron on 05.06.17.
 *
 */
class CollectionContract {

    interface View {
        fun showImages(collectionList: List<Collection>)
        fun addMoreImagesToList(otherList: List<Collection>)
        fun showError()
        fun showLoading()
        fun moveToPosition(position: Int)
    }

    interface Presenter {
        fun downloadCollections(page: Int, resultsPerPage: Int, firstLoad: Boolean)
    }

}