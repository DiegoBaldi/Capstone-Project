package nanodegree.diegobaldi.it.tonightmovie.views;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.adapters.ProfileMovieAdapter;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistColumns;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistProvider;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFavoritesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "profile";
    private static final int POSTER_WIDTH = 160;
    private static final String BUNDLE_MOVIES = "movies";
    private static final String BUNDLE_PROFILE = "profile";

    String[] mProjection =
        {
                WatchlistColumns._ID,    // Product class constant for the _ID column name
                WatchlistColumns.THE_MOVIE_DB_ID,   // Product class constant for the product id column name
        };


    private User mProfile;

    private RecyclerView mFavsRecyclerView;
    private ProfileMovieAdapter mFavsAdapter;
    private List<Movie> mFavMovies = new ArrayList<>();


    public ProfileFavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param profile Parameter 1.
     * @return A new instance of fragment ProfileFavoritesFragment.
     */
    public static ProfileFavoritesFragment newInstance(User profile) {
        ProfileFavoritesFragment fragment = new ProfileFavoritesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfile = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_favorites, container, false);
        mFavsRecyclerView = (RecyclerView) rootView.findViewById(R.id.favs_recyclerview);
        ((SimpleItemAnimator) mFavsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getSpanSize());
        mFavsRecyclerView.setLayoutManager(mLayoutManager);
        mFavsAdapter = new ProfileMovieAdapter(getActivity());
        mFavsRecyclerView.setAdapter(mFavsAdapter);
        if(savedInstanceState!=null){
            mFavMovies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
            mProfile = savedInstanceState.getParcelable(BUNDLE_PROFILE);
            mFavsAdapter.setItems(mFavMovies);
        }
        else{
            getProfileFavMovies(mProfile.getId());
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList(BUNDLE_MOVIES, new ArrayList<>(mFavMovies));
        outState.putParcelable(BUNDLE_PROFILE, mProfile);
        super.onSaveInstanceState(outState);
    }

    private void getProfileFavMovies(String id) {
        mFavMovies.clear();
        mFavsAdapter.setItems(mFavMovies);
        FirebaseUtil.getFavoritesRef(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot movieSnapshot : dataSnapshot.getChildren()){
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    movie.setFavorite(true);
                    findIfInWatchlist(movie);
                    mFavMovies.add(movie);
                }
                mFavsAdapter.setItems(mFavMovies);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findIfInWatchlist(Movie movie) {
        String[] selectionArgs = {""};
        selectionArgs[0] = ""+movie.getId();
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

    private int getSpanSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int maxElements = 1;
        maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
        return maxElements;
    }
}
