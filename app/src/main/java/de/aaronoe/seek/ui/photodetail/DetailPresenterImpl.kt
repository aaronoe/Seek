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
            view.showSnackBarWithMessage(context.getString(R.string.no_share))
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

    override fun likePicture(id: String) {
        val call = apiService.likePicture(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(p0: Call<ResponseBody>?, p1: Response<ResponseBody>?) {
                view.showSnackBarWithMessage(context.getString(R.string.liked_this_image))
            }

            override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {
            }
        })
    }

    override fun dislikePicture(id: String) {
        val call = apiService.dislikePicture(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(p0: Call<ResponseBody>?, p1: Response<ResponseBody>?) {
                view.showSnackBarWithMessage(context.getString(R.string.liked_this_image))
            }

            override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {
            }
        })
    }

    override fun addPhotoToCollections(username: String, id: String) {

        val call = apiService.getCollectionsForUser(username, 30, 1)
        call.enqueue(object : Callback<List<Collection>>{
            override fun onFailure(p0: Call<List<Collection>>?, p1: Throwable?) {
                view.showSnackBarWithMessage(context.getString(R.string.could_not_load_collections))
            }

            override fun onResponse(p0: Call<List<Collection>>?, response: Response<List<Collection>>?) {
                if (response == null  || response.body() == null || response.body().isEmpty()) {
                    view.showSnackBarWithMessage(context.getString(R.string.could_not_load_collections))
                    return
                }
                showSelectionDialog(response.body(), id)
            }
        })


    }

    private fun showSelectionDialog(collections: List<Collection>, photoId : String) {
        Log.e("showSelectionDialog", " - " + collections.size)
        val mSelectedItems = ArrayList<Int>()  // Where we track the selected items
        val builder = AlertDialog.Builder(context)
        val options = arrayOfNulls<String>(collections.size)
        var i = 0
        collections.forEach {
            options[i] = it.title
            i++
        }

        builder.setTitle(context.getString(R.string.choose_collections))
                .setMultiChoiceItems(options, null,
                        { _, which, isChecked ->

                            if (isChecked) {
                                mSelectedItems.add(collections[which].id)
                            } else if (mSelectedItems.contains(collections[which].id)) {
                                mSelectedItems.remove(collections[which].id)
                            }
                        }
                )
                .setPositiveButton("Add", { _, _ ->
                    addPhotoToCollections(mSelectedItems, photoId)
                })
                .setNeutralButton("Create New", { _, _ ->
                    addPhotoToCollections(mSelectedItems, photoId)
                    createNewCollection(photoId)
                })
                .setNegativeButton("Cancel", { dialogInterface, _ -> dialogInterface.dismiss() })


        view.showDialog(builder.create())
    }


    private fun createNewCollection(photoId: String) {

        val builder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        val contentView = inflater.inflate(R.layout.new_collection_dialog, null)

        builder.setTitle(R.string.create_new_collection)
                .setView(contentView)

        val nameText = contentView.findViewById(R.id.dialog_input_name) as EditText
        val descriptionText = contentView.findViewById(R.id.dialog_input_description) as EditText
        val privateCheck = contentView.findViewById(R.id.dialog_checkbox) as CheckBox

        builder.setPositiveButton("Ok", null)
        val buildDialog = builder.create()


        buildDialog.setOnShowListener {
            val createButton = (buildDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            createButton.setOnClickListener {

                val name = nameText.text.toString()

                if (name.isEmpty()) {
                    view.showSnackBarWithMessage("Please enter a valid name")
                    return@setOnClickListener
                }

                val description = descriptionText.text.toString()
                val private = privateCheck.isEnabled

                val call = apiService.createCollections(name, description, private)
                call.enqueue(object : Callback<Collection> {
                    override fun onFailure(p0: Call<Collection>?, p1: Throwable?) {
                        view.showSnackBarWithMessage("Could not create new Collection")
                    }

                    override fun onResponse(p0: Call<Collection>?, response: Response<Collection>?) {
                        if (response == null || response.body() == null) {
                            view.showSnackBarWithMessage("Could not create new Collection")
                            return
                        }
                        addPhotoToCollections(listOf(response.body().id), photoId)
                        view.showSnackBarWithMessage("Created new collection: $name")
                    }
                })
                buildDialog.dismiss()
            }
        }

        buildDialog.show()

    }

    private fun addPhotoToCollections(collectionIds : List<Int>, photoId: String) {

        collectionIds.forEach {
            val call = apiService.addPhotoToCollection(it, photoId)
            call.enqueue(object : Callback<ResponseBody>{
                override fun onResponse(p0: Call<ResponseBody>?, p1: Response<ResponseBody>?) {
                    view.showSnackBarWithMessage(context.getString(R.string.photo_added_to_collectionn))
                }

                override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {
                    view.showSnackBarWithMessage(context.getString(R.string.could_not_add_to_collection))
                }
            })
        }

    }

    override fun getDetailsForPhoto() {
        val call = apiService.getPhotoById(photo.id)
        view.showLoading()
        call.enqueue(object : Callback<SinglePhoto> {
            override fun onResponse(call: Call<SinglePhoto>?, response: Response<SinglePhoto>?) {
                if (response == null || response.body() == null) {
                    view.showDownloadError()
                    return
                }
                view.hideLoading()
                view.showDetailPane(response.body())
            }
            override fun onFailure(call: Call<SinglePhoto>?, t: Throwable?) {
                view.hideMetaPane()
                view.showSnackBarWithMessage(context.getString(R.string.no_meta_data))
            }

        })
    }

}