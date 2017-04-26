package nanodegree.diegobaldi.it.tonightmovie.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by diego on 05/03/2017.
 */

@ContentProvider(authority = WatchlistProvider.AUTHORITY, database = WatchlistDatabase.class)
public final class WatchlistProvider {

    public static final String AUTHORITY = "nanodegree.diegobaldi.it.tonightmovie.data.WatchlistProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String WATCHLIST = "watchlist";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = WatchlistDatabase.WATCHLIST)
    public static class Watchlist {

        @ContentUri(
                path = "watchlist",
                type = "vnd.android.cursor.dir/list",
                defaultSort = WatchlistColumns.TIMESTAMP + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.WATCHLIST);

        @InexactContentUri(
                path = Path.WATCHLIST + "/#",
                name = "WATCHLIST_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = WatchlistColumns.THE_MOVIE_DB_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.WATCHLIST, String.valueOf(id));
        }
    }
}
