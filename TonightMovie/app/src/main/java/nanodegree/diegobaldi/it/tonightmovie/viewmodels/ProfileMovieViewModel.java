package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.views.MovieActivity;

/**
 * Created by diego on 05/03/2017.
 */

public class ProfileMovieViewModel extends BaseObservable {

    private Context context;
    private Movie movie;

    public ProfileMovieViewModel(Context context, Movie movie) {
        this.context = context;
        this.movie = movie;
    }

    public String getName(){
        return movie.getOriginalTitle();
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500"+movie.getPosterPath());
    }

    public View.OnClickListener onClickPoster() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetailsActivity();
            }
        };
    }

    private void launchDetailsActivity() {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra("movie", movie);
        context.startActivity(intent);
    }
}