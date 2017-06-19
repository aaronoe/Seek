package de.aaronoe.picsplash;

import android.app.Application;

import de.aaronoe.picsplash.data.local.SplashProvider;
import de.aaronoe.picsplash.injection.ApplicationModule;
import de.aaronoe.picsplash.injection.DaggerNetComponent;
import de.aaronoe.picsplash.injection.NetComponent;
import de.aaronoe.picsplash.injection.NetModule;

/**
 * Created by aaron on 29.05.17.
 *
 */

public class SplashApp extends Application {

    public static final String DOWNLOAD_PATH = "/Pictures/AaronSplash/";
    private static final String BASE_URL = "https://api.unsplash.com/";
    private NetComponent mNetComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger%COMPONENT_NAME%
        mNetComponent = DaggerNetComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .netModule(new NetModule(BASE_URL))
                .build();

    }


    public NetComponent getNetComponent() {
        return mNetComponent;
    }

}
