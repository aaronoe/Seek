package de.aaronoe.seek.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.google.firebase.perf.metrics.AddTrace
import de.aaronoe.seek.R
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.photos.PhotoDownloadLink
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.photodetail.DetailContract
import org.jetbrains.anko.downloadManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.File.separator


/**
 * Created by aaron on 01.06.17.
 *
 */
class PhotoDownloadUtils {

    companion object {

        @AddTrace(name = "downloadImage", enabled = true/*Optional*/)
        fun downloadImage(
            context: Context,
            listener: DetailContract.View,
            photo: PhotosReply,
            type: Int,
            apiService: UnsplashInterface
        ) {

            listener.showBottomProgressBar()

            apiService.getPhotoDownloadUrl(photoId = photo.id)
                .enqueue(object : Callback<PhotoDownloadLink> {
                    override fun onFailure(call: Call<PhotoDownloadLink>?, t: Throwable?) {
                        listener.hideBottomProgressBar()
                        listener.showDownloadError()
                    }

                    override fun onResponse(
                        call: Call<PhotoDownloadLink>?,
                        response: Response<PhotoDownloadLink>?
                    ) {
                        if (response?.body() == null) {
                            listener.showDownloadError()
                            listener.hideBottomProgressBar()
                            return
                        }
                        downloadOrSetAsWallpaper(
                            context = context, photo = photo, photoUrl = response.body().url,
                            type = type, listener = listener
                        )
                    }
                })

        }

        private fun downloadOrSetAsWallpaper(
            context: Context,
            photoUrl: String,
            photo: PhotosReply,
            type: Int,
            listener: DetailContract.View
        ) {
            listener.showBottomProgressBar()

            Glide.with(context)
                .load(photoUrl)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>(photo.width, photo.height) {

                    override fun onResourceReady(
                        resource: Bitmap,
                        glideAnimation: GlideAnimation<in Bitmap>?
                    ) {
                        val mFormat = Bitmap.CompressFormat.JPEG
                        val myImageFile = File(
                            Environment
                                .getExternalStoragePublicDirectory(SplashApp.DOWNLOAD_PATH)
                                .absolutePath + separator + "Pictures" + separator + context.getString(
                                R.string.app_name
                            )
                                    + separator + photo.id + "_" + ".jpg"
                        )
                        val contentUri = FileProvider.getUriForFile(
                            context,
                            context.applicationContext.packageName + ".provider",
                            myImageFile
                        )
                        ImageDownloader.writeToDisk(
                            myImageFile,
                            resource,
                            object : ImageDownloader.OnBitmapSaveListener {
                                override fun onBitmapSaved() {
                                    if (type == TYPE_DOWNLOAD) {
                                        val intent = Intent()
                                        intent.action = Intent.ACTION_VIEW
                                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        intent.setDataAndType(contentUri, "image/*")
                                        sendNotification(context, intent)
                                    } else if (type == TYPE_WALLPAPER) {
                                        val intent = WallpaperManager.getInstance(context)
                                            .getCropAndSetWallpaperIntent(contentUri)
                                        (context as Activity).startActivityForResult(intent, 13451)
                                    }
                                    listener.hideBottomProgressBar()
                                }

                                override fun onBitmapSaveError(error: ImageDownloader.ImageError) {
                                    error.printStackTrace()
                                    listener.hideBottomProgressBar()
                                    listener.showDownloadError()
                                }
                            },
                            mFormat,
                            true
                        )
                    }
                })
        }

        const val TYPE_DOWNLOAD = 143
        const val TYPE_WALLPAPER = 152

