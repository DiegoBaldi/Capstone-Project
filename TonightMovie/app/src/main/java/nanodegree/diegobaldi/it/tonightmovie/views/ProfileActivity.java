package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.User;

import static nanodegree.diegobaldi.it.tonightmovie.R.id.nav_profile;

public class ProfileActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FloatingActionButton mFab;

    private User mProfile;
    private boolean mIsLoggedUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intent = getIntent();
        if (intent.hasExtra("profile")) {
            mProfile = intent.getParcelableExtra("profile");
            if (mProfile.getId().equalsIgnoreCase(TonightMovieApp.getUser().getId())) {
                setToolbarTitle("My Profile");
                mIsLoggedUserProfile = true;
            } else {
                setToolbarTitle(mProfile.getDisplayName());
                mIsLoggedUserProfile = false;
            }
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getResources().getBoolean(R.bool.tablet));

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void launchNewAdviceActivity(Movie movie) {
        Intent intent = new Intent(this, NewRequestActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    protected boolean useTabLayout() {
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsLoggedUserProfile)
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private boolean mIsTablet = false;

        public SectionsPagerAdapter(FragmentManager fm, boolean isTablet) {
            super(fm);
            mIsTablet = isTablet;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return ProfileDetailsFragment.newInstance(mProfile);
                case 1:
                    return FeedActivityFragment.newInstance(mProfile.getId(), mIsTablet);
                case 2:
                    return ProfileFavoritesFragment.newInstance(mProfile);
                case 3:
                    return ProfileWatchlistFragment.newInstance();
                default:
                    return ProfileDetailsFragment.newInstance(mProfile);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (mIsLoggedUserProfile)
                return 4;
            else
                return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mIsLoggedUserProfile) {
                switch (position) {
                    case 0:
                        return "Profile";
                    case 1:
                        return "Requests";
                    case 2:
                        return "Favorites";
                    case 3:
                        return "Watchlist";
                }
            } else {
                switch (position) {
                    case 0:
                        return "Profile";
                    case 1:
                        return "Requests";
                    case 2:
                        return "Favorites";
                }
            }
            return null;
        }
    }

    @Override
    protected int navItemToCheck() {
        return R.id.nav_profile;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == nav_profile) {
            // Handle the camera action
            return true;
        }
        return super.onNavigationItemSelected(item);
    }
}
