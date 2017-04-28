package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.views.ProfileActivity;
import nanodegree.diegobaldi.it.tonightmovie.views.RequestActivity;

/**
 * Created by diego on 28/02/2017.
 */

public class ClosedRequestViewModel extends BaseObservable {

    private Context context;
    private MovieRequest request;

    public ClosedRequestViewModel(Context context, MovieRequest request) {
        this.context = context;
        this.request = request;
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + request.getMovie().getPosterPath());
    }

    public Uri getRequesterImage() {
        if(request.getAuthor()!= null && request.getAuthor().getPhotoURL()!=null)
            return Uri.parse(request.getAuthor().getPhotoURL());
        else
            return Uri.parse("");
    }

    public String getRequesterName() {
        return request.getAuthor().getDisplayName();
    }

    public String getAcceptedMovieName() {
        return request.getAcceptedAdvice().getMovie().getOriginalTitle();
    }

    public String getMovieName() {
        return request.getMovie().getOriginalTitle();
    }

    public Uri getAdvisorImage() {
        if(request.getAcceptedAdvice()!=null && request.getAcceptedAdvice().getAuthor()!=null && request.getAcceptedAdvice().getAuthor().getPhotoURL()!=null)
            return Uri.parse(request.getAcceptedAdvice().getAuthor().getPhotoURL());
        else
            return Uri.parse("");
    }

    public String getAdvisorName() {
        return request.getAcceptedAdvice().getAuthor().getDisplayName();
    }

    public Uri getAcceptedPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + request.getAcceptedAdvice().getMovie().getPosterPath());
    }

    public View.OnClickListener onClickRequest() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRequestDetailsActivity();
            }
        };
    }

    private void launchRequestDetailsActivity() {
        Intent intent = new Intent(context, RequestActivity.class);
        intent.putExtra("request", request);
        context.startActivity(intent);
    }

    public View.OnClickListener onClickRequester() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfileActivity(request.getAuthor());
            }
        };
    }

    public View.OnClickListener onClickAdvisor() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfileActivity(request.getAcceptedAdvice().getAuthor());
            }
        };
    }

    private void launchProfileActivity(User profile) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("profile", profile);
        context.startActivity(intent);
    }
}