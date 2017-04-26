package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Intent;
import android.os.Bundle;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.Advice;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;

public class NewAdviceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_new_advice);
        setToolbarTitle("New Advice");

        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            // Add the fragment to the 'fragment_container' FrameLayout
            Intent intent = getIntent();
            if(intent.hasExtra("request") && intent.hasExtra("movie")){
                MovieRequest request = intent.getParcelableExtra("request");
                Advice advice = new Advice();
                advice.setMovie((Movie) intent.getParcelableExtra("movie"));
                advice.setAuthor(TonightMovieApp.getUser());
                getFragmentManager().beginTransaction().add(R.id.fragment_container_new_advice, NewAdviceActivityFragment.newInstance(request, advice)).commit();
            }
        }
    }

    @Override
    protected int navItemToCheck() {
        return -1;
    }
}
