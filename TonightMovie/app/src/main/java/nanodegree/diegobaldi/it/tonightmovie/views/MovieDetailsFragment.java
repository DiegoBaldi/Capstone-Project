package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.adapters.ReviewAdapter;
import nanodegree.diegobaldi.it.tonightmovie.adapters.VideoAdapter;
import nanodegree.diegobaldi.it.tonightmovie.api.TheMovieDBApiEndpointInterface;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentMovieBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieDetails;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.MovieDetailsViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "movieId";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private int mMovieId;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mVideosRecyclerView, mReviewsRecyclerView;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetailsFragment newInstance(int movieId, String param2) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, movieId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie, container, false);
        FragmentMovieBinding fragmentMovieBinding = DataBindingUtil.bind(rootView);
        mVideosRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mVideosRecyclerView.setLayoutManager(linearLayoutManager);
        mVideoAdapter = new VideoAdapter(getActivity());
        mVideosRecyclerView.setAdapter(mVideoAdapter);
        mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviews_recyclerview);
        LinearLayoutManager linearLayoutManagerReview = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(linearLayoutManagerReview);
        mReviewAdapter = new ReviewAdapter(getActivity());
        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
        getMovieDetails(mMovieId, fragmentMovieBinding);
        return rootView;
    }

    private void getMovieDetails(final int movieId, final FragmentMovieBinding fragmentMovieBinding) {
        TheMovieDBApiEndpointInterface theMovieDBService = TonightMovieApp.getRetrofit().create(TheMovieDBApiEndpointInterface.class);
        Locale defaultLocale = Locale.getDefault();
        Call<MovieDetails> call= theMovieDBService.getMovieDetails(movieId, "f9e991dc40d898e632bfaeee97371573", String.format(getString(R.string.iso_language), defaultLocale.getLanguage(), defaultLocale.getCountry()));
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if(response.code()==200) {
                    MovieDetails movieResult = response.body();
                    fragmentMovieBinding.setMovieDetailsViewModel(new MovieDetailsViewModel(getActivity(), movieResult));
                    if(movieResult.getVideos()!=null)
                        mVideoAdapter.setItems(movieResult.getVideos());
                    if(movieResult.getReviews()!=null)
                        mReviewAdapter.setItems(movieResult.getReviews());
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
//                mMoviesProgress.setVisibility(View.GONE);
                Log.d(LOG_TAG, t.getLocalizedMessage());
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
