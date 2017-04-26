package nanodegree.diegobaldi.it.tonightmovie.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.adapters.RequestAdapter;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.EndlessRecyclerOnScrollListener;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_MODE = "profileId";
    private static final String ARG_DEVICE = "isTablet";
    private static final String BUNDLE_GENRE_INDEX = "selectedGenreIndex";
    private static final String BUNDLE_REQUESTS = "requests";
    private static final String BUNDLE_GENRE = "selectedGenre";
    private static final String BUNDLE_LAST_REQUEST = "lastRequestId";
    private static final String LOG_TAG = FeedActivityFragment.class.getSimpleName();
    private RequestAdapter mAdapter;
    private String mProfileId, lastRequestId, mSelectedGenreString;
    private boolean mIsTablet;
    private int mSelectedGenre;

    private static final int MAX_REQUESTS_PER_PAGE = 10;


    private List<MovieRequest> requests = new ArrayList<>();

    private TextView mEmptyRequestsView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    private ProgressBar mProgressBar;

    private long count, requestsCount, requestsTaken;

    public FeedActivityFragment() {
    }

    public static FeedActivityFragment newInstance(String profileId, boolean isTablet) {
        FeedActivityFragment fragment = new FeedActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, profileId);
        args.putBoolean(ARG_DEVICE, isTablet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfileId = getArguments().getString(ARG_MODE);
            mIsTablet = getArguments().getBoolean(ARG_DEVICE, false);
            if(mProfileId==null || mProfileId.equalsIgnoreCase(""))
                setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        mEmptyRequestsView = (TextView) rootView.findViewById(R.id.empty_feed);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.feed_progress);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // use a linear layout manager
        if(!mIsTablet){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);
            mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    getRequests(currentPage);
                }
            };

        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    getRequests(currentPage);
                }
            };
        }

        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);

        // specify an adapter (see also next example)
        mAdapter = new RequestAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState!=null){
            mSelectedGenre = savedInstanceState.getInt(BUNDLE_GENRE_INDEX);
            mSelectedGenreString = savedInstanceState.getString(BUNDLE_GENRE);
            requests = savedInstanceState.getParcelableArrayList(BUNDLE_REQUESTS);
            mProfileId = savedInstanceState.getString(ARG_MODE);
            lastRequestId = savedInstanceState.getString(BUNDLE_LAST_REQUEST);
            mAdapter.setItems(requests);
        }
        else{
            getRequests(0);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("requests", new ArrayList<>(requests));
        outState.putInt("selectedGenreIndex", mSelectedGenre);
        outState.putString("selectedGenre", mSelectedGenreString);
        outState.putString(ARG_MODE, mProfileId);
        if(lastRequestId!=null)
            outState.putString(BUNDLE_LAST_REQUEST, lastRequestId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.filter_title)
                    .items(R.array.filter_genres)
                    .itemsCallbackSingleChoice(mSelectedGenre, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            Log.d(LOG_TAG, text.toString());
                            mSelectedGenre = which;
                            mSelectedGenreString = text.toString().toLowerCase();
                            getRequests(0);
                            return true;
                        }
                    })
                    .positiveText(R.string.ok)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getRequests(int page) {
        requestsTaken = 0;
        if (page == 0) {
            lastRequestId = null;
            requests.clear();
            mAdapter.setItems(requests);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if(mProfileId !=null && !mProfileId.equalsIgnoreCase("")){
            if (page == 0)
                FirebaseUtil.getUserRequestRef(mProfileId).orderByKey().limitToLast(MAX_REQUESTS_PER_PAGE).addListenerForSingleValueEvent(getProfileRequestEventListener(page));
            else
                FirebaseUtil.getUserRequestRef(mProfileId).orderByKey().limitToLast(MAX_REQUESTS_PER_PAGE + 1).endAt(lastRequestId).addListenerForSingleValueEvent(getProfileRequestEventListener(page));
        } else{
            if (mSelectedGenre == 0) {
                if (page == 0)
                    FirebaseUtil.getRequestRef().orderByKey().limitToLast(MAX_REQUESTS_PER_PAGE).addListenerForSingleValueEvent(getNewEventListener(page));
                else
                    FirebaseUtil.getRequestRef().orderByKey().limitToLast(MAX_REQUESTS_PER_PAGE + 1).endAt(lastRequestId).addListenerForSingleValueEvent(getNewEventListener(page));
            } else {
                if (page == 0)
                    FirebaseUtil.getGenreRequestRef(mSelectedGenreString).orderByKey().limitToLast(MAX_REQUESTS_PER_PAGE).addListenerForSingleValueEvent(getNewEventListener(page));
                else
                    FirebaseUtil.getGenreRequestRef(mSelectedGenreString).orderByKey().limitToLast(MAX_REQUESTS_PER_PAGE + 1).endAt(lastRequestId).addListenerForSingleValueEvent(getNewEventListener(page));
            }
        }
    }

    public ValueEventListener getProfileRequestEventListener(final int page){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(page==0){
                    if(mProgressBar!=null && mProgressBar.getVisibility()==View.VISIBLE)
                        mProgressBar.setVisibility(View.GONE);
                }
                if(dataSnapshot.hasChildren()){
                    String requestToDelete = lastRequestId;
                    count = 0;
                    requestsTaken = 0;
                    mEmptyRequestsView.setVisibility(View.GONE);
                    requestsCount = dataSnapshot.getChildrenCount();
                    if(requestsCount !=1 || page==0 ) {
                        for (DataSnapshot requestSnapshot: dataSnapshot.getChildren()) {
                            count++;
                            if (count == 1)
                                lastRequestId = requestSnapshot.getKey();
                            if (requestToDelete == null || !requestToDelete.equals(requestSnapshot.getKey())) {
                                getRequestDetails(requestSnapshot.getKey());
                            }
                        }
                    }

                } else {
                    if(mProgressBar!=null && mProgressBar.getVisibility()==View.VISIBLE)
                        mProgressBar.setVisibility(View.GONE);
                    mEmptyRequestsView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void getRequestDetails(String key) {
        FirebaseUtil.getRequestRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestsTaken++;
                if(dataSnapshot.exists()){
                    MovieRequest request = dataSnapshot.getValue(MovieRequest.class);
                    request.setId(dataSnapshot.getKey());
                    request.setInterested(true);
                    requests.add(request);
                }
                if(requestsTaken==requestsCount){
                    Collections.sort(requests, new Comparator<MovieRequest>() {
                        @Override
                        public int compare(MovieRequest t1, MovieRequest t2) {
                            return Long.valueOf(t2.getCreated()).compareTo(t1.getCreated());
                        }
                    });
                    if(mProgressBar!=null && mProgressBar.getVisibility()==View.VISIBLE)
                        mProgressBar.setVisibility(View.GONE);
                    mAdapter.setItems(requests);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ValueEventListener getNewEventListener(final int page){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(page==0){
                    if(mProgressBar!=null && mProgressBar.getVisibility()==View.VISIBLE)
                        mProgressBar.setVisibility(View.GONE);
                }
                if(dataSnapshot.hasChildren()) {
                    String requestToDelete = lastRequestId;
//                    List<MovieRequest> requestsToAdd = new ArrayList<>();
                    requestsCount = dataSnapshot.getChildrenCount();
                    count = 0;
                    requestsTaken = 0;
                    mEmptyRequestsView.setVisibility(View.GONE);
                    if(requestsCount !=1 || page==0 ){
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            count++;
                            if(count==1)
                                lastRequestId = child.getKey();
                            if(requestToDelete==null || !requestToDelete.equals(child.getKey())){
                                MovieRequest movieRequest = child.getValue(MovieRequest.class);
                                movieRequest.setId(child.getKey());
                                findIfInterested(movieRequest);
                                requests.add(movieRequest);
                            }
                            else {
                                requestsTaken++;
                            }
                        }
                        Collections.sort(requests, new Comparator<MovieRequest>() {
                            @Override
                            public int compare(MovieRequest t1, MovieRequest t2) {
                                return Long.valueOf(t2.getCreated()).compareTo(t1.getCreated());
                            }
                        });
                        mAdapter.setItems(requests);
                    }
                }
                else{
                    if(page==0)
                        mEmptyRequestsView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
    }

    private void findIfInterested(final MovieRequest request) {
        FirebaseUtil.getUserRequestRef(TonightMovieApp.getUser().getId()).child(request.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestsTaken++;
                if(dataSnapshot.getValue()!=null)
                    request.setInterested(true);
                if(requestsTaken == requestsCount){
                    mAdapter.notifyItemChanged(0, requestsCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRefresh() {
        getRequests(0);
        mEndlessRecyclerOnScrollListener.refreshListener();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
