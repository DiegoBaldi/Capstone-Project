package nanodegree.diegobaldi.it.tonightmovie.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemProfileMovieBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.ProfileMovieViewModel;

/**
 * Created by diego on 05/03/2017.
 */

public class ProfileMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Movie> mMovies;
    private Context mContext;
//    private ProfileMovieBindingHolder.AddToFavoritesOrWatchListClickInterface mClickListener;



    public ProfileMovieAdapter(Context context) {
        mContext = context;
        mMovies = new ArrayList<>();
//        mClickListener = clickInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemProfileMovieBinding itemProfileMovieBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_profile_movie,
                parent,
                false);
        return new ProfileMovieAdapter.ProfileMovieBindingHolder(itemProfileMovieBinding);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemProfileMovieBinding itemProfileMovieBinding = ((ProfileMovieBindingHolder) holder).binding;
        itemProfileMovieBinding.setMovieViewModel(new ProfileMovieViewModel(mContext, mMovies.get(position)));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setItems(List<Movie> movie) {
        mMovies = movie;
        notifyDataSetChanged();
    }

    public void addItem(Movie movie) {
        mMovies.add(movie);
        notifyDataSetChanged();
    }

    public static class ProfileMovieBindingHolder extends RecyclerView.ViewHolder {
        private ItemProfileMovieBinding binding;
//        private ProfileMovieBindingHolder.AddToFavoritesOrWatchListClickInterface mClickListener;

        public ProfileMovieBindingHolder(ItemProfileMovieBinding binding) {
            super(binding.movieFrame);
//            mClickListener = clickListener;
            this.binding = binding;
//            this.binding.favorite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mClickListener.onFavClicked(getAdapterPosition());
//                }
//            });
//            this.binding.watchlist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mClickListener.onWatchClicked(getAdapterPosition());
//                }
//            });
        }

//        public interface AddToFavoritesOrWatchListClickInterface {
//            public void onFavClicked(int position);
//            public void onWatchClicked(int position);
//        }
    }

}