package nanodegree.diegobaldi.it.tonightmovie.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.views.FeedActivity;
import nanodegree.diegobaldi.it.tonightmovie.views.MovieActivity;
import nanodegree.diegobaldi.it.tonightmovie.widget.WatchlistAppWidgetProvider;

public class CheckUserLoginStatus extends AppCompatActivity {


    private static final int RC_SIGN_IN = 678;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(TonightMovieApp.getUser()==null){
                        if(user.getPhotoUrl()!=null)
                            TonightMovieApp.setUser(new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString()));
                        else
                            TonightMovieApp.setUser(new User(user.getUid(), user.getDisplayName(), user.getEmail()));

                    }
                    if(getIntent().hasExtra(WatchlistAppWidgetProvider.MOVIE_ID) && getIntent().hasExtra(WatchlistAppWidgetProvider.MOVIE_ORIGINAL_TITLE) && getIntent().hasExtra(WatchlistAppWidgetProvider.MOVIE_POSTER_PATH)){
                        Intent intent = new Intent(getApplicationContext(), MovieActivity.class);
                        int movieId = getIntent().getIntExtra(WatchlistAppWidgetProvider.MOVIE_ID, 0);
                        String moviePosterPath = getIntent().getStringExtra(WatchlistAppWidgetProvider.MOVIE_POSTER_PATH);
                        String movieOriginalTitle = getIntent().getStringExtra(WatchlistAppWidgetProvider.MOVIE_ORIGINAL_TITLE);
                        Movie movie = new Movie(movieId, movieOriginalTitle, moviePosterPath );
                        intent.putExtra("movie", movie);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.login)
                                    .setLogo(R.mipmap.login_logo)
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
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
}
