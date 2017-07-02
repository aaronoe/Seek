package de.aaronoe.seek.injection;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.aaronoe.seek.SplashApp;

/**
 * Created by aaron on 10.06.17.
 *
 */

@Module
public class ApplicationModule {

    Application mApplication;
    SplashApp mSplashApp;

    public ApplicationModule(Application application, SplashApp splashApp) {
        mApplication = application;
        mSplashApp = splashApp;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    SplashApp provideSplashApp() {
        return mSplashApp;
    }

}

