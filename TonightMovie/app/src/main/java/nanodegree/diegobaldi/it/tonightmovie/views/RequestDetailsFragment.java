package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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
import nanodegree.diegobaldi.it.tonightmovie.adapters.SearchMovieAdapter;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentRequestBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.RequestDetailsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "request";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = RequestDetailsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private MovieRequest mRequest;
    private String mParam2;
    private List<Movie> mFavMovies = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private RecyclerView mFavsRecyclerView;
    private SearchMovieAdapter mFavsAdapter;

    public RequestDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieRequest Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestDetailsFragment newInstance(MovieRequest movieRequest, String param2) {
        RequestDetailsFragment fragment = new RequestDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, movieRequest);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequest = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_request, container, false);
        FragmentRequestBinding fragmentRequestBinding = DataBindingUtil.bind(rootView);
        fragmentRequestBinding.setRequestDetailsViewModel(new RequestDetailsViewModel(getActivity(),mRequest));

        mFavsRecyclerView = (RecyclerView) rootView.findViewById(R.id.favs_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFavsRecyclerView.setLayoutManager(linearLayoutManager);
        mFavsAdapter = new SearchMovieAdapter(getActivity(), true, false);
        mFavsRecyclerView.setAdapter(mFavsAdapter);
        getAuthorFavMovies(mRequest.getAuthor().getId());
        return rootView;
    }

    private void getAuthorFavMovies(String authorId) {
        FirebaseUtil.getFavoritesRef().child(authorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot movieSnapshot : dataSnapshot.getChildren()){
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    mFavMovies.add(movie);
                }
                mFavsAdapter.setItems(mFavMovies);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
