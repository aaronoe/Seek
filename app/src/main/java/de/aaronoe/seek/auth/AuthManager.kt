package de.aaronoe.seek.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import de.aaronoe.seek.SplashApp
import de.aaronoe.seek.data.model.photos.User
import de.aaronoe.seek.data.remote.UnsplashInterface
import org.jetbrains.anko.defaultSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 *
 * Created by private on 29.06.17.
 */
class AuthManager(splashApp: SplashApp) {

    private val PREFERENCE_NAME = "resplash_authorize_manager"
    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_LOGGED_IN = "key_init"
    private val KEY_USERNAME = "key_init"
    private val TOKEN_NOT_SET = "not_set"

    var loggedIn : Boolean
    var token : String
    var userName: String? = TOKEN_NOT_SET
    var sharedPreferences : SharedPreferences

    @Inject
    lateinit var apiService : UnsplashInterface

    init {
        sharedPreferences = splashApp.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreferences.getString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET)
        loggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
        splashApp.netComponent.inject(this)
    }

    fun login(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).putBoolean(KEY_LOGGED_IN, true).apply()
        loggedIn = true
        this.token = token
        updateUsername()
    }

    private fun updateUsername() {
        val call = apiService.getUserInfo()
        call.enqueue(object : Callback<User> {
            override fun onFailure(p0: Call<User>?, p1: Throwable?) {

            }

            override fun onResponse(p0: Call<User>?, p1: Response<User>?) {
                userName = p1?.body()?.username
                sharedPreferences.edit().putString(KEY_USERNAME, userName).apply()
                Log.e("Updated userid ", userName)
            }
        })

    }

    fun logout() {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET).putBoolean(KEY_LOGGED_IN, false).apply()
        loggedIn = false
        this.token = TOKEN_NOT_SET
    }

}