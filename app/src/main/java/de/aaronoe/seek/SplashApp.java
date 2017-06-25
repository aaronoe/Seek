package de.aaronoe.seek;

import android.app.Application;

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
    private NetComponent mNetComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        DOWNLOAD_PATH = "/Pictures/" + getString(R.string.app_name);

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
