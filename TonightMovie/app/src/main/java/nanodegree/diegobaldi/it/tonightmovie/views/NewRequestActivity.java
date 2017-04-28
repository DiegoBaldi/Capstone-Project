package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Intent;
import android.os.Bundle;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;

public class NewRequestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_new_request);
        setToolbarTitle("New Advice");

        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            // Add the fragment to the 'fragment_container' FrameLayout
            Intent intent = getIntent();
            if (intent.hasExtra("movie")) {
                Movie movie = intent.getParcelableExtra("movie");
                getFragmentManager().beginTransaction().add(R.id.fragment_container_new_request, NewRequestActivityFragment.newInstance(movie)).commit();
            }
        }
    }

    @Override
    protected int navItemToCheck() {
        return -1;
    }
}
