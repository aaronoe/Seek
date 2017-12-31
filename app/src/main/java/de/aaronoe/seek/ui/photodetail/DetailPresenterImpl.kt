package de.aaronoe.seek.ui.photodetail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import de.aaronoe.seek.BuildConfig
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.singleItem.SinglePhoto
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.util.DisplayUtils
import de.aaronoe.seek.util.PhotoDownloadUtils
import de.aaronoe.seek.util.subscribeDefault
import okhttp3.ResponseBody
import org.jetbrains.anko.layoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by aaron on 01.06.17.
 *
 */
class DetailPresenterImpl(val context : Context,
                          val apiService: UnsplashInterface,
                          val view : DetailContract.View,
                          val photo: PhotosReply) : DetailContract.Presenter {


    override fun getIntentForImage(image: Bitmap) {

        view.showBottomProgressBar()

        val bmpUri = DisplayUtils.getLocalBitmapUri(context, image)
        if (bmpUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.setDataAndType(bmpUri, "image/*")
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            view.hideBottomProgressBar()
            view.showShareBottomsheet(shareIntent)
        } else {
            view.hideBottomProgressBar()
            view.showSnackBarWithMessage(context.getString(R.string.no_share))
        }
    }

    override fun setImageAsWallpaper() {
        PhotoDownloadUtils.downloadImage(context,
                view,
                photo, PhotoDownloadUtils.TYPE_WALLPAPER)
    }


    override fun getDetailsForPhoto() {
        view.showLoading()
        apiService.getPhotoById(photo.id)
                .subscribeDefault(onSuccess = {
                    view.hideLoading()
                    view.showDetailPane(it)
                }, onError = {
                    view.hideMetaPane()
                    view.showSnackBarWithMessage(context.getString(R.string.no_meta_data))
                })
    }

}