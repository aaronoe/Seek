package de.aaronoe.seek;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import java.net.URI;

import de.aaronoe.seek.auth.AuthManager;
import de.aaronoe.seek.data.local.SplashProvider;
import de.aaronoe.seek.injection.ApplicationModule;
import de.aaronoe.seek.injection.DaggerNetComponent;
import de.aaronoe.seek.injection.NetComponent;
import de.aaronoe.seek.injection.NetModule;

/**
 * Created by aaron on 29.05.17.
 *
 */

public class SplashApp extends Application {

    public static String DOWNLOAD_PATH;
    private static final String BASE_URL = "https://api.unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_URL = "https://unsplash.com";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";
    private NetComponent mNetComponent;
    private AuthManager mAuthManager;
    public static final String CLIENT_ID = BuildConfig.UNSPLASH_API_KEY;

    @Override
    public void onCreate() {
        super.onCreate();

        DOWNLOAD_PATH = "/Pictures/" + getString(R.string.app_name);

        // Dagger%COMPONENT_NAME%
        mNetComponent = DaggerNetComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .netModule(new NetModule(BASE_URL, UNSPLASH_URL))
                .build();

        mAuthManager = new AuthManager(this);
        instance = this;
    }

    public String getLoginUrl(Context c){
        return UNSPLASH_URL + "/oauth/authorize"
                + "?client_id=" + BuildConfig.UNSPLASH_API_KEY
                + "&redirect_uri=" + c.getString(R.string.unsplash_callback)
                + "&response_type=" + "code"
                + "&scope=" + "public+read_user+write_user+read_photos+write_photos+write_likes+read_collections+write_collections";
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    private static SplashApp instance;

    public static SplashApp getInstance() {return  instance; }

    public AuthManager getAuthManager() {
        return mAuthManager;
    }
}
