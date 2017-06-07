package de.aaronoe.picsplash.ui.collectiondetail

import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.model.collections.Collection

/**
 * Created by aaron on 07.06.17.
 *
 */
class CollectionDetailContract {

    interface View {
        fun showImages(photosList: List<PhotosReply>)
        fun showError()
        fun showLoading()
    }

    interface Presenter {
        fun downloadImages(collection: Collection, page: Int, isFirstLoad: Boolean)
    }

}