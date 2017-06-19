package de.aaronoe.picsplash.ui.mainlist

import de.aaronoe.picsplash.data.model.PhotosReply

/**
 * Created by aaron on 29.05.17.
 *
 */
class ListContract {

    interface View {
        fun showImages(imageList: List<PhotosReply>)
        fun addMoreImagesToList(otherList: List<PhotosReply>)
        fun showError()
        fun showLoading()
        fun moveToPosition(position: Int)
    }

    interface Presenter {
        fun downloadPhotos(page: Int, resultsPerPage: Int)
        fun downloadMorePhotos(page: Int, resultsPerPage: Int)
    }

}