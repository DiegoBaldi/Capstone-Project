package nanodegree.diegobaldi.it.tonightmovie.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.FragmentProfileDetailsBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.ProfileDetailsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDetailsFragment extends Fragment {
    private static final String BUNDLE_PROFILE = "profile";
    private static final String BUNDLE_PROFILE_DETAILS = "profileDetails";

    private User mProfile, mUserDetails;
    FragmentProfileDetailsBinding mFragmentProfileDetailBinding;


    public ProfileDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param profile Parameter 1.
     * @return A new instance of fragment ProfileDetailsFragment.
     */
    public static ProfileDetailsFragment newInstance(User profile) {
        ProfileDetailsFragment fragment = new ProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_PROFILE, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfile = getArguments().getParcelable(BUNDLE_PROFILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_details, container, false);
        mFragmentProfileDetailBinding = DataBindingUtil.bind(rootView);
        if(savedInstanceState!=null){
            mProfile = savedInstanceState.getParcelable(BUNDLE_PROFILE);
            mUserDetails = savedInstanceState.getParcelable(BUNDLE_PROFILE_DETAILS);
            mFragmentProfileDetailBinding.setProfileDetailsViewModel(new ProfileDetailsViewModel(mUserDetails));
        } else {
            getUserDetails(mProfile.getId());
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelable(BUNDLE_PROFILE_DETAILS, mUserDetails);
        outState.putParcelable(BUNDLE_PROFILE, mProfile);
        super.onSaveInstanceState(outState);
    }

    private void getUserDetails(String id) {
        FirebaseUtil.getUsersRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    mUserDetails = dataSnapshot.getValue(User.class);
                    mFragmentProfileDetailBinding.setProfileDetailsViewModel(new ProfileDetailsViewModel(mUserDetails));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
