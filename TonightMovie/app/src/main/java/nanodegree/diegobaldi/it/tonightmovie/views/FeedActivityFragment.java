package nanodegree.diegobaldi.it.tonightmovie.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.adapters.RequestAdapter;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedActivityFragment extends Fragment {
    
    RequestAdapter mAdapter;

    public FeedActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RequestAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        getRequests();

        return rootView;
    }


    public void getRequests() {
        FirebaseUtil.getRequestRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MovieRequest> requests = new ArrayList<MovieRequest>();
                for (DataSnapshot requestSnapshot: dataSnapshot.getChildren()) {
                    MovieRequest request = requestSnapshot.getValue(MovieRequest.class);
                    request.setId(requestSnapshot.getKey());
                    requests.add(request);
                }
                mAdapter.setItems(requests);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
