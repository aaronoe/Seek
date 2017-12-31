package de.aaronoe.seek.data.remote

import de.aaronoe.seek.data.model.collections.Collection
import de.aaronoe.seek.data.model.collectionsearch.CollectionSearchReply
import de.aaronoe.seek.data.model.photos.PhotosReply
import de.aaronoe.seek.data.model.photos.User
import de.aaronoe.seek.data.model.photosearch.PhotoSearchReply
import de.aaronoe.seek.data.model.singleItem.SinglePhoto
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by aaron on 29.05.17.
 *
 */
interface UnsplashInterface {

    @GET("photos/{curated}")
    fun getPhotos(@Path("curated") curated: String,
                  @Query("per_page") per_page: Int,
                  @Query("page") page: Int,
                  @Query("order_by") sortOrder: String) : Single<List<PhotosReply>>

    @GET("photos/{imageId}")
    fun getPhotoById(@Path("imageId") imageId: String) : Single<SinglePhoto>

    @GET("collections/featured")
    fun getCollections(@Query("page") page: Int,
                       @Query("per_page") per_page: Int) : Single<List<Collection>>

    @GET("collections/{collectionId}/photos")
    fun getPhotosForCollection(@Path("collectionId") collectionId : Int,
                               @Query("page") page: Int,
                               @Query("per_page") per_page: Int) : Single<List<PhotosReply>>

    @GET("search/photos")
    fun searchForPhotos(@Query("query") searchQuery: String,
                        @Query("per_page") per_page: Int,
                        @Query("page") page: Int) : Single<PhotoSearchReply>

    @GET("search/collections")
    fun searchForCollections(@Query("query") searchQuery: String,
                             @Query("per_page") per_page: Int,
                             @Query("page") page: Int) : Single<CollectionSearchReply>

    @GET("photos/random")
    fun getRandomPhoto(@Query("orientation") orientation : String,
                       @Query("featured") featured: String) : Single<PhotosReply>

    @GET("users/{username}/photos")
    fun getPhotosForUser(@Path("username") username: String,
                         @Query("order_by") order : String = "latest",
                         @Query("per_page") per_page: Int = 30,
                         @Query("page") page: Int) : Single<List<PhotosReply>>

    @GET("users/{username}/collections")
    fun getCollectionsForUser(@Path("username") username: String,
                              @Query("per_page") per_page: Int = 30,
                              @Query("page") page: Int) : Single<List<Collection>>

    @GET("users/{username}/likes")
    fun getLikesForUser(@Path("username") username: String,
                        @Query("order_by") order : String = "latest",
                        @Query("per_page") per_page: Int = 30,
                        @Query("page") page: Int) : Single<List<PhotosReply>>

    @GET("me")
    fun getUserInfo() : Single<User>

    @GET("users/{username}")
    fun getPublicUser(@Path("username") username: String) : Single<User>

    @POST("photos/{id}/like")
    fun likePicture(@Path("id") photoId : String) : Single<ResponseBody>

    @DELETE("photos/{id}/like")
    fun dislikePicture(@Path("id") photoId : String) : Single<ResponseBody>

    @POST("collections/{collection_id}/add")
    fun addPhotoToCollection(@Path("collection_id") collectionId: Int,
                             @Query("photo_id") photoId: String) : Single<ResponseBody>

    @DELETE("collections/{collection_id}/remove")
    fun removePhotoFromCollection(@Path("collection_id") collectionId: String,
                                  @Query("photo_id") photoId: String) : Single<ResponseBody>

    @POST("collections")
    fun createCollections(@Query("title") title : String,
                          @Query("description") description : String,
                          @Query("private") private : Boolean) : Single<Collection>


    @DELETE("collections/{id}")
    fun deleteCollection(@Path("id") collectionId: Int) : Single<ResponseBody>

}