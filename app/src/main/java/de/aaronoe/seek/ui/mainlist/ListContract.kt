package de.aaronoe.seek.ui.mainlist

import com.sackcentury.shinebuttonlib.ShineButton
import de.aaronoe.seek.data.model.photos.PhotosReply

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
        fun showSnackBarWithMessage(message: String)
        fun moveToPosition(position: Int)
    }

    interface Presenter {
        fun downloadPhotos(page: Int, resultsPerPage: Int)
        fun downloadMorePhotos(page: Int, resultsPerPage: Int)
        fun likePicture(photo: PhotosReply)
        fun addPhotoToCollections(username: String, id: String, button : ShineButton)
        fun dislikePicture(photo: PhotosReply)
    }

}