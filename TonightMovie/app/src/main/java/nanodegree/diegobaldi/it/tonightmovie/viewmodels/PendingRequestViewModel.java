package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.views.ProfileActivity;
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

    @Bindable
    public boolean getRequestInterested(){
        return request.isInterested();
    }

    public void setRequestInterested(boolean interest){
        request.setInterested(interest);
    }

    public int isFromUser(){
        if(TonightMovieApp.getUser().getId().equalsIgnoreCase(request.getAuthor().getId()))
            return View.GONE;
        else return View.VISIBLE;
    }

    public String isFollowed(){
        if(request.isInterested()){
            return "Following";
        }
        else return "Follow";
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

    public String getMovieName(){
        return request.getMovie().getOriginalTitle();
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

    public View.OnClickListener onClickAuthor() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAuthorProfileActivity();
            }
        };
    }

    private void launchAuthorProfileActivity() {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("profile", request.getAuthor());
        context.startActivity(intent);
    }

    public View.OnClickListener onClickInterested() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToUserRequests();
            }
        };
    }

    private void addToUserRequests() {
        Map<String, Object> childUpdates = new HashMap<>();
        if(!getRequestInterested()){
            childUpdates.put(FirebaseUtil.getUserRequestPath(TonightMovieApp.getUser().getId(), request.getId()), ServerValue.TIMESTAMP);
            childUpdates.put(FirebaseUtil.getRequestUserPath(TonightMovieApp.getUser().getId(), request.getId()), ServerValue.TIMESTAMP);
        }
        else{
            childUpdates.put(FirebaseUtil.getUserRequestPath(TonightMovieApp.getUser().getId(), request.getId()), null);
            childUpdates.put(FirebaseUtil.getRequestUserPath(TonightMovieApp.getUser().getId(), request.getId()), null);
        }
        FirebaseUtil.getBaseRef().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                request.setInterested(!getRequestInterested());
                notifyPropertyChanged(BR.requestInterested);
            }
        });
    }

}