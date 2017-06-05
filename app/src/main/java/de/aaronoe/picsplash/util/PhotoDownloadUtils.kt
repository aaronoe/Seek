package de.aaronoe.picsplash.util

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import de.aaronoe.picsplash.R
import de.aaronoe.picsplash.data.model.PhotosReply
import de.aaronoe.picsplash.ui.photodetail.DetailContract
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
                            val myImageFile = File(Environment.getExternalStorageDirectory().absolutePath + separator + "Pictures" + separator + "PicSplash"
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
    }

}