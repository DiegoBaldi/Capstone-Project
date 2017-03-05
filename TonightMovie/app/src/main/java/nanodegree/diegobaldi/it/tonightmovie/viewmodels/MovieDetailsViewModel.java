package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.ContentValues;
import android.content.Context;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistColumns;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistProvider;
import nanodegree.diegobaldi.it.tonightmovie.models.Genre;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieDetails;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by diego on 25/02/2017.
 */

public class MovieDetailsViewModel extends BaseObservable {

    private Context context;
    private MovieDetails movie;

    public MovieDetailsViewModel(Context context, MovieDetails movie) {
        this.context = context;
        this.movie = movie;
    }
    
    public String getMovieTitle() {
        return movie.getTitle();
    }

    public String getYear() {
        return String.format(Locale.getDefault(), context.getString(R.string.year), movie.getReleaseDate().substring(0,4));
    }

    public String getGenre() {
        String genres = "";
        int i = 1;
        for(Genre genre: movie.getGenres()){
            if(i==movie.getGenres().size())
                genres += genre.getName();
            else
                genres += genre.getName()+", ";
            i++;
        }
        return String.format(Locale.getDefault(), context.getString(R.string.genre), genres);
    }

    public String getScore() {
        return String.format(Locale.getDefault(), context.getString(R.string.vote), movie.getVoteAverage(), movie.getVoteCount());
    }

    public String getSynopsis() {
        return movie.getOverview();
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500"+movie.getPosterPath());
    }

    public Uri getPosterBackdropPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500"+movie.getBackdropPath());
    }

    public View.OnClickListener onClickFavorite() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                movie.setFavorite(!movie.isFavorite());
                if(movie.isFavorite()){
                    Map<String, Object> movieValues = movie.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(movie.getId()+"", movieValues);
                    FirebaseUtil.getFavoritesRef().child(TonightMovieApp.getUser().getId()).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_pressed));
                        }
                    });
                } else{
                    FirebaseUtil.getFavoritesRef().child(TonightMovieApp.getUser().getId()).child(String.valueOf(movie.getId())).setValue(null)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_neutral));
                                                       }
                                                   }
                            );
                }
            }
        };
    }


    public View.OnClickListener onClickWatchlist() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movie.setInWatchlist(!movie.isInWatchlist());
                if(movie.isInWatchlist()){
                    addMovieToDatabase();
                    ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watchlist_pressed));
                } else{
                    deleteMovieFromDatabase();
                    ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watchlist_neutral));
                }

            }
        };
    }

    private void deleteMovieFromDatabase() {
        getApplicationContext().getContentResolver().delete(WatchlistProvider.Watchlist.CONTENT_URI,
                WatchlistColumns.THE_MOVIE_DB_ID + " = " + movie.getId(),
                null);
    }

    private void addMovieToDatabase() {
        new Thread(new Runnable() {
            @Override public void run() {
                ContentValues cv = new ContentValues();
                cv.put(WatchlistColumns.THE_MOVIE_DB_ID, movie.getId());
                cv.put(WatchlistColumns.ORIGINAL_TITLE, movie.getOriginalTitle());
                cv.put(WatchlistColumns.POSTER_PATH, movie.getPosterPath());
                cv.put(WatchlistColumns.TIMESTAMP, System.currentTimeMillis());
                getApplicationContext().getContentResolver().insert(WatchlistProvider.Watchlist.CONTENT_URI, cv);
            }
        }).start();
    }

    public View.OnClickListener onClickBuy() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
    }
}