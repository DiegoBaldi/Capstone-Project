package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.views.MovieActivity;
import nanodegree.diegobaldi.it.tonightmovie.views.NewAdviceActivity;

/**
 * Created by diego on 24/02/2017.
 */

public class SearchMovieViewModel extends BaseObservable {

    private Context context;
    private Movie movie;
    private boolean isFavList;
    private boolean sendToNewAdvice;
    private MovieRequest request;

    public SearchMovieViewModel(Context context, Movie movie, boolean isFavList, boolean sendToNewAdvice) {
        this.context = context;
        this.movie = movie;
        this.isFavList = isFavList;
        this.sendToNewAdvice = sendToNewAdvice;
    }

    public SearchMovieViewModel(Context context, Movie movie, MovieRequest request, boolean isFavList, boolean sendToNewAdvice) {
        this.context = context;
        this.movie = movie;
        this.isFavList = isFavList;
        this.sendToNewAdvice = sendToNewAdvice;
        this.request = request;
    }


    public String getTitle() {
        if (isFavList)
            return movie.getOriginalTitle();
        else
            return movie.getTitle();
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
    }

    public View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sendToNewAdvice)
                    launchDetailsActivity();
                else
                    returnResultMovie();
            }
        };
    }

    private void launchDetailsActivity() {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra("movie", movie);
        context.startActivity(intent);
    }

    private void returnResultMovie() {
        Intent intent = new Intent(context, NewAdviceActivity.class);
        intent.putExtra("movie", movie);
        ((Activity) context).setResult(Activity.RESULT_OK, intent);
        ((Activity) context).finish();
    }
}