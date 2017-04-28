package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.views.FeedActivity;

/**
 * Created by diego on 26/02/2017.
 */
@InverseBindingMethods({
        @InverseBindingMethod(type = Spinner.class, attribute = "android:selectedItemPosition"),
})
public class NewRequestViewModel extends BaseObservable {
    public MovieRequest request;
    public int genrePos;
    public Context context;
    public String[] genres;
    public String message = "";
    public boolean tryToSubmit = false;
    public int requestSubmitted = 0;

    public NewRequestViewModel(Context context, Movie movie) {
        this.request = new MovieRequest(movie);
        this.context = context;
        this.genres = context.getResources().getStringArray(R.array.genres);
    }

    @Bindable
    public int getRequestSubmitted() {
        return requestSubmitted;
    }

    public void setRequestSubmitted(int requestSubmitted) {
        this.requestSubmitted = requestSubmitted;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500" + request.getMovie().getPosterPath());
    }

    public String getMovieName() {
        return request.getMovie().getOriginalTitle();
    }

    public String getDescription() {
        return request.getDescription();
    }

    @Bindable
    public String getGenre() {
        return request.getGenre();
    }

    public void setGenre(String genre) {
        request.setGenre(genre);
        notifyPropertyChanged(BR.genre);
    }

    @Bindable
    public int getGenrePos() {
        return genrePos;
    }

    public void setGenrePos(int position) {
        this.genrePos = position;
        setGenre(genres[position]);
    }

    @Bindable
    public String getDescriptionError() {
        if (request.getDescription() != null && request.getDescription().isEmpty() && tryToSubmit) {
            return context.getString(R.string.no_description_error);
        }
        return null;
    }

    public TextWatcher getDescriptionWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                request.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public EditText.OnFocusChangeListener getDescriptionFocusListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    notifyPropertyChanged(BR.descriptionError);
                }
            }
        };
    }

    public View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSubmit = true;
                notifyPropertyChanged(BR.descriptionError);
                sendRequest();
            }
        };
    }

    public View.OnClickListener goBack() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFeedback();
            }
        };
    }

    private void goToFeedback() {
        Intent intent = new Intent(context, FeedActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private void sendRequest() {
        final String key = FirebaseUtil.getRequestRef().push().getKey();
        Map<String, Object> requestValues = request.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseUtil.getRequestPath(key), requestValues);
        childUpdates.put(FirebaseUtil.getUserRequestPath(request.getAuthor().getId(), key), ServerValue.TIMESTAMP);
        childUpdates.put(FirebaseUtil.getRequestUserPath(request.getAuthor().getId(), key), ServerValue.TIMESTAMP);
        childUpdates.put(FirebaseUtil.getGenreRequestsPath(request.getGenre().toLowerCase(), key), requestValues);
        FirebaseUtil.getBaseRef().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                requestSubmitted = 1;
                setMessage(context.getString(R.string.request_sent_success));
                notifyPropertyChanged(BR.requestSubmitted);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                requestSubmitted = 2;
                setMessage(context.getString(R.string.request_sent_error));
                notifyPropertyChanged(BR.requestSubmitted);
            }
        });
    }
}