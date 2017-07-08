package de.aaronoe.seek.ui.mainlist

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import com.sackcentury.shinebuttonlib.ShineButton
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.singleItem.SinglePhoto
import de.aaronoe.seek.data.remote.UnsplashInterface
import okhttp3.ResponseBody
import org.jetbrains.anko.layoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 30.05.17.
 *
 */
class PhotoListPresenterImpl(val view: ListContract.View,
                             val apiService: UnsplashInterface,
                             var curated: String,
                             var filter: String,
                             val context: Context) : ListContract.Presenter {


    override fun downloadPhotos(page: Int, resultsPerPage: Int) {
        view.showLoading()

        val call: Call<List<PhotosReply>> = apiService.getPhotos(
                curated,
                resultsPerPage,
                page, filter)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>?) {
                if (response?.body() == null) {
                    view.showError()
                    return
                }
                view.showImages(response.body())
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {
                view.showError()
            }

        })
    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        val call: Call<List<PhotosReply>> = apiService.getPhotos(
                curated,
                resultsPerPage,
                page, filter)

        call.enqueue(object: Callback<List<PhotosReply>> {
            override fun onResponse(call: Call<List<PhotosReply>>?, response: Response<List<PhotosReply>>?) {
                if (response?.body() == null) return

                view.addMoreImagesToList(response.body())
            }

            override fun onFailure(call: Call<List<PhotosReply>>?, t: Throwable?) {

            }

        })
    }

}