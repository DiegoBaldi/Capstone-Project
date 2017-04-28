package nanodegree.diegobaldi.it.tonightmovie.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.adapters.AdviceAdapter;
import nanodegree.diegobaldi.it.tonightmovie.models.LikeStatus;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieAdvicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieAdvicesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_PARAM1 = "movieId";
    private static final int POSTER_WIDTH = 160;
    private static final String BUNDLE_ADVICES = "advices";
    private static final String BUNDLE_ADVICES_COUNT = "advicesCount";
    private static final String BUNDLE_ADVICES_TAKEN = "advicesTaken";
    private static final String BUNDLE_MOVIE_ID = "movieId";

    private int mMovieId;
    private List<Movie> mAdvices = new ArrayList<>();
    private long mCount;
    private long mMovieAdviceTaken;

    private TextView mEmptyAdvice;
    private ProgressBar mAdviceProgress;

    private AdviceAdapter<Movie> mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MovieAdvicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Parameter 1.
     * @return A new instance of fragment MovieAdvicesFragment.
     */
    public static MovieAdvicesFragment newInstance(int movieId) {
        MovieAdvicesFragment fragment = new MovieAdvicesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_advices, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.advice_recyclerview);
        mEmptyAdvice = (TextView) rootView.findViewById(R.id.empty_advice);
        mAdviceProgress = (ProgressBar) rootView.findViewById(R.id.advice_progress);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //Read in https://developer.android.com/training/material/lists-cards.html to improve performance
        recyclerView.setHasFixedSize(false);
        //Code to set a GridLayoutManager with columns each row instead of the default linearLayout
        mAdapter = new AdviceAdapter<>(getActivity(), true, mMovieId);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getSpanSize());
        recyclerView.setLayoutManager(mLayoutManager);

        if (savedInstanceState != null) {
            mAdvices = savedInstanceState.getParcelableArrayList(BUNDLE_ADVICES);
            mMovieId = savedInstanceState.getInt(BUNDLE_MOVIE_ID);
            mCount = savedInstanceState.getLong(BUNDLE_ADVICES_COUNT);
            mMovieAdviceTaken = savedInstanceState.getLong(BUNDLE_ADVICES_TAKEN);
            mAdapter.setItems(mAdvices);
        } else {
            getAdvices(mMovieId);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_ADVICES, new ArrayList<>(mAdvices));
        outState.putLong(BUNDLE_ADVICES_COUNT, mCount);
        outState.putLong(BUNDLE_ADVICES_TAKEN, mMovieAdviceTaken);
        outState.putInt(BUNDLE_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    private void getAdvices(final int movieId) {
        mAdvices.clear();
        mAdapter.setItems(mAdvices);
        mEmptyAdvice.setVisibility(View.GONE);
        mAdviceProgress.setVisibility(View.VISIBLE);
        FirebaseUtil.getMovieAdvicesRef(movieId).orderByChild("karma").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mCount = dataSnapshot.getChildrenCount();
                    for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                        Movie movie = movieSnapshot.getValue(Movie.class);
                        movie.setId(Integer.valueOf(movieSnapshot.getKey()));
                        getAdviceLikeStatus(movie);
                        mAdvices.add(movie);
                    }
                } else {
                    mAdviceProgress.setVisibility(View.GONE);
                    mEmptyAdvice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getAdviceLikeStatus(final Movie movie) {
        FirebaseUtil.getUserMovieAdvicesRef().child(String.valueOf(mMovieId)).child(String.valueOf(movie.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMovieAdviceTaken++;
                if (dataSnapshot.hasChildren()) {
                    LikeStatus likeStatus = dataSnapshot.getValue(LikeStatus.class);
                    movie.setLikeStatus(likeStatus.getLiked());
                }
                if (mMovieAdviceTaken == mCount) {
                    sortByKarma(mAdvices);
                    mAdviceProgress.setVisibility(View.GONE);
                    mAdapter.setItems(mAdvices);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sortByKarma(List<Movie> advices) {
        Collections.sort(advices, new Comparator<Movie>() {
            public int compare(Movie movie1, Movie movie2) {
                return Integer.valueOf(movie1.getKarma()).compareTo(movie2.getKarma()); // To compare integer values
            }
        });
    }

    private int getSpanSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int maxElements = 1;
//        if(mTwoPane){
//            maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/TWO_PANE_POSTER_WIDTH);
//
//        } else{
//            maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
//        }
        maxElements = (int) Math.floor((metrics.widthPixels / metrics.density) / POSTER_WIDTH);
        return maxElements;
    }

    @Override
    public void onRefresh() {
        getAdvices(mMovieId);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
