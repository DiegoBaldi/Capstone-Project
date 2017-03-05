package nanodegree.diegobaldi.it.tonightmovie.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemReviewBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Review;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.ReviewViewModel;

/**
 * Created by diego on 25/02/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.BindingHolder> {
    private List<Review> mReviews;
    private Context mContext;

    public ReviewAdapter(Context context) {
        mContext = context;
        mReviews = new ArrayList<>();
    }

    @Override
    public ReviewAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemReviewBinding itemReviewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_review,
                parent,
                false);
        return new ReviewAdapter.BindingHolder(itemReviewBinding);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.BindingHolder holder, int position) {
        ItemReviewBinding itemReviewBinding = holder.binding;
        itemReviewBinding.setReviewViewModel(new ReviewViewModel(mContext, mReviews.get(position)));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void setItems(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public void addItem(Review review) {
        mReviews.add(review);
        notifyDataSetChanged();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemReviewBinding binding;

        public BindingHolder(ItemReviewBinding binding) {
            super(binding.reviewFrame);
            this.binding = binding;
        }
    }

}
