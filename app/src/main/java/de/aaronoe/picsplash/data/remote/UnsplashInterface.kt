package de.aaronoe.picsplash.data.remote

import de.aaronoe.picsplash.data.model.PhotosReply
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by aaron on 29.05.17.
 *
 */
interface UnsplashInterface {

    @GET("photos/{curated}")
    fun getPhotos(@Path("curated") curated: String,
                  @Query("client_id") clientId: String,
                  @Query("per_page") per_page: Int,
                  @Query("page") page: Int,
                  @Query("order_by") sortOrder: String) : Call<List<PhotosReply>>

}