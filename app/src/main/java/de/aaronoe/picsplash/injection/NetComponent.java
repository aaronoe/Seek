package de.aaronoe.picsplash.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.aaronoe.picsplash.ui.mainlist.FeaturedFragment;

/**
 * Created by aaron on 29.05.17.
 *
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {

    void inject(FeaturedFragment featuredFragment);

}
