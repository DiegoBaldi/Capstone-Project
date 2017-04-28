package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.iid.FirebaseInstanceId;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

import static nanodegree.diegobaldi.it.tonightmovie.R.id.nav_feed;

public class FeedActivity extends BaseActivity {

    private static final String LOG_TAG = FeedActivity.class.getSimpleName();

    private int selectedGenre = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_feed);
        setToolbarTitle("Movie Master");
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FeedActivityFragment fragment = FeedActivityFragment.newInstance("", getResources().getBoolean(R.bool.tablet));
            fragmentTransaction.replace(R.id.feed_fragment_container, fragment);
            fragmentTransaction.commit();
        }
        if (FirebaseInstanceId.getInstance().getToken() != null && TonightMovieApp.getUser() != null) {
            sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), TonightMovieApp.getUser().getId());
        }
    }

    private void sendRegistrationToServer(String refreshedToken, String userId) {
        FirebaseUtil.getUsersRef().child(userId).child("fcmTokens").child(refreshedToken).setValue(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int navItemToCheck() {
        return R.id.nav_feed;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == nav_feed) {
            // Handle the camera action
            return true;
        }
        return super.onNavigationItemSelected(item);
    }
}