        fun sendNotification(context: Context, intent: Intent) {

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_fiber_smart_record_black_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.image_downloaded))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            mNotificationManager.notify(1, mBuilder.build())
        }

        fun getIntentForFile(context: Context, fileId: Long): Intent {
            val uri = context.downloadManager.getUriForDownloadedFile(fileId)
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.setDataAndType(uri, "image/*")
            return intent
        }

        fun sendNotificationForId(context: Context, fileId: Long) {

            val intent = getIntentForFile(context, fileId)

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_camera_black_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.image_downloaded))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            mNotificationManager.notify(1, mBuilder.build())

        }

        @AddTrace(name = "downloadPhotoManager", enabled = true)
        fun downloadPhoto(context: Context, photo: PhotosReply, apiService: UnsplashInterface) {
            apiService.getPhotoDownloadUrl(photoId = photo.id)
                .enqueue(object : Callback<PhotoDownloadLink> {
                    override fun onFailure(call: Call<PhotoDownloadLink>?, t: Throwable?) = Unit

                    override fun onResponse(
                        call: Call<PhotoDownloadLink>?,
                        response: Response<PhotoDownloadLink>?
                    ) {
                        if (response?.body() == null) {
                            return
                        }
                        downloadPhotoWithFileManager(
                            context = context, photo = photo, apiService = apiService,
                            downloadUrl = response.body().url
                        )
                    }
                })

        }

        private fun downloadPhotoWithFileManager(
            context: Context,
            photo: PhotosReply,
            downloadUrl: String,
            apiService: UnsplashInterface
        ) {
            val fileId: Long
            val prefManager = PreferenceManager.getDefaultSharedPreferences(context)
            val quality = 1

            if (!prefManager.contains(photo.id + quality)) {
                val request = DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle(context.getString(R.string.seek_download))
                    .setDescription(context.getString(R.string.downloading_photo))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir(
                        SplashApp.DOWNLOAD_PATH,
                        photo.user.name + "_" + photo.id + "_" + quality
                    )
                request.allowScanningByMediaScanner()

                fileId = context.downloadManager.enqueue(request)

                prefManager.edit().putLong(photo.id + quality, fileId).apply()
            } else {
                fileId = prefManager.getLong(photo.id + quality, -1L)
                // If the file does not exist but is in shared preferences, delete it from shared prefs
                if (!validDownload(context, fileId)) {
                    prefManager.edit().remove(photo.id + quality).apply()
                    downloadPhoto(context, photo, apiService)
                    return
                }
                try {
                    (context as DetailContract.View).hideBottomProgressBar()
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }
                sendNotificationForId(context, fileId)
            }
        }

        fun getPhotoLinkForQuality(photo: PhotosReply, setting: String): String {
            when (Integer.parseInt(setting)) {
                10 -> return photo.urls.thumb
                11 -> return photo.urls.small
                12 -> return photo.urls.regular
                13 -> return photo.urls.full
                14 -> return photo.urls.raw
            }
            return photo.urls.regular
        }

        fun getCollectionLinkForQuality(collection: Collection, setting: String): String? {
            if (collection.coverPhoto == null) return null
            when (Integer.parseInt(setting)) {
                10 -> return collection.coverPhoto.urls.thumb
                11 -> return collection.coverPhoto.urls.small
                12 -> return collection.coverPhoto.urls.regular
                13 -> return collection.coverPhoto.urls.full
                14 -> return collection.coverPhoto.urls.raw
            }
            return collection.coverPhoto.urls.regular
        }

        fun downloadImageIntoViewWithAnimation(
            context: Context,
            url: String,
            photo: PhotosReply,
            view: ImageView
        ) {
            Glide.with(context)
                .load(url)
                .asBitmap()
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .into(object : BitmapImageViewTarget(view) {
                    override fun setResource(resource: Bitmap) {
                        // Do bitmap magic here
                        view.setImageBitmap(resource)
                        DisplayUtils.startSaturationAnimation(context, view, 2000)
                    }
                })
        }


        /**
         * Check if download was valid, see issue
         * http://code.google.com/p/android/issues/detail?id=18462
         * *
         * @return
         */
        @AddTrace(name = "validDownload", enabled = true/*Optional*/)
        private fun validDownload(context: Context, downloadId: Long): Boolean {

            //Verify if download is a success
            val c = context.downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

            if (c.moveToFirst()) {
                val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    return true //Download is valid, celebrate
                } else {
                    val reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                    Log.d(
                        "validDownload",
                        "Download not correct, status [$status] reason [$reason]"
                    )
                    return false
                }
            }
            return false
        }


    }

}