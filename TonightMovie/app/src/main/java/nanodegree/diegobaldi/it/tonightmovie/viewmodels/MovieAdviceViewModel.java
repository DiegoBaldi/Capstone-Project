package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Locale;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.views.MovieActivity;

/**
 * Created by diego on 25/02/2017.
 */

public class MovieAdviceViewModel extends BaseObservable {

    private static final String LOG_TAG = MovieAdviceViewModel.class.getSimpleName();
    private Context context;
    private Movie movie;
    private int requestMovieId;

    public MovieAdviceViewModel(Context context, Movie movie, int requestMovieId) {
        this.context = context;
        this.movie = movie;
        this.requestMovieId = requestMovieId;
    }

    @Bindable
    public String getKarma() {
        return String.format(Locale.getDefault(), context.getString(R.string.advice_karma), movie.getKarma());
    }

    @Bindable
    public int getLikeStatus() {
        return movie.getLikeStatus();
    }


    public String getMovieName() {
        return movie.getOriginalTitle();
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
    }


    public View.OnClickListener onClickPoster() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetailsActivity();
            }
        };
    }

    public View.OnClickListener onDownVote() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int karma = movie.getKarma();
                int newLikeStatus = 0;
                int oldLikeStatus = movie.getLikeStatus();
                switch (oldLikeStatus) {
                    case 0:
                        karma = karma - 1;
                        newLikeStatus = 2;
                        break;
                    case 1:
                        karma = karma - 2;
                        newLikeStatus = 2;
                        break;
                    case 2:
                        karma = karma + 1;
                        newLikeStatus = 0;
                }
                changeKarma(false, karma, oldLikeStatus, newLikeStatus);
            }
        };
    }

    public View.OnClickListener onUpVote() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int karma = movie.getKarma();
                int newLikeStatus = 0;
                int oldLikeStatus = movie.getLikeStatus();
                switch (oldLikeStatus) {
                    case 0:
                        karma = karma + 1;
                        newLikeStatus = 1;
                        break;
                    case 1:
                        karma = karma - 1;
                        newLikeStatus = 0;
                        break;
                    case 2:
                        karma = karma + 2;
                        newLikeStatus = 1;
                        break;
                }
                changeKarma(true, karma, oldLikeStatus, newLikeStatus);
            }
        };
    }


    private void changeKarma(boolean upvote, int karma, int oldLikeStatus, int newLikeStatus) {
        movie.setKarma(karma);
        notifyPropertyChanged(BR.karma);
        changeLikeStatus(newLikeStatus);
        changeMovieAdviceKarma(upvote, oldLikeStatus);
        updateUserMovieAdviceStatus(newLikeStatus);

    }

    private void updateUserMovieAdviceStatus(int newLikeStatus) {
        FirebaseUtil.getUserMovieAdvicesRef().child(String.valueOf(requestMovieId)).child(String.valueOf(movie.getId())).child("liked").setValue(newLikeStatus);
    }


    private void changeMovieAdviceKarma(boolean isUpvote, final int likeStatus) {
        if (isUpvote) {
            FirebaseUtil.getMovieAdvicesRef(requestMovieId).child(String.valueOf(movie.getId())).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Movie mutableMovie = mutableData.getValue(Movie.class);
                    if (mutableMovie == null) {
                        return Transaction.success(mutableData);
                    }
                    int karma = mutableMovie.getKarma();

                    switch (likeStatus) {
                        case 0:
                            karma = karma + 1;
                            break;
                        case 1:
                            karma = karma - 1;
                            break;
                        case 2:
                            karma = karma + 2;
                            break;

                    }

                    mutableMovie.setKarma(karma);
                    // Set value and report transaction success
                    mutableData.setValue(mutableMovie.toMovieAdvicesMap());
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(LOG_TAG, "adviceTransaction:onComplete:" + databaseError);
                }
            });
        } else {
            FirebaseUtil.getMovieAdvicesRef(requestMovieId).child(String.valueOf(movie.getId())).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Movie mutableMovie = mutableData.getValue(Movie.class);
                    if (mutableMovie == null) {
                        return Transaction.success(mutableData);
                    }

                    int karma = mutableMovie.getKarma();

                    switch (likeStatus) {
                        case 0:
                            karma = karma - 1;
                            break;
                        case 1:
                            karma = karma - 2;
                            break;
                        case 2:
                            karma = karma + 1;

                    }

                    mutableMovie.setKarma(karma);
                    // Set value and report transaction success
                    mutableData.setValue(mutableMovie.toMovieAdvicesMap());
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(LOG_TAG, "adviceTransaction:onComplete:" + databaseError);
                }
            });
        }
    }

    private void changeLikeStatus(int likeStatus) {
        movie.setLikeStatus(likeStatus);
        notifyPropertyChanged(BR.likeStatus);
    }

    private void launchDetailsActivity() {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra("movie", movie);
        context.startActivity(intent);
    }
}