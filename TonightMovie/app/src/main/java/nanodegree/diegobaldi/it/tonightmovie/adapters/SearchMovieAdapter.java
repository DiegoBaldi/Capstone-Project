package nanodegree.diegobaldi.it.tonightmovie.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemFavMovieBinding;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemSearchMovieBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.SearchMovieViewModel;

/**
 * Created by diego on 24/02/2017.
 */

public class SearchMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Movie> mMovies;
    private Context mContext;
    private boolean mIsFavList;
    private boolean mIsHereForARequest;

    public SearchMovieAdapter(Context context, boolean isFavList, boolean isHereForARequest) {
        mContext = context;
        mMovies = new ArrayList<>();
        mIsFavList = isFavList;
        mIsHereForARequest = isHereForARequest;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mIsFavList){
            ItemFavMovieBinding itemFavMovieBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_fav_movie,
                    parent,
                    false);
            return new SearchMovieAdapter.FavBindingHolder(itemFavMovieBinding);
        } else {
            ItemSearchMovieBinding itemSearchMovieBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_search_movie,
                    parent,
                    false);
            return new SearchMovieAdapter.SearchBindingHolder(itemSearchMovieBinding);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mIsFavList){
            ItemFavMovieBinding itemFavMovieBinding = ((SearchMovieAdapter.FavBindingHolder) holder).binding;
            itemFavMovieBinding.setMovieViewModel(new SearchMovieViewModel(mContext, mMovies.get(position), mIsFavList, mIsHereForARequest));
        } else {
            ItemSearchMovieBinding itemSearchMovieBinding = ((SearchMovieAdapter.SearchBindingHolder) holder).binding;
            itemSearchMovieBinding.setMovieViewModel(new SearchMovieViewModel(mContext, mMovies.get(position), mIsFavList, mIsHereForARequest));
        }

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setItems(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public void addItem(Movie post) {
        mMovies.add(post);
        notifyDataSetChanged();
    }

    public static class FavBindingHolder extends RecyclerView.ViewHolder {
        private ItemFavMovieBinding binding;

        public FavBindingHolder(ItemFavMovieBinding binding) {
            super(binding.frame);
            this.binding = binding;
        }
    }

    public static class SearchBindingHolder extends RecyclerView.ViewHolder {
        private ItemSearchMovieBinding binding;

        public SearchBindingHolder(ItemSearchMovieBinding binding) {
            super(binding.frame);
            this.binding = binding;
        }
    }

}