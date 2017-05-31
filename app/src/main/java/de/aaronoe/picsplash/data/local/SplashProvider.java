package de.aaronoe.picsplash.data.local;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 *
 * Created by aaron on 29.05.17.
 */

@ContentProvider(
        authority = SplashProvider.AUTHORITY,
        database = SplashDatabase.class)
public final class SplashProvider {

    public static final String AUTHORITY = "de.aaronoe.picsplash.provider.splashprovider";

    @TableEndpoint(table = SplashDatabase.SPLASH)
    public static class Splash {

        @ContentUri(
                path = "splashes",
                type = "vnd.android.cursor.dir/splash",
                defaultSort = ImageColumns.COLUMN_TIMESTAMP + " DESC")
        public static final Uri SPLASHES_URI = Uri.parse("content://" + AUTHORITY + "/splashes");


        @InexactContentUri(
                path = "splashes/#",
                name = "SPLASH_ID",
                type = "vnd.android.cursor.item/splash",
                whereColumn = ImageColumns.COLUMN_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/splashes/" + id);
        }

    }

}
