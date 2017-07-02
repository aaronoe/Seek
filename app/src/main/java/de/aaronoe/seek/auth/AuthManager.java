package de.aaronoe.seek.auth;

import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String KEY_PROFILE_IMAGE = "key_username";
    public static final String TOKEN_NOT_SET = "not_set";

    public boolean loggedIn;
    public String token;
    public String userName;
    public String profilePicture;
    private SharedPreferences mSharedPreferences;
    @Inject
    UnsplashInterface apiService;

    public AuthManager(SplashApp application) {
        mSharedPreferences = application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        token = mSharedPreferences.getString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET);
        userName = mSharedPreferences.getString(KEY_UNSPLASH_USERNAME, TOKEN_NOT_SET);
        profilePicture = mSharedPreferences.getString(KEY_PROFILE_IMAGE, TOKEN_NOT_SET);
        loggedIn = mSharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        application.getNetComponent().inject(this);
    }

    public void login(String token) {
        mSharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).putBoolean(KEY_LOGGED_IN, true).apply();
        this.token = token;
        loggedIn = true;
        updateUsername(null);
    }

    private void updateUsername(final OnRetrieveUserInfoListener listener) {
        Call<User> call = apiService.getUserInfo();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null || response.body().getUsername() == null) return;
                userName = response.body().getUsername();
                profilePicture = response.body().getProfileImage().getMedium();
                mSharedPreferences.edit()
                        .putString(KEY_UNSPLASH_USERNAME, userName)
                        .putString(KEY_PROFILE_IMAGE, profilePicture)
                        .apply();
                if (listener != null) listener.onSuccess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                if (listener != null) listener.onFailure();
            }
        });
    }

    public interface OnRetrieveUserInfoListener {
        void onSuccess();
        void onFailure();
    }

    public void logout() {
        mSharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET)
                .putBoolean(KEY_LOGGED_IN, false)
                .putString(KEY_UNSPLASH_USERNAME, TOKEN_NOT_SET)
                .putString(KEY_PROFILE_IMAGE, TOKEN_NOT_SET)
                .apply();
        loggedIn = false;
        userName = TOKEN_NOT_SET;
        token = TOKEN_NOT_SET;
        profilePicture = TOKEN_NOT_SET;
    }

}
