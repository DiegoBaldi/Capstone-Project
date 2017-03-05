package nanodegree.diegobaldi.it.tonightmovie.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import nanodegree.diegobaldi.it.tonightmovie.BuildConfig;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.views.FeedActivity;

public class CheckUserLoginStatus extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(TonightMovieApp.getUser()==null)
                        TonightMovieApp.setUser(new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString()));
                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mValueEventListener!=null)
            mDatabase.child("").child(user.getUid()).removeEventListener(mValueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mValueEventListener!=null)
            mDatabase.child("").child(user.getUid()).addValueEventListener(mValueEventListener);
    }
}
