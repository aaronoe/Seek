package de.aaronoe.seek.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.aaronoe.seek.ui.collectiondetail.CollectionDetailActivity;
import de.aaronoe.seek.ui.collectionlist.CollectionFragment;
import de.aaronoe.seek.ui.mainlist.PhotoListFragment;
import de.aaronoe.seek.ui.mainnav.NavigationActivity;
import de.aaronoe.seek.ui.photodetail.PhotoDetailActivity;
import de.aaronoe.seek.ui.search.SearchActivity;
import de.aaronoe.seek.ui.search.results.SearchResultActivity;

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
