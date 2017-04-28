package nanodegree.diegobaldi.it.tonightmovie.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.adapters.SearchMovieAdapter;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentRequestBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.RequestDetailsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RequestDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REQUEST = "request";
    private static final String LOG_TAG = RequestDetailsFragment.class.getSimpleName();

    private MovieRequest mRequest;
    private List<Movie> mFavMovies = new ArrayList<>();


    private RecyclerView mFavsRecyclerView;
    private SearchMovieAdapter mFavsAdapter;
    private TextView mEmptyFavs;
    private ProgressBar mFavsProgress;

    public RequestDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieRequest Parameter 1.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    public static RequestDetailsFragment newInstance(MovieRequest movieRequest) {
        RequestDetailsFragment fragment = new RequestDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_REQUEST, movieRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequest = getArguments().getParcelable(ARG_REQUEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
        FragmentRequestBinding fragmentRequestBinding = DataBindingUtil.bind(rootView);
        fragmentRequestBinding.setRequestDetailsViewModel(new RequestDetailsViewModel(getActivity(), mRequest));

        mEmptyFavs = (TextView) rootView.findViewById(R.id.empty_favs);
        mFavsProgress = (ProgressBar) rootView.findViewById(R.id.favs_progress);
        mFavsRecyclerView = (RecyclerView) rootView.findViewById(R.id.favs_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFavsRecyclerView.setLayoutManager(linearLayoutManager);
        mFavsAdapter = new SearchMovieAdapter(getActivity(), true, false);
        mFavsRecyclerView.setAdapter(mFavsAdapter);
        getAuthorFavMovies(mRequest.getAuthor().getId());
        return rootView;
    }

    private void getAuthorFavMovies(String authorId) {
        mFavsProgress.setVisibility(View.VISIBLE);
        mEmptyFavs.setVisibility(View.GONE);
        FirebaseUtil.getFavoritesRef(authorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFavsProgress.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                        Movie movie = movieSnapshot.getValue(Movie.class);
                        mFavMovies.add(movie);
                    }
                } else {
                    mEmptyFavs.setVisibility(View.VISIBLE);
                }
                mFavsAdapter.setItems(mFavMovies);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
