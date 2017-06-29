package de.aaronoe.seek.auth

import android.content.Context
import android.content.SharedPreferences
import de.aaronoe.seek.SplashApp

/**
 *
 * Created by private on 29.06.17.
 */
class AuthManager(splashApp: SplashApp) {

    private val PREFERENCE_NAME = "resplash_authorize_manager"
    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_INIT = "key_init"
    public val TOKEN_NOT_SET = "not_set"

    var loggedIn : Boolean
    var token : String
    var sharedPreferences : SharedPreferences

    init {
        sharedPreferences = splashApp.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreferences.getString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET)
        loggedIn = sharedPreferences.getBoolean(KEY_INIT, false)
    }

    fun login(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).putBoolean(KEY_INIT, true).apply()
        loggedIn = true
        this.token = token
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
        loggedIn = false
        this.token = TOKEN_NOT_SET
    }

}