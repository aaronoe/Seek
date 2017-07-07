package de.aaronoe.seek.ui.userdetail.photos

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import com.sackcentury.shinebuttonlib.ShineButton
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.mainlist.ListContract
import okhttp3.ResponseBody
import org.jetbrains.anko.layoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by private on 22.06.17.
 *
 */
class UserPhotosPresenter(val view: ListContract.View,
                          val apiService: UnsplashInterface,
                          val context: Context,
                          val username: String) : ListContract.Presenter {

    override fun downloadPhotos(page: Int, resultsPerPage: Int) {

        view.showLoading()
        val call = apiService.getPhotosForUser(username, "latest", resultsPerPage, page)

        call.enqueue(object  : Callback<List<PhotosReply>> {
            override fun onResponse(p0: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                view.showImages(response.body())
            }

            override fun onFailure(p0: Call<List<PhotosReply>>?, p1: Throwable?) {
                view.showError()
            }
        })

    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        val call = apiService.getPhotosForUser(username, "latest", resultsPerPage, page)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(p0: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>) {
                view.addMoreImagesToList(response.body())
            }

            override fun onFailure(p0: Call<List<PhotosReply>>?, p1: Throwable?) {
                //view.showError()
            }
        })

    }

    override fun likePicture(photo: PhotosReply) {
        val call = apiService.likePicture(photo.id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(p0: Call<ResponseBody>?, p1: Response<ResponseBody>?) {
                view.showSnackBarWithMessage(context.getString(R.string.liked_this_image))
                photo.likedByUser = true
            }

            override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {
            }
        })
    }

    override fun dislikePicture(photo: PhotosReply) {
        val call = apiService.dislikePicture(photo.id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(p0: Call<ResponseBody>?, p1: Response<ResponseBody>?) {
                view.showSnackBarWithMessage(context.getString(R.string.disliked_photo))
                photo.likedByUser = false
            }

            override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {
            }
        })
    }



    override fun addPhotoToCollections(username: String, id: String, button : ShineButton) {

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
                showSelectionDialog(response.body(), id, button)
            }
        })


    }

    private fun showSelectionDialog(collections: List<Collection>, photoId : String, button : ShineButton) {
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
                .setNegativeButton("Cancel", { dialogInterface, _ ->
                    button.setChecked(false, true)
                    dialogInterface.dismiss() })


        builder.create().show()
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


}
