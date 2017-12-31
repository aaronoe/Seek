package de.aaronoe.seek.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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

        apiService.getUserInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(new SingleObserver<User>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(User user) {
                    mSharedPreferences.edit()
                        .putString(KEY_UNSPLASH_USERNAME, user.getUsername())
                        .putString(KEY_PROFILE_IMAGE, user.getProfileImage().getMedium())
                        .putString(KEY_FULL_NAME, user.getName())
                        .apply();

                    Bundle event = new Bundle();
                    event.putString("username", userName);
                    mFirebaseAnalytics.logEvent("updateUsername", event);

                    if (mListener != null) mListener.OnLoginSuccess();
                }

                @Override
                public void onError(Throwable e) {
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
