package de.aaronoe.seek.auth

import android.util.Log
import de.aaronoe.seek.SplashApp
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 *
 * Created by private on 29.06.17.
 */
class AuthenticationInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request
        if (SplashApp.getInstance().authManager.loggedIn) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + SplashApp.getInstance().authManager.token)
                    .build()
            Log.e("Request: ", "Bearer " + SplashApp.getInstance().authManager.token)
        } else {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID " + SplashApp.CLIENT_ID)
                    .build()
            Log.e("Request: ", "Client-ID " + SplashApp.CLIENT_ID)

        }
        return chain.proceed(request)
    }
}