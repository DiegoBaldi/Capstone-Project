package nanodegree.diegobaldi.it.tonightmovie.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemAdviceBinding;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemMovieAdviceBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.AdviceViewModel;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.MovieAdviceViewModel;

/**
 * Created by diego on 26/02/2017.
 */

public class AdviceAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<T> mAdvices;
    private Context mContext;
    private boolean mIsAuthor;
    private String mRequestId;
    private int mRequestMovieId;
    private boolean mIsMovieAdvices;

    public AdviceAdapter(Context context, boolean isMovieAdvices, int requestMovieId) {
        mContext = context;
        mAdvices = new ArrayList<>();
        mIsMovieAdvices = isMovieAdvices;
        mRequestMovieId = requestMovieId;
    }

    public void setRequestAdviceVariables(boolean isAuthor, String requestId){
        mIsAuthor = isAuthor;
        mRequestId = requestId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mIsMovieAdvices){
            ItemMovieAdviceBinding itemMovieAdviceBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_movie_advice,
                    parent,
                    false);
            return new AdviceAdapter.MovieAdviceBindingHolder(itemMovieAdviceBinding);
        } else {
            ItemAdviceBinding itemAdviceBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_advice,
                    parent,
                    false);
            return new AdviceAdapter.RequestAdviceBindingHolder(itemAdviceBinding);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mIsMovieAdvices){
            ItemMovieAdviceBinding itemMovieAdviceBinding = ((MovieAdviceBindingHolder) holder).binding;
            itemMovieAdviceBinding.setMovieAdviceViewModel(new MovieAdviceViewModel(mContext,(Movie) mAdvices.get(position), mRequestMovieId));
        } else {
            ItemAdviceBinding itemAdviceBinding = ((RequestAdviceBindingHolder) holder).binding;
            itemAdviceBinding.setAdviceViewModel(new AdviceViewModel(mContext,(Advice) mAdvices.get(position), mIsAuthor, mRequestId, mRequestMovieId));
        }
    }


    @Override
    public int getItemCount() {
        return mAdvices.size();
    }

    public void setItems(List<T> advices) {
        mAdvices = advices;
        notifyDataSetChanged();
    }

    public void addItem(T advice) {
        mAdvices.add(advice);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mIsMovieAdvices){
            return 0;
        } else
            return 1;
    }

    public static class MovieAdviceBindingHolder extends RecyclerView.ViewHolder {
        private ItemMovieAdviceBinding binding;

        public MovieAdviceBindingHolder(ItemMovieAdviceBinding binding) {
            super(binding.adviceFrame);
            this.binding = binding;
        }
    }

    public static class RequestAdviceBindingHolder extends RecyclerView.ViewHolder {
        private ItemAdviceBinding binding;

        public RequestAdviceBindingHolder(ItemAdviceBinding binding) {
            super(binding.adviceFrame);
            this.binding = binding;
        }
    }

}