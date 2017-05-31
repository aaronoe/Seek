package de.aaronoe.picsplash.data.local;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 *
 * Created by aaron on 29.05.17.
 */

@Database(version = SplashDatabase.VERSION)
class SplashDatabase {

    static final int VERSION = 1;

    @Table(ImageColumns.class)
    static final String SPLASH = "splash_table";

}
