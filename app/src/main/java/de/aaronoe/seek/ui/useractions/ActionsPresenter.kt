package de.aaronoe.seek.ui.useractions

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
import de.aaronoe.seek.util.subscribeDefault
import okhttp3.ResponseBody
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.layoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by private on 7/7/17.
 *
 */
class ActionsPresenter(val apiService : UnsplashInterface,
                       val context : Context,
                       val view : ActionsContract.View) : ActionsContract.Presenter {

    override fun likePicture(photo: PhotosReply) {
        apiService.likePicture(photo.id)
                .subscribeDefault(onSuccess = {
                    view.showSnackBarWithMessage(context.getString(R.string.liked_this_image))
                    photo.likedByUser = true
                }, onError = {})
    }

    override fun dislikePicture(photo: PhotosReply) {
        val call = apiService.dislikePicture(photo.id)
                .subscribeDefault(onSuccess = {
                    view.showSnackBarWithMessage(context.getString(R.string.disliked_photo))
                    photo.likedByUser = false
                }, onError = {})
    }



    override fun addPhotoToCollections(username: String, id: String, button : ShineButton) {
        apiService.getCollectionsForUser(username, 30, 1)
                .subscribeDefault(onSuccess = {
                    showSelectionDialog(it, id, button)
                }, onError = {
                    view.showSnackBarWithMessage(context.getString(R.string.could_not_load_collections))
                })
    }

    private fun showSelectionDialog(collections: List<Collection>, photoId : String, button : ShineButton) {
        Log.e("showSelectionDialog", " - " + collections.size)
        val mSelectedItems = ArrayList<Int>()  // Where we track the selected items
        val builder = AlertDialog.Builder(context)
        val options = arrayOfNulls<String>(collections.size)
        collections.forEachWithIndex { index, collection ->
            options[index] = collection.title
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

        val nameText = contentView.findViewById<EditText>(R.id.dialog_input_name) as EditText
        val descriptionText = contentView.findViewById<EditText>(R.id.dialog_input_description) as EditText
        val privateCheck = contentView.findViewById<CheckBox>(R.id.dialog_checkbox) as CheckBox

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

                apiService.createCollections(name, description, private)
                        .subscribeDefault(onSuccess = {
                            addPhotoToCollections(listOf(it.id), photoId)
                            view.showSnackBarWithMessage("Created new collection: $name")
                        }, onError = {
                            view.showSnackBarWithMessage("Could not create new Collection")
                        })
                buildDialog.dismiss()
            }
        }

        buildDialog.show()

    }

    private fun addPhotoToCollections(collectionIds : List<Int>, photoId: String) {

        collectionIds.forEach {
            apiService.addPhotoToCollection(it, photoId)
                    .subscribeDefault(onSuccess = {
                        view.showSnackBarWithMessage(context.getString(R.string.photo_added_to_collectionn))
                    }, onError = {
                        view.showSnackBarWithMessage(context.getString(R.string.could_not_add_to_collection))
                    })
        }

    }
}