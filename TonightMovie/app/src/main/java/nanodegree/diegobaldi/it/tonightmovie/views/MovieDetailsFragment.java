package nanodegree.diegobaldi.it.tonightmovie.views;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.adapters.ReviewAdapter;
import nanodegree.diegobaldi.it.tonightmovie.adapters.VideoAdapter;
import nanodegree.diegobaldi.it.tonightmovie.api.TheMovieDBApiEndpointInterface;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistColumns;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistProvider;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentMovieBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieDetails;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.MovieDetailsViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BUNDLE_MOVIE_ID = "movieId";
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final String BUNDLE_MOVIE_DETAILS = "movieDetails";

    private int mMovieId;
    private MovieDetails mMovieDetails;
    private FragmentMovieBinding mFragmentMovieBinding;

    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    String[] mProjection =
        {
                WatchlistColumns._ID,    // Product class constant for the _ID column name
                WatchlistColumns.THE_MOVIE_DB_ID,   // Product class constant for the product id column name
        };

    private OnRebindCallback<FragmentMovieBinding> delayRebindCallback =
            new OnRebindCallback<FragmentMovieBinding>() {
                @Override
                public boolean onPreBind(FragmentMovieBinding binding) {
                    return false;
                }
            };

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Parameter 1.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    public static MovieDetailsFragment newInstance(int movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getInt(BUNDLE_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie, container, false);
        mFragmentMovieBinding = DataBindingUtil.bind(rootView);
        mFragmentMovieBinding.addOnRebindCallback(delayRebindCallback);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFragmentMovieBinding.trailerRecyclerview.setLayoutManager(linearLayoutManager);
        mVideoAdapter = new VideoAdapter(getActivity());
        mFragmentMovieBinding.trailerRecyclerview.setAdapter(mVideoAdapter);
        LinearLayoutManager linearLayoutManagerReview = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mFragmentMovieBinding.reviewsRecyclerview.setLayoutManager(linearLayoutManagerReview);
        mReviewAdapter = new ReviewAdapter(getActivity());
        mFragmentMovieBinding.reviewsRecyclerview.setAdapter(mReviewAdapter);
        mFragmentMovieBinding.reviewsRecyclerview.setNestedScrollingEnabled(false);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelable(BUNDLE_MOVIE_DETAILS, mMovieDetails);
        outState.putInt(BUNDLE_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null && getActivity()!=null){
            mMovieDetails = savedInstanceState.getParcelable(BUNDLE_MOVIE_DETAILS);
            mMovieId = savedInstanceState.getInt(BUNDLE_MOVIE_ID);
            if(mMovieDetails.getReviews()!=null && mMovieDetails.getReviews().size()>0)
                mReviewAdapter.setItems(mMovieDetails.getReviews());
            else{
                mFragmentMovieBinding.emptyReviews.setVisibility(View.VISIBLE);
            }
            if(mMovieDetails.getVideos()!=null && mMovieDetails.getVideos().size()>0)
                mVideoAdapter.setItems(mMovieDetails.getVideos());
            else {
                mFragmentMovieBinding.emptyTrailer.setVisibility(View.VISIBLE);
            }
            mFragmentMovieBinding.removeOnRebindCallback(delayRebindCallback);
            mFragmentMovieBinding.setMovieDetailsViewModel(new MovieDetailsViewModel(getActivity(), mMovieDetails));
            mFragmentMovieBinding.executePendingBindings();
        }
        else{
            getMovieDetails(mMovieId);
        }
    }

    private void getMovieDetails(final int movieId) {
        mFragmentMovieBinding.emptyTrailer.setVisibility(View.GONE);
        mFragmentMovieBinding.emptyReviews.setVisibility(View.GONE);
        mFragmentMovieBinding.reviewsProgress.setVisibility(View.VISIBLE);
        mFragmentMovieBinding.trailerProgress.setVisibility(View.VISIBLE);
        TheMovieDBApiEndpointInterface theMovieDBService = TonightMovieApp.getRetrofit().create(TheMovieDBApiEndpointInterface.class);
        Locale defaultLocale = Locale.getDefault();
        Call<MovieDetails> call= theMovieDBService.getMovieDetails(movieId, "f9e991dc40d898e632bfaeee97371573", String.format(getString(R.string.iso_language), defaultLocale.getLanguage(), defaultLocale.getCountry()));
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if(response.code()==200) {
                    mMovieDetails = response.body();
                    findIfInWatchlist(mMovieDetails);
                    findIfFavorite(mMovieDetails);
                    mFragmentMovieBinding.reviewsProgress.setVisibility(View.GONE);
                    mFragmentMovieBinding.trailerProgress.setVisibility(View.GONE);
                    if(mMovieDetails.getVideos()!=null && mMovieDetails.getVideos().size()>0)
                        mVideoAdapter.setItems(mMovieDetails.getVideos());
                    else {
                        mFragmentMovieBinding.emptyTrailer.setVisibility(View.VISIBLE);
                    }
                    if(mMovieDetails.getReviews()!=null && mMovieDetails.getReviews().size()>0)
                        mReviewAdapter.setItems(mMovieDetails.getReviews());
                    else{
                        mFragmentMovieBinding.emptyReviews.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
//                mMoviesProgress.setVisibility(View.GONE);
                Log.d(LOG_TAG, t.getLocalizedMessage());
            }
        });
    }

    private void findIfFavorite(final MovieDetails movie) {
        FirebaseUtil.getFavoritesRef(TonightMovieApp.getUser().getId()).child(""+movie.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                    movie.setFavorite(true);
                mFragmentMovieBinding.removeOnRebindCallback(delayRebindCallback);
                mFragmentMovieBinding.setMovieDetailsViewModel(new MovieDetailsViewModel(getActivity(), movie));
                mFragmentMovieBinding.executePendingBindings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void findIfInWatchlist(MovieDetails movie) {
        String[] selectionArgs = {""};
        selectionArgs[0] = ""+movie.getId();
        if(getActivity()!=null){
            Cursor cursor = getActivity().getContentResolver().query(WatchlistProvider.Watchlist.withId(movie.getId()), mProjection, "the_movie_db_id = ?", selectionArgs, null);
            if (null == cursor) {
                movie.setInWatchlist(false);
            } else if (cursor.getCount() < 1) {
                movie.setInWatchlist(false);
                cursor.close();
            } else {
                movie.setInWatchlist(true);
                cursor.close();
            }
        }
    }
}
