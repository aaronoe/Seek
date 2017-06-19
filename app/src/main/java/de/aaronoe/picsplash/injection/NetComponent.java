package de.aaronoe.picsplash.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.aaronoe.picsplash.ui.collectiondetail.CollectionDetailActivity;
import de.aaronoe.picsplash.ui.collectionlist.CollectionFragment;
import de.aaronoe.picsplash.ui.mainlist.PhotoListFragment;
import de.aaronoe.picsplash.ui.mainnav.NavigationActivity;
import de.aaronoe.picsplash.ui.photodetail.PhotoDetailActivity;
import de.aaronoe.picsplash.ui.search.SearchActivity;
import de.aaronoe.picsplash.ui.search.results.SearchResultActivity;

/**
 * Created by aaron on 29.05.17.
 *
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetModule.class})
public interface NetComponent {

    void inject(PhotoListFragment photoListFragment);
    void inject(SearchActivity searchActivity);
    void inject(NavigationActivity navigationActivity);
    void inject(PhotoDetailActivity detailActivity);
    void inject(SearchResultActivity searchResultActivity);
    void inject(CollectionFragment fragment);
    void inject(CollectionDetailActivity collectionDetailActivity);

}
