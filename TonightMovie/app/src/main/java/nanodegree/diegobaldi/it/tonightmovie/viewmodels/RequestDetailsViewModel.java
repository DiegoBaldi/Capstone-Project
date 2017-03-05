package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;

/**
 * Created by diego on 25/02/2017.
 */

public class RequestDetailsViewModel extends BaseObservable {

    private Context context;
    private MovieRequest request;

    public RequestDetailsViewModel(Context context, MovieRequest request) {        this.context = context;
        this.request = request;
    }


    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500"+ request.getMovie().getPosterPath());
    }

    public Uri getAuthorImage() {
        return Uri.parse(request.getAuthor().getPhotoURL());
    }

    public String getAuthorName(){
        return request.getAuthor().getDisplayName();
    }

    public String getDescription(){
        return request.getDescription();
    }

    public View.OnClickListener onClickFavorite() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    public View.OnClickListener onClickWatchlist() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    public View.OnClickListener onClickBuy() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
    }
}