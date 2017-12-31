package de.aaronoe.seek.ui.search.photos

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import com.sackcentury.shinebuttonlib.ShineButton
import de.aaronoe.seek.R
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.photosearch.PhotoSearchReply
import de.aaronoe.seek.data.remote.UnsplashInterface
import de.aaronoe.seek.ui.mainlist.ListContract
import de.aaronoe.seek.util.subscribeDefault
import okhttp3.ResponseBody
import org.jetbrains.anko.layoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by aaron on 17.06.17.
 *
 */
class PhotoSearchPresenter(val view: ListContract.View,
                           val apiService: UnsplashInterface,
                           val context: Context,
                           val query: String) : ListContract.Presenter {

    override fun downloadPhotos(page: Int, resultsPerPage: Int) {
        view.showLoading()
        apiService.searchForPhotos(query, resultsPerPage, page)
                .subscribeDefault(onSuccess = {
                    view.showImages(it.results)
                }, onError = {
                    view.showError()
                })
    }

    override fun downloadMorePhotos(page: Int, resultsPerPage: Int) {
        apiService.searchForPhotos(query, resultsPerPage, page)
                .subscribeDefault(onSuccess = {
                    view.addMoreImagesToList(it.results)
                }, onError = {})
    }


}