package de.aaronoe.picsplash.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.SplashApp
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.data.model.collections.Collection
import de.aaronoe.picsplash.ui.photodetail.DetailContract
import org.jetbrains.anko.downloadManager
import java.io.File
import java.io.File.separator




/**
 * Created by aaron on 01.06.17.
 *
 */
class PhotoDownloadUtils {

    companion object {

        fun downloadImage(context: Context,
                          listener: DetailContract.View,
                          photo: PhotosReply,
                          type: Int) {

            listener.showBottomProgressBar()

            Glide.with(context)
                    .load(photo.urls.full)
                    .asBitmap()
                    .into(object : SimpleTarget<Bitmap>(photo.width, photo.height) {

                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                            val mFormat = Bitmap.CompressFormat.JPEG
                            val myImageFile = File(Environment
                                    .getExternalStoragePublicDirectory(SplashApp.DOWNLOAD_PATH)
                                    .absolutePath + separator + "Pictures" + separator + "PicSplash"
                                    + separator + photo.id + "_"  + ".jpg")
                            val contentUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", myImageFile)
                            ImageDownloader.writeToDisk(myImageFile, resource, object : ImageDownloader.OnBitmapSaveListener {
                                override fun onBitmapSaved() {
                                    if (type == TYPE_DOWNLOAD) {
                                        Log.e("TYPE DOWNLOAD: " , "Creating intent")
                                        val intent = Intent()
                                        intent.action = Intent.ACTION_VIEW
                                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        intent.setDataAndType(contentUri, "image/*")
                                        Log.e("TYPE DOWNLOAD: " , contentUri.toString())
                                        sendNotification(context, intent)
                                    } else if (type == TYPE_WALLPAPER) {
                                        val intent = WallpaperManager.getInstance(context).getCropAndSetWallpaperIntent(contentUri)
                                        (context as Activity).startActivityForResult(intent, 13451)
                                    }
                                    listener.hideBottomProgressBar()
                                }

                                override fun onBitmapSaveError(error: ImageDownloader.ImageError) {
                                    error.printStackTrace()
                                    listener.hideBottomProgressBar()
                                    listener.showDownloadError()
                                }
                            }, mFormat, true)
                        }
                    })
        }

        val TYPE_DOWNLOAD = 143
        val TYPE_WALLPAPER = 152

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

        fun getIntentForFile(context: Context, fileId: Long) : Intent {
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
                    .setSmallIcon(R.drawable.ic_fiber_smart_record_black_24dp)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(context.getString(R.string.image_downloaded))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)

            val mNotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            mNotificationManager.notify(1, mBuilder.build())

        }

        fun downloadPhoto(c: Context, photo: PhotosReply) {
            val fileId : Long
            val prefManager = PreferenceManager.getDefaultSharedPreferences(c)
            val quality = prefManager.getString(c.getString(R.string.pref_key_download_quality),
                    c.getString(R.string.quality_regular_const))!!
            Log.e("downloadPhoto(): ", " with quality: " + quality)

            val photoUrl = PhotoDownloadUtils.getPhotoLinkForQuality(photo, quality)

            if (!prefManager.contains(photo.id + quality)) {
                val request = DownloadManager.Request(Uri.parse(photoUrl))
                        .setTitle("PicSplash Download")
                        .setDescription("Downloading Photo")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                        .setDestinationInExternalPublicDir(
                                SplashApp.DOWNLOAD_PATH,
                                photo.user.name + "_" + photo.id + "_" + quality)
                request.allowScanningByMediaScanner()

                fileId = c.downloadManager.enqueue(request)

                prefManager.edit().putLong(photo.id + quality, fileId).apply()
            } else {
                fileId = prefManager.getLong(photo.id + quality, -1L)
                // If the file does not exist but is in shared preferences, delete it from shared prefs
                if (!validDownload(c, fileId)) {
                    prefManager.edit().remove(photo.id + quality).apply()
                    downloadPhoto(c, photo)
                    return
                }
                Log.e("downloadPhoto(): ", "Already Downloaded with: " + fileId)
                sendNotificationForId(c, fileId)
            }
        }

        fun getPhotoLinkForQuality(photo: PhotosReply, setting: String) : String {
            when(Integer.parseInt(setting)) {
                10 -> return photo.urls.thumb
                11 -> return photo.urls.small
                12 -> return photo.urls.regular
                13 -> return photo.urls.full
                14 -> return photo.urls.raw
            }
            return photo.urls.regular
        }

        fun getCollectionLinkForQuality(collection: Collection, setting: String) : String {
            when(Integer.parseInt(setting)) {
                10 -> return collection.coverPhoto.urls.thumb
                11 -> return collection.coverPhoto.urls.small
                12 -> return collection.coverPhoto.urls.regular
                13 -> return collection.coverPhoto.urls.full
                14 -> return collection.coverPhoto.urls.raw
            }
            return collection.coverPhoto.urls.regular
        }


        /**
         * Check if download was valid, see issue
         * http://code.google.com/p/android/issues/detail?id=18462
         * @param long1
         * *
         * @return
         */
        private fun validDownload(context: Context, downloadId: Long): Boolean {

            Log.d("validDownload", "Checking download status for id: " + downloadId)

            //Verify if download is a success
            val c = context.downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

            if (c.moveToFirst()) {
                val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    return true //Download is valid, celebrate
                } else {
                    val reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                    Log.d("validDownload", "Download not correct, status [$status] reason [$reason]")
                    return false
                }
            }
            return false
        }


    }

}