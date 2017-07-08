package de.aaronoe.seek.ui.useractions

import com.sackcentury.shinebuttonlib.ShineButton
import de.aaronoe.seek.data.model.photos.PhotosReply

/**
 *
 * Created by private on 7/7/17.
 */
class ActionsContract {

    interface Presenter {
        fun addPhotoToCollections(username: String, id: String, button : ShineButton)
        fun likePicture(photo: PhotosReply)
        fun dislikePicture(photo: PhotosReply)
    }

    interface View {
        fun showSnackBarWithMessage(message : String)
    }

}