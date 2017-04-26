package nanodegree.diegobaldi.it.tonightmovie.views;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentNewRequestBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.NewRequestViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewRequestActivityFragment extends Fragment {

    private static final String ARG_PARAM1 = "movie";

    private Movie mMovie;

    public NewRequestActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie Parameter 1.
     * @return A new instance of fragment MovieAdvicesFragment.
     */
    public static NewRequestActivityFragment newInstance(Movie movie) {
        NewRequestActivityFragment fragment = new NewRequestActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_request, container, false);
        FragmentNewRequestBinding fragmentNewRequestBinding = DataBindingUtil.bind(rootView);
        fragmentNewRequestBinding.setNewRequestViewModel(new NewRequestViewModel(getActivity(), mMovie));
        return rootView;
    }
}
