package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import java.util.Locale;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.views.RequestActivity;

/**
 * Created by diego on 28/02/2017.
 */

public class PendingRequestViewModel extends BaseObservable {

    private Context context;
    private MovieRequest request;

    public PendingRequestViewModel(Context context, MovieRequest movieRequest) {
        this.context = context;
        this.request = movieRequest;
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500"+request.getMovie().getPosterPath());
    }

    public Uri getAuthorImage(){
        return Uri.parse(request.getAuthor().getPhotoURL());
    }

    public String getAuthorName(){
        return request.getAuthor().getDisplayName();
    }

    public String getShortDescription(){
        return String.format(Locale.getDefault(), context.getString(R.string.request_short_description), request.getGenre(), request.getMovie().getOriginalTitle());
    }

    public View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetailsActivity();
            }
        };
    }

    private void launchDetailsActivity() {
        Intent intent = new Intent(context, RequestActivity.class);
        intent.putExtra("request", request);
        context.startActivity(intent);
    }

    public View.OnClickListener onClicInterested() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetailsActivity();
            }
        };
    }

}