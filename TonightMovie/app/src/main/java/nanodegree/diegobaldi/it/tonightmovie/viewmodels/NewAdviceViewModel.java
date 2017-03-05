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
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.BR;
import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

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
    public boolean tryToSubmit = false;
    public boolean adviceSubmitted = false;



    public NewAdviceViewModel(Context context, MovieRequest request, Advice advice){
        this.request = request;
        this.context = context;
        this.advice = advice;
    }

    public Uri getPosterPath(){
        return  Uri.parse("https://image.tmdb.org/t/p/w500"+advice.getMovie().getPosterPath());
    }

    @Bindable
    public boolean getAdviceSubmitted() {
        return adviceSubmitted;
    }

    public void setAdviceSubmitted(boolean adviceSubmitted) {
        this.adviceSubmitted = adviceSubmitted;
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

    private void sendRequest() {
        String key = FirebaseUtil.getRequestAdvicesRef().child(request.getId()).push().getKey();
        Map<String, Object> adviceValues = advice.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseUtil.getMovieAdvicesPath(request.getMovie().getId(), advice.getMovie().getId()), adviceValues);
        childUpdates.put(FirebaseUtil.getRequestAdvicesPath(request.getId(), key), adviceValues);
        FirebaseUtil.getBaseRef().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("arrived", "HERE");
                adviceSubmitted = true;
                notifyPropertyChanged(BR.adviceSubmitted);
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