package de.aaronoe.seek.ui.photodetail

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AlertDialog
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.data.model.singleItem.SinglePhoto

/**
 * Created by aaron on 31.05.17.
 *
 */

class DetailContract {

    interface View {
        fun loadImage()
        fun setUpInfo()
        fun showShareBottomsheet(shareIntent : Intent)
        fun showSnackBarWithMessage(message: String)
        fun showDetailPane(singlePhoto: SinglePhoto?)
        fun showLoading()
        fun hideLoading()
        fun hideMetaPane()
        fun showBottomProgressBar()
        fun hideBottomProgressBar()
        fun showDownloadError()
        fun showDialog(dialog : AlertDialog)
    }

    interface Presenter {
        fun getIntentForImage(image: Bitmap)
        fun setImageAsWallpaper()
        fun getDetailsForPhoto()
    }

}