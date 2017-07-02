package de.aaronoe.seek.injection;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.aaronoe.seek.SplashApp;
import de.aaronoe.seek.auth.AuthManager;
import de.aaronoe.seek.auth.AuthenticationInterceptor;
import de.aaronoe.seek.data.remote.AuthorizationInterface;
import de.aaronoe.seek.data.remote.UnsplashInterface;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aaron on 30.05.17.
 *
 */

@Module
public class NetModule {

    private String mBaseUrl;
    private String mAuthUrl;

    public NetModule(String baseUrl, String authUrl) {
        mBaseUrl = baseUrl;
        mAuthUrl = authUrl;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor())
                .build();
    }

    @Provides
    @Singleton
    UnsplashInterface provideApiInterface(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(UnsplashInterface.class);
    }

    @Provides
    @Singleton
    AuthorizationInterface provideAuthInterface(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mAuthUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(AuthorizationInterface.class);
    }

    @Provides
    @Singleton
    AuthManager provideAuthmanager(SplashApp splashApp) {
        return new AuthManager(splashApp);
    }

}
