package de.aaronoe.picsplash.ui.photodetail

import android.content.Intent
import android.graphics.Bitmap
import de.aaronoe.picsplash.data.model.singleItem.SinglePhoto

/**
 * Created by aaron on 31.05.17.
 *
 */

class DetailContract {

    interface View {
        fun loadImage()
        fun setUpInfo()
        fun showShareBottomsheet(shareIntent : Intent)
        fun showSnackBarShareError(message: String)
        fun showDetailPane(singlePhoto: SinglePhoto?)
        fun showLoading()
        fun hideLoading()
        fun hideMetaPane()
        fun showBottomProgressBar()
        fun hideBottomProgressBar()
        fun showDownloadError()
    }

    interface Presenter {
        fun getIntentForImage(image: Bitmap)
        fun saveImage()
        fun setImageAsWallpaper()
        fun getDetailsForPhoto()
    }

}