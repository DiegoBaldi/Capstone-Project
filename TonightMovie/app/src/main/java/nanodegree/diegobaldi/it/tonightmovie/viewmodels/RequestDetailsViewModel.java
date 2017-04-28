package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.views.MovieActivity;
import nanodegree.diegobaldi.it.tonightmovie.views.ProfileActivity;

/**
 * Created by diego on 25/02/2017.
 */

public class RequestDetailsViewModel extends BaseObservable {

    private Context context;
    private MovieRequest request;

    public RequestDetailsViewModel(Context context, MovieRequest request) {
        this.context = context;
        this.request = request;
    }


    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + request.getMovie().getPosterPath());
    }

    public Uri getAuthorImage() {
        if(request.getAuthor()!=null && request.getAuthor().getPhotoURL()!=null)
            return Uri.parse(request.getAuthor().getPhotoURL());
        else
            return Uri.parse("");
    }

    public String getAuthorName() {
        return request.getAuthor().getDisplayName();
    }

    public String getMovieName() {
        return request.getMovie().getOriginalTitle();
    }

    public String getDescription() {
        return request.getDescription();
    }

    public View.OnClickListener onClickFavorite() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    public View.OnClickListener onClickPoster() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMovieDetails();

            }
        };
    }

    private void launchMovieDetails() {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra("movie", request.getMovie());
        context.startActivity(intent);
    }

    public View.OnClickListener onClickUserInfo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfile();
            }
        };
    }

    private void launchProfile() {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("profile", request.getAuthor());
        context.startActivity(intent);
    }
}