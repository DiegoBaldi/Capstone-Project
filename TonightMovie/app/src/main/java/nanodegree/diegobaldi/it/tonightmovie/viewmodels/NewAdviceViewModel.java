package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.net.Uri;
import android.view.View;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.views.RequestActivity;

/**
 * Created by diego on 26/02/2017.
 */
@InverseBindingMethods({
        @InverseBindingMethod(type = Spinner.class, attribute = "android:selectedItemPosition"),
})
public class NewAdviceViewModel extends BaseObservable {

    public MovieRequest request;
    public Advice advice;
    public Context context;
    public String message = "";
    public boolean tryToSubmit = false;
    public int adviceSubmitted = 0;

    public NewAdviceViewModel(Context context, MovieRequest request, Advice advice) {
        this.request = request;
        this.context = context;
        this.advice = advice;
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + advice.getMovie().getPosterPath());
    }

    public String getMovieName() {
        return advice.getMovie().getOriginalTitle();
    }

    @Bindable
    public int getAdviceSubmitted() {
        return adviceSubmitted;
    }

    public void setAdviceSubmitted(int adviceSubmitted) {
        this.adviceSubmitted = adviceSubmitted;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSubmit = true;
                sendRequest();
            }
        };
    }

    public View.OnClickListener goBack() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRequest();
            }
        };
    }

    private void goToRequest() {
        Intent intent = new Intent(context, RequestActivity.class);
        intent.putExtra("request", request);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private void sendRequest() {
        tryToSaveMovieAdvice();
        tryToSaveRequestAdvice();
    }

    private void tryToSaveRequestAdvice() {
        FirebaseUtil.getRequestAdvicesMovieRef(request.getId(), advice.getMovie().getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(advice);
                    return Transaction.success(mutableData);
                }

                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    adviceSubmitted = 1;
                    setMessage(context.getString(R.string.advice_sent_success));
                    notifyPropertyChanged(BR.adviceSubmitted);
                } else {
                    adviceSubmitted = 2;
                    setMessage(context.getString(R.string.advice_sent_error));
                    notifyPropertyChanged(BR.adviceSubmitted);
                }

            }
        });
    }

    private void tryToSaveMovieAdvice() {
        FirebaseUtil.getMovieAdvicesMovieRef(request.getMovie().getId(), advice.getMovie().getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(advice.getMovie().toMovieAdvicesMap());
                    return Transaction.success(mutableData);
                }

                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }


}