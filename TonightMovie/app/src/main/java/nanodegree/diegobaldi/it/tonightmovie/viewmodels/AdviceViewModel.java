package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Locale;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.views.MovieActivity;

/**
 * Created by diego on 25/02/2017.
 */

public class AdviceViewModel extends BaseObservable {

    private static final String LOG_TAG = AdviceViewModel.class.getSimpleName();
    private Context context;
    private Advice advice;
    private String requestId;
    private int requestMovieId;
    private boolean isAuthor;

    public AdviceViewModel(Context context, Advice advice, boolean isAuthor, String requestId, int requestMovieId) {
        this.context = context;
        this.advice = advice;
        this.isAuthor = isAuthor;
        this.requestId = requestId;
        this.requestMovieId = requestMovieId;
    }

    public int getIsAnswerVisibility() {
        if (isAuthor)
            return View.VISIBLE;
        else return View.GONE;
    }

    @Bindable
    public String getKarma() {
        return String.format(Locale.getDefault(), context.getString(R.string.advice_karma), advice.getKarma());
    }

    @Bindable
    public int getLikeStatus() {
        return advice.getLikeStatus();
    }

    public String getAuthorName() {
        return advice.getAuthor().getDisplayName();
    }

    public String getMovieName() {
        return advice.getMovie().getOriginalTitle();
    }

    public Uri getAuthorImage() {
        if (advice.getAuthor().getPhotoURL() != null && advice.getAuthor().getPhotoURL() != null)
            return Uri.parse(advice.getAuthor().getPhotoURL());
        else {
            return Uri.parse("customProfile");
        }
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + advice.getMovie().getPosterPath());
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
                int karma = advice.getKarma();
                int newLikeStatus = 0;
                int oldLikeStatus = advice.getLikeStatus();
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
                int karma = advice.getKarma();
                int newLikeStatus = 0;
                int oldLikeStatus = advice.getLikeStatus();
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
        advice.setKarma(karma);
        notifyPropertyChanged(BR.karma);
        changeLikeStatus(newLikeStatus);
        changeMovieAdviceKarma(upvote, oldLikeStatus);
        changeRequestAdviceKarma(upvote, oldLikeStatus);
    }

    private void changeRequestAdviceKarma(boolean isUpvote, final int likeStatus) {
        if(isUpvote){
            FirebaseUtil.getRequestAdvicesRef().child(requestId).child(advice.getId()).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Advice mutableAdvice = mutableData.getValue(Advice.class);
                    if (mutableAdvice == null) {
                        return Transaction.success(mutableData);
                    }

                    int karma = mutableAdvice.getKarma();
//                    int likeStatus = 0;

                    switch (likeStatus) {
                        case 0:
                            karma = karma + 1;
//                            likeStatus = 1;
                            break;
                        case 1:
                            karma = karma - 1;
//                            likeStatus = 0;
                            break;
                        case 2:
                            karma = karma + 2;
//                            likeStatus = 1;
                            break;

                    }

                    mutableAdvice.setKarma(karma);
//                    advice.setKarma(karma);
//                    notifyPropertyChanged(BR.karma);
//                    changeLikeStatus(likeStatus);

                    // Set value and report transaction success
                    mutableData.setValue(mutableAdvice.toMap());
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
        else {
            FirebaseUtil.getRequestAdvicesRef().child(requestId).child(advice.getId()).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Advice mutableAdvice = mutableData.getValue(Advice.class);
                    if (mutableAdvice == null) {
                        return Transaction.success(mutableData);
                    }

                    int karma = mutableAdvice.getKarma();
//                    int likeStatus = 0;

                    switch (likeStatus) {
                        case 0:
                            karma = karma - 1;
//                            likeStatus = 2;
                            break;
                        case 1:
                            karma = karma - 2;
//                            likeStatus = 2;
                            break;
                        case 2:
                            karma = karma + 1;
//                            likeStatus = 0;

                    }

                    mutableAdvice.setKarma(karma);
//                    advice.setKarma(karma);
//                    notifyPropertyChanged(BR.karma);
//                    changeLikeStatus(likeStatus);
                    // Set value and report transaction success
                    mutableData.setValue(mutableAdvice.toMap());
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

    private void changeMovieAdviceKarma(boolean isUpvote, final int likeStatus) {
        if(isUpvote){
            FirebaseUtil.getMovieAdvicesRef(requestMovieId).child(String.valueOf(advice.getMovie().getId())).runTransaction(new Transaction.Handler() {
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
        }
        else {
            FirebaseUtil.getMovieAdvicesRef(requestMovieId).child(String.valueOf(advice.getMovie().getId())).runTransaction(new Transaction.Handler() {
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
        advice.setLikeStatus(likeStatus);
        notifyPropertyChanged(BR.likeStatus);
    }

    public View.OnClickListener onAcceptAdvice() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptAdvice();
                if (advice.isTheAnswer())
                    ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_neutral));
                else
                    ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_accent));

            }
        };
    }

    private void acceptAdvice() {
        FirebaseUtil.getRequestAdvicesRef().child(requestId).child(advice.getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Advice mutableAdvice = mutableData.getValue(Advice.class);
                if (mutableAdvice == null) {
                    return Transaction.success(mutableData);
                }

                boolean isTheAnswer = mutableAdvice.isTheAnswer();
                int karma = mutableAdvice.getKarma();

                if (advice.isTheAnswer()) {
                    karma = karma - 50;
                    isTheAnswer = false;
                } else {
                    karma = karma + 50;
                    isTheAnswer = true;

                }

                mutableAdvice.setKarma(karma);
                mutableAdvice.setTheAnswer(isTheAnswer);
                advice.setKarma(karma);
                notifyPropertyChanged(BR.karma);
                changeAcceptedStatus(isTheAnswer);

                // Set value and report transaction success
                mutableData.setValue(mutableAdvice.toMap());
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "adviceTransaction:onComplete:" + databaseError);
            }

        });
    }

    private void changeAcceptedStatus(boolean isTheAnswer) {
        advice.setTheAnswer(isTheAnswer);
    }

    public View.OnClickListener onClickAuthor() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfileActivity();
            }
        };
    }

    private void launchProfileActivity() {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra("movie", advice.getMovie());
        context.startActivity(intent);
    }

    private void launchDetailsActivity() {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra("movie", advice.getMovie());
        context.startActivity(intent);
    }

    @BindingAdapter("app:animatedLikeActions")
    public static void flip(final View view, final int likeStatus) {
        // Now create an animator
        switch (likeStatus) {
            case 0:
                ((ImageView) view.findViewById(R.id.upvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_up_neutral));
                ((ImageView) view.findViewById(R.id.downvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_down_neutral));
                break;
            case 1:
                ((ImageView) view.findViewById(R.id.upvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_up_pressed));
                ((ImageView) view.findViewById(R.id.downvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_down_neutral));
                break;
            case 2:
                ((ImageView) view.findViewById(R.id.upvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_up_neutral));
                ((ImageView) view.findViewById(R.id.downvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_down_pressed));
                break;
            default:
        }
    }
}