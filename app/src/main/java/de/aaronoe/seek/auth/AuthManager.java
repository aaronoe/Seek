package de.aaronoe.seek.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Objects;

import javax.inject.Inject;

import de.aaronoe.seek.SplashApp;
import de.aaronoe.seek.data.model.photos.User;
import de.aaronoe.seek.data.remote.UnsplashInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by private on 01.07.17.
 *
 */

public class AuthManager {

    private static final String PREFERENCE_NAME = "resplash_authorize_manager";
    private static final String KEY_ACCESS_TOKEN = "key_access_token";
    private static final String KEY_LOGGED_IN = "key_user_logged_in";
    private static final String KEY_UNSPLASH_USERNAME = "key_username";
    private static final String KEY_PROFILE_IMAGE = "key_user_profileimage";
    public static final String TOKEN_NOT_SET = "not_set";

    public boolean loggedIn;
    public boolean justLoggedOut;
    public String token;
    public String userName;
    public String profilePicture;
    private SharedPreferences mSharedPreferences;
    private AuthStateListener mAuthStatelistener;
    @Inject
    UnsplashInterface apiService;

    public AuthManager(SplashApp application) {
        mSharedPreferences = application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        token = mSharedPreferences.getString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET);
        userName = mSharedPreferences.getString(KEY_UNSPLASH_USERNAME, TOKEN_NOT_SET);
        profilePicture = mSharedPreferences.getString(KEY_PROFILE_IMAGE, TOKEN_NOT_SET);
        loggedIn = mSharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        justLoggedOut = false;
        application.getNetComponent().inject(this);
    }

    public void login(String token) {
        mSharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).putBoolean(KEY_LOGGED_IN, true).apply();
        this.token = token;
        loggedIn = true;
        justLoggedOut = false;
        updateUsername();
    }

    public void updateUsername() {

        Call<User> call = apiService.getUserInfo();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null || response.body().getUsername() == null) return;
                userName = response.body().getUsername();
                profilePicture = response.body().getProfileImage().getLarge();
                mSharedPreferences.edit()
                        .putString(KEY_UNSPLASH_USERNAME, userName)
                        .putString(KEY_PROFILE_IMAGE, profilePicture)
                        .apply();
                if (mAuthStatelistener != null) mAuthStatelistener.OnUserInfoSuccess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
            }
        });
    }

    public interface AuthStateListener {
        void OnUserInfoSuccess();
    }

    public void registerListener(AuthStateListener listener) {
        mAuthStatelistener = listener;
    }

    public void unregisterListener() {
        mAuthStatelistener = null;
    }

    public void logout() {
        mSharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET)
                .putBoolean(KEY_LOGGED_IN, false)
                .putString(KEY_UNSPLASH_USERNAME, TOKEN_NOT_SET)
                .putString(KEY_PROFILE_IMAGE, TOKEN_NOT_SET)
                .apply();
        loggedIn = false;
        justLoggedOut = true;
        userName = TOKEN_NOT_SET;
        token = TOKEN_NOT_SET;
        profilePicture = TOKEN_NOT_SET;
    }

}
