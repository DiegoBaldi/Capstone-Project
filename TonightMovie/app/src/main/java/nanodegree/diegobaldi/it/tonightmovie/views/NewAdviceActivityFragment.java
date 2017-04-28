package nanodegree.diegobaldi.it.tonightmovie.views;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentNewAdviceBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.NewAdviceViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewAdviceActivityFragment extends Fragment {

    private static final String ARG_REQUEST = "request";
    private static final String ARG_ADVICE = "advice";

    private MovieRequest mRequest;
    private Advice mAdvice;

    public NewAdviceActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param request Parameter 1.
     * @param advice  Parameter 2.
     * @return A new instance of fragment MovieAdvicesFragment.
     */
    public static NewAdviceActivityFragment newInstance(MovieRequest request, Advice advice) {
        NewAdviceActivityFragment fragment = new NewAdviceActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_REQUEST, request);
        args.putParcelable(ARG_ADVICE, advice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequest = getArguments().getParcelable(ARG_REQUEST);
            mAdvice = getArguments().getParcelable(ARG_ADVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_advice, container, false);
        FragmentNewAdviceBinding fragmentNewAdviceBinding = DataBindingUtil.bind(rootView);
        fragmentNewAdviceBinding.setNewAdviceViewModel(new NewAdviceViewModel(getActivity(), mRequest, mAdvice));
        return rootView;
    }
}
