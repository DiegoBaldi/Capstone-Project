package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.adapters.ProfileMovieAdapter;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistColumns;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistProvider;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileWatchlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileWatchlistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int POSTER_WIDTH = 160;
    private static final String LOG_TAG = ProfileWatchlistFragment.class.getSimpleName();
    private static final String BUNDLE_MOVIE_COUNT = "movieCount";
    private static final String BUNDLE_MOVIES = "movies";


    private RecyclerView mWatchRecyclerView;
    private ProfileMovieAdapter mWatchAdapter;
    private List<Movie> mWatchMovies = new ArrayList<>();

    private int mWatchlistCount, mFavoriteInfoTaken;

    private Cursor mCursor;

    // A "projection" defines the columns that will be returned for each row
    String[] mProjection =
            {
                    WatchlistColumns._ID,    // Product class constant for the _ID column name
                    WatchlistColumns.THE_MOVIE_DB_ID,   // Product class constant for the product id column name
            };

    // A "projection" defines the columns that will be returned for each row
    String[] mProjectionWatchlist =
            {
                    WatchlistColumns._ID,    // Product class constant for the _ID column name
                    WatchlistColumns.THE_MOVIE_DB_ID,   // Product class constant for the product id column name
                    WatchlistColumns.POSTER_PATH,   // Product class constant for the product id column name
                    WatchlistColumns.ORIGINAL_TITLE,   // Product class constant for the product id column name
            };

    private int COL_ID = 0;
    private int COL_PRODUCT_ID = 1;
    private int COL_POSTER_PATH = 2;
    private int COL_TITLE = 3;

    public ProfileWatchlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileWatchlistFragment.
     */
    public static ProfileWatchlistFragment newInstance() {
        ProfileWatchlistFragment fragment = new ProfileWatchlistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_watchlist, container, false);
        mWatchRecyclerView = (RecyclerView) rootView.findViewById(R.id.watch_recyclerview);
        ((SimpleItemAnimator) mWatchRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getSpanSize());
        mWatchRecyclerView.setLayoutManager(mLayoutManager);
        mWatchAdapter = new ProfileMovieAdapter(getActivity());
        mWatchRecyclerView.setAdapter(mWatchAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            mWatchlistCount = savedInstanceState.getInt(BUNDLE_MOVIE_COUNT);
            mWatchMovies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
            mWatchAdapter.setItems(mWatchMovies);
        } else if(mWatchMovies.size()>0)
            mWatchAdapter.setItems(mWatchMovies);
        else{
            getLoaderManager().initLoader(1, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList(BUNDLE_MOVIES, new ArrayList<>(mWatchMovies));
        outState.putInt(BUNDLE_MOVIE_COUNT, mWatchlistCount);
        super.onSaveInstanceState(outState);
    }

    private void findIfFavorite(final Movie movie) {
        FirebaseUtil.getFavoritesRef(TonightMovieApp.getUser().getId()).child(""+movie.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFavoriteInfoTaken++;
                if(dataSnapshot.getValue()!=null){
                    movie.setFavorite(true);
                }
                mWatchMovies.add(movie);
                if(mFavoriteInfoTaken==mWatchlistCount)
                    mWatchAdapter.setItems(mWatchMovies);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getSpanSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int maxElements = 1;
        maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
        return maxElements;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), WatchlistProvider.Watchlist.CONTENT_URI, mProjectionWatchlist, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteInfoTaken = 0;
        mWatchMovies.clear();
        mWatchAdapter.setItems(mWatchMovies);
        mCursor = data;
        try{
            mCursor.moveToPosition(-1);
            mWatchlistCount = mCursor.getCount();
            while(mCursor.moveToNext()){
                Movie movie = new Movie(mCursor.getInt(COL_PRODUCT_ID), mCursor.getString(COL_TITLE), mCursor.getString(COL_POSTER_PATH));
                movie.setInWatchlist(true);
                findIfFavorite(movie);
            }
        } catch (Exception exception){
            Log.d(LOG_TAG, "Something went wrong!");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    class WatchlistTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int position = params[0];
            Movie movie = mWatchMovies.get(position);
            if(!movie.isInWatchlist()){
                ContentValues cv = new ContentValues();
                cv.put(WatchlistColumns.THE_MOVIE_DB_ID, movie.getId());
                cv.put(WatchlistColumns.ORIGINAL_TITLE, movie.getOriginalTitle());
                cv.put(WatchlistColumns.POSTER_PATH, movie.getPosterPath());
                cv.put(WatchlistColumns.TIMESTAMP, System.currentTimeMillis());
                getApplicationContext().getContentResolver().insert(WatchlistProvider.Watchlist.CONTENT_URI, cv);
                movie.setInWatchlist(true);
            } else {
                String[] selectionArgs = {""};
                selectionArgs[0] = ""+movie.getId();
                getActivity().getContentResolver().delete(WatchlistProvider.Watchlist.withId(movie.getId()), "the_movie_db_id = ?", selectionArgs);
                movie.setInWatchlist(false);
                mWatchMovies.remove(position);
            }
            return position;
        }

        @Override
        protected void onPostExecute(Integer position) {
            mWatchAdapter.notifyItemRemoved(position);
        }
    }

}
