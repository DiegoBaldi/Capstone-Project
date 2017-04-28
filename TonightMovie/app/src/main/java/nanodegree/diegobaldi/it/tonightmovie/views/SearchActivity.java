package nanodegree.diegobaldi.it.tonightmovie.views;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.adapters.SearchMovieAdapter;
import nanodegree.diegobaldi.it.tonightmovie.api.TheMovieDBApiEndpointInterface;
import nanodegree.diegobaldi.it.tonightmovie.models.ApiResult;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends Activity {

    private static final int POSTER_WIDTH = 160;
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();
    private static final String BUNDLE_HERE_FOR_ADVICE = "isHereForAnAdvice";
    private static final String BUNDLE_MOVIES = "movies";

    private List<Movie> mMovies = new ArrayList<>();
    private SearchMovieAdapter mAdapter;
    private boolean mIsHereForARequest = false;
    private TextView mEmptyResults;
    private ProgressBar mSearchProgress;

    private Handler mHandler = new Handler();

    private Runnable mRunnable;
    private boolean mIsFromSavedIstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getIntent().hasExtra(BUNDLE_HERE_FOR_ADVICE)) {
            mIsHereForARequest = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results);
        setupRecyclerView(recyclerView);

        mSearchProgress = (ProgressBar) findViewById(R.id.search_progress);
        mEmptyResults = (TextView) findViewById(R.id.empty_search);

        if (savedInstanceState != null) {
            mIsHereForARequest = savedInstanceState.getBoolean(BUNDLE_HERE_FOR_ADVICE);
            mMovies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
            mAdapter.setItems(mMovies);
            mIsFromSavedIstance = true;
        } else {
            mIsFromSavedIstance = false;
        }

        EditText editText = (EditText) findViewById(R.id.search_string);
        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!mIsFromSavedIstance) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.postDelayed(mRunnable = createRunnable(s.toString()), 500);
                    }
                } else if (s.toString().equals("")) {
                    mHandler.removeCallbacks(mRunnable);
                    if (mAdapter != null) {
                        mMovies.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                }
                mIsFromSavedIstance = false;
            }
        });

    }

    private Runnable createRunnable(final String paramStr) {

        return new Runnable() {
            public void run() {
                getMovies(paramStr);
            }
        };

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_MOVIES, new ArrayList<>(mMovies));
        outState.putBoolean(BUNDLE_HERE_FOR_ADVICE, mIsHereForARequest);
        super.onSaveInstanceState(outState);
    }

    private void getMovies(String query) {
        TheMovieDBApiEndpointInterface theMovieDBService = TonightMovieApp.getRetrofit().create(TheMovieDBApiEndpointInterface.class);
        Locale defaultLocale = Locale.getDefault();
        if (mSearchProgress.getVisibility() != View.VISIBLE)
            mSearchProgress.setVisibility(View.VISIBLE);
        if (mEmptyResults.getVisibility() == View.VISIBLE)
            mEmptyResults.setVisibility(View.GONE);
        Call<ApiResult<Movie>> call = theMovieDBService.searchMovie(getString(R.string.the_movie_db_api_key), String.format(getString(R.string.iso_language), defaultLocale.getLanguage(), defaultLocale.getCountry()), query, 1);
        call.enqueue(new Callback<ApiResult<Movie>>() {
            @Override
            public void onResponse(Call<ApiResult<Movie>> call, Response<ApiResult<Movie>> response) {
                mSearchProgress.setVisibility(View.GONE);
                if (response.code() == 200) {
                    ApiResult<Movie> movieResult = response.body();
                    mMovies.clear();
                    if (movieResult.getTotalResults() != 0) {
                        mMovies = movieResult.getResults();
                        mAdapter.setItems(mMovies);
                    } else {
                        mAdapter.setItems(mMovies);
                        mEmptyResults.setText(getString(R.string.no_results));
                        mEmptyResults.setVisibility(View.VISIBLE);
                    }
                } else {
                    mEmptyResults.setText(getString(R.string.ops));
                    mEmptyResults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<Movie>> call, Throwable t) {
                Log.d(LOG_TAG, t.getLocalizedMessage());
                mSearchProgress.setVisibility(View.GONE);
                mEmptyResults.setText(getString(R.string.ops));
                mEmptyResults.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Read in https://developer.android.com/training/material/lists-cards.html to improve performance
        recyclerView.setHasFixedSize(false);

        //Code to set a GridLayoutManager with columns each row instead of the default linearLayout
        mAdapter = new SearchMovieAdapter(this, false, mIsHereForARequest);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, getSpanSize());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private int getSpanSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int maxElements = 1;
//        if(mTwoPane){
//            maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/TWO_PANE_POSTER_WIDTH);
//
//        } else{
//            maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
//        }
        maxElements = (int) Math.floor((metrics.widthPixels / metrics.density) / POSTER_WIDTH);
        return maxElements;
    }
}
