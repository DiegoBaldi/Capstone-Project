package nanodegree.diegobaldi.it.tonightmovie.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by diego on 05/03/2017.
 */

public interface WatchlistColumns {
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @NotNull
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    String THE_MOVIE_DB_ID = "the_movie_db_id";
    @DataType(TEXT) @NotNull
    String ORIGINAL_TITLE = "original_title";

    @DataType(TEXT) @NotNull
    String POSTER_PATH = "poster_path";

    @DataType(REAL) @NotNull
    String TIMESTAMP = "timestamp";


}

