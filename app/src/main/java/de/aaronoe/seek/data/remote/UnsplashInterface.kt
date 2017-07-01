package de.aaronoe.seek.data.remote

import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.collectionsearch.CollectionSearchReply
import de.aaronoe.seek.data.model.photos.User
import de.aaronoe.seek.data.model.photosearch.PhotoSearchReply
import de.aaronoe.seek.data.model.singleItem.SinglePhoto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by aaron on 29.05.17.
 *
 */
interface UnsplashInterface {

    @GET("photos/{curated}")
    fun getPhotos(@Path("curated") curated: String,
                  @Query("per_page") per_page: Int,
                  @Query("page") page: Int,
                  @Query("order_by") sortOrder: String) : Call<List<PhotosReply>>

    @GET("photos/{imageId}")
    fun getPhotoById(@Path("imageId") imageId: String) : Call<SinglePhoto>

    @GET("collections/featured")
    fun getCollections(@Query("page") page: Int,
                       @Query("per_page") per_page: Int) : Call<List<Collection>>

    @GET("collections/{collectionId}/photos")
    fun getPhotosForCollection(@Path("collectionId") collectionId : Int,
                               @Query("page") page: Int,
                               @Query("per_page") per_page: Int) : Call<List<PhotosReply>>

    @GET("search/photos")
    fun searchForPhotos(@Query("query") searchQuery: String,
                        @Query("per_page") per_page: Int,
                        @Query("page") page: Int) : Call<PhotoSearchReply>

    @GET("search/collections")
    fun searchForCollections(@Query("query") searchQuery: String,
                             @Query("per_page") per_page: Int,
                             @Query("page") page: Int) : Call<CollectionSearchReply>

    @GET("photos/random")
    fun getRandomPhoto(@Query("orientation") orientation : String,
                       @Query("featured") featured: String) : Call<PhotosReply>

    @GET("users/{username}/photos")
    fun getPhotosForUser(@Path("username") username: String,
                         @Query("order_by") order : String = "latest",
                         @Query("per_page") per_page: Int = 30,
                         @Query("page") page: Int) : Call<List<PhotosReply>>

    @GET("users/{username}/collections")
    fun getCollectionsForUser(@Path("username") username: String,
                              @Query("per_page") per_page: Int = 30,
                              @Query("page") page: Int) : Call<List<Collection>>

    @GET("users/{username}/likes")
    fun getLikesForUser(@Path("username") username: String,
                        @Query("order_by") order : String = "latest",
                        @Query("per_page") per_page: Int = 30,
                        @Query("page") page: Int) : Call<List<PhotosReply>>

    @GET("me")
    fun getUserInfo() : Call<User>
}