package nanodegree.diegobaldi.it.tonightmovie.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by diego on 05/03/2017.
 */

@Database(version = WatchlistDatabase.VERSION)
public final class WatchlistDatabase {

    public static final int VERSION = 1;

    @Table(WatchlistColumns.class) public static final String WATCHLIST = "watchlist";

}