package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import nanodegree.diegobaldi.it.tonightmovie.adapters.AdviceAdapter;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestAdvicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestAdvicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestAdvicesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "requestId";
    private static final String ARG_PARAM2 = "isAuthor";
    private static final String ARG_PARAM3 = "movieId";
    private static final int POSTER_WIDTH = 160;

    // TODO: Rename and change types of parameters
    private String mRequestId;
    private int mMovieId;
    private boolean mIsAuthor;
    private List<Advice> mAdvices = new ArrayList<>();
    private boolean loggedUserRequest = false;

    private OnFragmentInteractionListener mListener;


    private AdviceAdapter<Advice> mAdapter;

    public RequestAdvicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requestId Parameter 1.
     * @param isAuthor Parameter 2.
     * @param movieId Parameter 3.
     * @return A new instance of fragment MovieAdvicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestAdvicesFragment newInstance(String requestId, boolean isAuthor, int movieId) {
        RequestAdvicesFragment fragment = new RequestAdvicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, requestId);
        args.putBoolean(ARG_PARAM2, isAuthor);
        args.putInt(ARG_PARAM3, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequestId = getArguments().getString(ARG_PARAM1);
            mIsAuthor = getArguments().getBoolean(ARG_PARAM2);
            mMovieId = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_advices, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.advice_recyclerview);
        setupRecyclerView(recyclerView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Read in https://developer.android.com/training/material/lists-cards.html to improve performance
        recyclerView.setHasFixedSize(false);
        //Code to set a GridLayoutManager with columns each row instead of the default linearLayout
        mAdapter = new AdviceAdapter<>(getActivity(), false, mMovieId);
        mAdapter.setRequestAdviceVariables(mIsAuthor, mRequestId);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getSpanSize());
        recyclerView.setLayoutManager(mLayoutManager);
        getAdvices(mRequestId);
    }

    private void getAdvices(String requestId) {
        FirebaseUtil.getRequestAdvicesRef().child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot adviceSnapshot: dataSnapshot.getChildren()){
                    Advice advice = adviceSnapshot.getValue(Advice.class);
                    advice.setId(adviceSnapshot.getKey());
                    mAdvices.add(advice);
                }
                mAdapter.setItems(mAdvices);
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
//        if(mTwoPane){
//            maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/TWO_PANE_POSTER_WIDTH);
//
//        } else{
//            maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
//        }
        maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
        return maxElements;
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
