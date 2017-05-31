package de.aaronoe.picsplash.injection

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *
 * Created by aaron on 29.05.17.
 */

@Module
class AppModule(internal var mApplication: Application) {

    @Provides
    @Singleton
    internal fun provideApplication(): Application {
        return mApplication
    }

}
