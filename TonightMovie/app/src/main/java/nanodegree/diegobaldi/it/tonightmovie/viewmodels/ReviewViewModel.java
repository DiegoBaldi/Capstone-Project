package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;

import nanodegree.diegobaldi.it.tonightmovie.models.Review;

/**
 * Created by diego on 25/02/2017.
 */

public class ReviewViewModel extends BaseObservable {

    private Review review;
    private Context mContext;

    public ReviewViewModel(Context context, Review review) {
        this.review = review;
        this.mContext = context;
    }

    public String getAuthor() {
        return "by " + review.getAuthor();
    }

    public String getContent() {
        return review.getContent();
    }
}