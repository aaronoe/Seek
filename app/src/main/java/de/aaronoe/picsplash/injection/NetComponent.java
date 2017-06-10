package de.aaronoe.picsplash.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.aaronoe.picsplash.ui.collectiondetail.CollectionDetailActivity;
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment;
import de.aaronoe.picsplash.ui.mainlist.FeaturedFragment;
import de.aaronoe.picsplash.ui.photodetail.PhotoDetailActivity;

/**
 * Created by aaron on 29.05.17.
 *
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetModule.class})
public interface NetComponent {

    void inject(FeaturedFragment featuredFragment);
    void inject(PhotoDetailActivity detailActivity);
    void inject(CollectionFragment fragment);
    void inject(CollectionDetailActivity collectionDetailActivity);

}
