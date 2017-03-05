package nanodegree.diegobaldi.it.tonightmovie.views;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ActivityBaseBinding;
import nanodegree.diegobaldi.it.tonightmovie.databinding.NavHeaderFeedBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.util.CheckUserLoginStatus;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.UserViewModel;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout fullLayout;
    private Toolbar toolbar;

    private User mUser;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        /**
         * This is going to be our actual root layout.
         */
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null, false);
        /**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link android.view.ViewStub}
         */


        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        /**
         * Note that we don't pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.
         */
        super.setContentView(fullLayout);

//        setupAuthListener();

        toolbar = (Toolbar) findViewById(R.id.toolbar);



        if (useToolbar())
        {
            setSupportActionBar(toolbar);
        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }

        if (useTabLayout())
        {
            findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.tabs).setVisibility(View.GONE);
        }
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayShowTitleEnabled(false);

//        setUpGoogleApiClient();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpNavView(navigationView);

    }

    private void setUpNavView(NavigationView navigationView) {
        ActivityBaseBinding baseBinding = ActivityBaseBinding.bind(fullLayout);
        mUser = TonightMovieApp.getUser();
        NavHeaderFeedBinding navHeaderFeedBinding = DataBindingUtil.bind(baseBinding.navView.getHeaderView(0));
        navHeaderFeedBinding.setUserViewModel(new UserViewModel(mUser));
    }

    private boolean useToolbar() {
        return true;
    }


    public void setToolbarTitle(String title){
        if(useToolbar()){
            toolbar.setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
//            mUser.setDisplayName("PRvoa genius");
        } else if (id == R.id.nav_settings) {
//            mUser.setEmail("prova super genios");
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_logout){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(BaseActivity.this, CheckUserLoginStatus.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected boolean useTabLayout(){
        return false;
    }
}
