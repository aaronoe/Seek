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
import de.aaronoe.seek.util.subscribeDefault
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

        apiService.getPhotos(
                curated,
                resultsPerPage,
                page, filter)
                .subscribeDefault(onSuccess = {
                    view.showImages(it)
                }, onError = {
                    view.showError()
                })
    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        apiService.getPhotos(
                curated,
                resultsPerPage,
                page, filter)
                .subscribeDefault(onSuccess = {
                    view.addMoreImagesToList(it)
                }, onError = {

                })
    }

}