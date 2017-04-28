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
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.LikeStatus;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RequestAdvicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestAdvicesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_REQUEST_ID = "requestId";
    private static final String ARG_IS_AUTHOR = "isAuthor";
    private static final String ARG_MOVIE_ID = "movieId";
    private static final int POSTER_WIDTH = 160;
    private static final String ARG_GENRE = "genre";
    private static final String BUNDLE_REQUEST_ID = "requestId";
    private static final String BUNDLE_MOVIE_ID = "movieId";
    private static final String BUNDLE_ADVICES_TAKEN = "advicesTaken";
    private static final String BUNDLE_ADVICES_COUNT = "advicesCount";
    private static final String BUNDLE_ADVICES = "advices";

    private String mRequestId;
    private int mMovieId;
    private boolean mIsAuthor;
    private List<Advice> mAdvices = new ArrayList<>();
    private boolean loggedUserRequest = false;
    private String mGenre;
    private long mCount;
    private long mRequestAdviceTaken;


    private ProgressBar mAdviceProgress;
    private TextView mEmptyAdvices;

    private AdviceAdapter<Advice> mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public RequestAdvicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requestId Parameter 1.
     * @param isAuthor  Parameter 2.
     * @param movieId   Parameter 3.
     * @return A new instance of fragment MovieAdvicesFragment.
     */
    public static RequestAdvicesFragment newInstance(String requestId, boolean isAuthor, int movieId, String genre) {
        RequestAdvicesFragment fragment = new RequestAdvicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_ID, requestId);
        args.putBoolean(ARG_IS_AUTHOR, isAuthor);
        args.putInt(ARG_MOVIE_ID, movieId);
        args.putString(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequestId = getArguments().getString(ARG_REQUEST_ID);
            mIsAuthor = getArguments().getBoolean(ARG_IS_AUTHOR);
            mMovieId = getArguments().getInt(ARG_MOVIE_ID);
            mGenre = getArguments().getString(ARG_GENRE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_advices, container, false);
        mEmptyAdvices = (TextView) rootView.findViewById(R.id.empty_advice);
        mAdviceProgress = (ProgressBar) rootView.findViewById(R.id.advice_progress);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.advice_recyclerview);
        //Read in https://developer.android.com/training/material/lists-cards.html to improve performance
        recyclerView.setHasFixedSize(false);
        //Code to set a GridLayoutManager with columns each row instead of the default linearLayout
        mAdapter = new AdviceAdapter<>(getActivity(), false, mMovieId);
        mAdapter.setRequestAdviceVariables(mIsAuthor, mRequestId, mGenre);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getSpanSize());
        recyclerView.setLayoutManager(mLayoutManager);
        if (savedInstanceState != null) {
            mAdvices = savedInstanceState.getParcelableArrayList(BUNDLE_ADVICES);
            mMovieId = savedInstanceState.getInt(BUNDLE_MOVIE_ID);
            mCount = savedInstanceState.getLong(BUNDLE_ADVICES_COUNT);
            mRequestAdviceTaken = savedInstanceState.getLong(BUNDLE_ADVICES_TAKEN);
            mRequestId = savedInstanceState.getString(BUNDLE_REQUEST_ID);
            mAdapter.setItems(mAdvices);
        } else {
            getAdvices(mRequestId);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_ADVICES, new ArrayList<>(mAdvices));
        outState.putLong(BUNDLE_ADVICES_COUNT, mCount);
        outState.putLong(BUNDLE_ADVICES_TAKEN, mRequestAdviceTaken);
        outState.putInt(BUNDLE_MOVIE_ID, mMovieId);
        outState.putString(BUNDLE_REQUEST_ID, mRequestId);
        super.onSaveInstanceState(outState);
    }

    private void getAdvices(String requestId) {
        mRequestAdviceTaken = 0;
        mAdvices.clear();
        mAdapter.setItems(mAdvices);
        mEmptyAdvices.setVisibility(View.GONE);
        mAdviceProgress.setVisibility(View.VISIBLE);
        FirebaseUtil.getRequestAdvicesRef().child(requestId).orderByChild("karma").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mCount = dataSnapshot.getChildrenCount();
                    for (DataSnapshot adviceSnapshot : dataSnapshot.getChildren()) {
                        Advice advice = adviceSnapshot.getValue(Advice.class);
                        advice.setId(adviceSnapshot.getKey());
                        getAdviceLikeStatus(advice);
                        mAdvices.add(advice);
                    }
                } else {
                    mAdviceProgress.setVisibility(View.GONE);
                    mEmptyAdvices.setText(R.string.no_advices_yet);
                    mEmptyAdvices.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getAdviceLikeStatus(final Advice advice) {
        FirebaseUtil.getUserRequestAdvicesRef().child(mRequestId).child(String.valueOf(advice.getMovie().getId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRequestAdviceTaken++;
                if (dataSnapshot.hasChildren()) {
                    LikeStatus likeStatus = dataSnapshot.getValue(LikeStatus.class);
                    advice.setLikeStatus(likeStatus.getLiked());
                    advice.setTheAnswer(likeStatus.getIsAccepted());
                }
                if (mRequestAdviceTaken == mCount) {
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

    private void sortByKarma(List<Advice> advices) {
        Collections.sort(advices, new Comparator<Advice>() {
            public int compare(Advice advice1, Advice advice2) {
                return Integer.valueOf(advice2.getKarma()).compareTo(advice1.getKarma()); // To compare integer values
            }
        });
    }

    private int getSpanSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int maxElements = 1;
        maxElements = (int) Math.floor((metrics.widthPixels / metrics.density) / POSTER_WIDTH);
        return maxElements;
    }

    @Override
    public void onRefresh() {
        getAdvices(mRequestId);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
