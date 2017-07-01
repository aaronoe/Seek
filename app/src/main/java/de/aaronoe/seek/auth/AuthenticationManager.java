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

public class AuthenticationManager {

    private SplashApp mSplashApp;

    private static final String PREFERENCE_NAME = "resplash_authorize_manager";
    private static final String KEY_ACCESS_TOKEN = "key_access_token";
    private static final String KEY_LOGGED_IN = "key_user_logged_in";
    private static final String KEY_USERNAME = "key_username";
    private static final String TOKEN_NOT_SET = "not_set";

    public boolean loggedIn;
    public String token;
    public String userName;
    public SharedPreferences mSharedPreferences;
    @Inject
    UnsplashInterface apiService;

    public AuthenticationManager(SplashApp application) {
        mSplashApp = application;
        mSharedPreferences = mSplashApp.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        token = mSharedPreferences.getString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET);
        userName = mSharedPreferences.getString(KEY_USERNAME, TOKEN_NOT_SET);
        loggedIn = mSharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        mSplashApp.getNetComponent().inject(this);
    }

    public void login(String token) {
        mSharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).putBoolean(KEY_LOGGED_IN, true).apply();
        userName = token;
        loggedIn = true;
        updateUsername();
    }

    private void updateUsername() {
        Call<User> call = apiService.getUserInfo();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null || response.body().getUsername() == null) return;
                userName = response.body().getUsername();
                mSharedPreferences.edit().putString(KEY_USERNAME, userName).apply();
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {

            }
        });
    }

    public void logout() {
        mSharedPreferences.edit().putString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET).putBoolean(KEY_LOGGED_IN, false).putString(KEY_USERNAME, TOKEN_NOT_SET).apply();
        loggedIn = false;
        userName = TOKEN_NOT_SET;
        token = TOKEN_NOT_SET;
    }

}
