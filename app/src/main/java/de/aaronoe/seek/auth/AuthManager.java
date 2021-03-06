package de.aaronoe.seek.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

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
    private static final String KEY_FULL_NAME = "key_full_name";

    public boolean loggedIn;
    public boolean justLoggedOut;
    public boolean justLoggedIn;
    public String token;
    public String userName;
    public String fullName;
    public String profilePicture;
    private SharedPreferences mSharedPreferences;
    private AuthStateListener mListener;

    @Inject
    UnsplashInterface apiService;
    @Inject
    FirebaseAnalytics mFirebaseAnalytics;

    public AuthManager(SplashApp application) {
        mSharedPreferences = application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        token = mSharedPreferences.getString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET);
        userName = mSharedPreferences.getString(KEY_UNSPLASH_USERNAME, TOKEN_NOT_SET);
        profilePicture = mSharedPreferences.getString(KEY_PROFILE_IMAGE, TOKEN_NOT_SET);
        loggedIn = mSharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        fullName = mSharedPreferences.getString(KEY_FULL_NAME, TOKEN_NOT_SET);
        justLoggedOut = false;
        justLoggedIn = false;
        application.getNetComponent().inject(this);
    }

    public void login(String token) {
        mSharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).putBoolean(KEY_LOGGED_IN, true).apply();
        this.token = token;
        loggedIn = true;
        justLoggedIn = true;
        justLoggedOut = false;

        Bundle event = new Bundle();
        event.putString("username", userName);
        mFirebaseAnalytics.logEvent("userLogin", event);

        updateUsername();
    }

    private static final String TAG = "AuthManager";
    public void updateUsername() {

        Call<User> call = apiService.getUserInfo();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null || response.body().getUsername() == null) return;
                userName = response.body().getUsername();
                profilePicture = response.body().getProfileImage().getLarge();
                fullName = response.body().getName();
                mSharedPreferences.edit()
                        .putString(KEY_UNSPLASH_USERNAME, userName)
                        .putString(KEY_PROFILE_IMAGE, profilePicture)
                        .putString(KEY_FULL_NAME, fullName)
                        .apply();

                Bundle event = new Bundle();
                event.putString("username", userName);
                mFirebaseAnalytics.logEvent("updateUsername", event);
                Log.d(TAG, "updateUsername - onResponse() called with: call = [" + call + "], response = [" + response + "]");

                if (mListener != null) mListener.OnLoginSuccess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                if (mListener != null) mListener.onLoginFailure();
            }
        });
    }

    public interface AuthStateListener {
        void OnLoginSuccess();
        void onLoginFailure();
    }

    public void registerListener(AuthStateListener authStateListener) {
        mListener = authStateListener;
    }

    public void unregisterListener() {
        mListener = null;
    }

    public void logout() {
        Bundle event = new Bundle();
        event.putString("username", userName);
        mFirebaseAnalytics.logEvent("userLogout", event);
        mSharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, TOKEN_NOT_SET)
                .putString(KEY_FULL_NAME, TOKEN_NOT_SET)
                .putBoolean(KEY_LOGGED_IN, false)
                .putString(KEY_UNSPLASH_USERNAME, TOKEN_NOT_SET)
                .putString(KEY_PROFILE_IMAGE, TOKEN_NOT_SET)
                .apply();
        loggedIn = false;
        justLoggedOut = true;
        userName = TOKEN_NOT_SET;
        token = TOKEN_NOT_SET;
        profilePicture = TOKEN_NOT_SET;
        fullName = TOKEN_NOT_SET;
    }

}
