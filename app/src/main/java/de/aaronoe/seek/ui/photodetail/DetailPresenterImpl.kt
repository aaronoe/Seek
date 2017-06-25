package de.aaronoe.seek.ui.photodetail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import de.aaronoe.seek.BuildConfig
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.singleItem.SinglePhoto
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.util.DisplayUtils
import de.aaronoe.seek.util.PhotoDownloadUtils
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

    val clientId = BuildConfig.UNSPLASH_API_KEY

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
            view.showSnackBarShareError(context.getString(R.string.no_share))
        }
    }

    override fun saveImage() {
        PhotoDownloadUtils.downloadImage(context,
                view,
                photo, PhotoDownloadUtils.TYPE_DOWNLOAD)
    }

    override fun setImageAsWallpaper() {
        PhotoDownloadUtils.downloadImage(context,
                view,
                photo, PhotoDownloadUtils.TYPE_WALLPAPER)
    }


    override fun getDetailsForPhoto() {
        val call = apiService.getPhotoById(photo.id, clientId)
        view.showLoading()
        call.enqueue(object : Callback<SinglePhoto> {
            override fun onResponse(call: Call<SinglePhoto>?, response: Response<SinglePhoto>) {
                view.hideLoading()
                view.showDetailPane(response.body())
            }
            override fun onFailure(call: Call<SinglePhoto>?, t: Throwable?) {
                view.hideMetaPane()
                view.showSnackBarShareError(context.getString(R.string.no_meta_data))
            }

        })
    }

}