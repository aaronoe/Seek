package de.aaronoe.picsplash.ui.photodetail

import android.content.Intent
import android.graphics.Bitmap

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
    }

    interface Presenter {
        fun getIntentForImage(image: Bitmap)
        fun saveImage()
        fun setImageAsWallpaper()
    }

}