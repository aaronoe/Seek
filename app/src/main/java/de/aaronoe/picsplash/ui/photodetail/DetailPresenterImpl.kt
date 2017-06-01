package de.aaronoe.picsplash.ui.photodetail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.util.DisplayUtils




/**
 * Created by aaron on 01.06.17.
 *
 */
class DetailPresenterImpl(val context : Context,
                          val view : DetailContract.View,
                          val photo: PhotosReply) : DetailContract.Presenter {

    override fun getIntentForImage(image: Bitmap) {

        val bmpUri = DisplayUtils.getLocalBitmapUri(context, image)
        if (bmpUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.setDataAndType(bmpUri, "image/*")
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            view.showShareBottomsheet(shareIntent)
        } else {
            view.showSnackBarShareError(context.getString(R.string.no_share))
        }
    }
}