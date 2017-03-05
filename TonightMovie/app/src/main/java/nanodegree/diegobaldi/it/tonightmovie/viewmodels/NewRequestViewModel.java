package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

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
    public boolean tryToSubmit = false;
    public boolean requestSubmitted = false;



    public NewRequestViewModel(Context context, Movie movie){
        this.request = new MovieRequest(movie);
        this.context = context;
        this.genres = context.getResources().getStringArray(R.array.genres);
    }

    @Bindable
    public boolean getRequestSubmitted() {
        return requestSubmitted;
    }

    public void setRequestSubmitted(boolean requestSubmitted) {
        this.requestSubmitted = requestSubmitted;
    }

    public Uri getPosterPath() {
        return Uri.parse("https://image.tmdb.org/t/p/w500"+request.getMovie().getPosterPath());
    }

    public String getDescription() {
        return request.getDescription();
    }

    @Bindable
    public String getGenre() {
        return request.getGenre();
    }

    public void setGenre(String genre){
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
            return "Must enter a description";
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
                if(!hasFocus){
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

    private void sendRequest() {
        String key = FirebaseUtil.getRequestRef().push().getKey();
        Map<String, Object> requestValues = request.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, requestValues);
        FirebaseUtil.getRequestRef().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("arrived", "HERE");
                requestSubmitted = true;
                notifyPropertyChanged(BR.requestSubmitted);
            }
        });
    }



    @BindingAdapter("app:animatedCompletion")
    public static void flip(final View view, final boolean completed) {
        // Now create an animator
        if(completed){
            AnimatorSet setOut = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                    R.animator.card_flip_left_out);
            setOut.setTarget(view.findViewById(R.id.form_card));
            AnimatorSet setIn = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                    R.animator.card_flip_left_in);
            setIn.setTarget(view.findViewById(R.id.success_card));
            setOut.start();
            setIn.start();
        }
    }
}