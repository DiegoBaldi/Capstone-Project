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
import android.widget.EditText;

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
    private static final String LOG_TAG = SearchActivity.class.getSimpleName() ;

    private List<Movie> movies = new ArrayList<>();
    private SearchMovieAdapter mAdapter;
    private boolean isHereForARequest = false;

    private Handler mHandler = new Handler();

    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if(getIntent().hasExtra("isHereForAnAdvice")){
            isHereForARequest = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results);
        setupRecyclerView(recyclerView);

        EditText editText = (EditText) findViewById(R.id.search_string);
        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
//                    mClearSearch.setVisibility(View.VISIBLE);
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable = createRunnable(s.toString()), 500);
                } else if(s.toString().equals("")){
//                    mClearSearch.setVisibility(View.GONE);
                    mHandler.removeCallbacks(mRunnable);
                    if(mAdapter!=null){
                        movies.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private Runnable createRunnable(final String paramStr){

        Runnable aRunnable = new Runnable(){
            public void run(){
                getMovies(paramStr);
            }
        };

        return aRunnable;

    }

    private void getMovies(String query) {
        TheMovieDBApiEndpointInterface theMovieDBService = TonightMovieApp.getRetrofit().create(TheMovieDBApiEndpointInterface.class);
        Locale defaultLocale = Locale.getDefault();
        Call<ApiResult<Movie>> call= theMovieDBService.searchMovie("f9e991dc40d898e632bfaeee97371573", String.format(getString(R.string.iso_language), defaultLocale.getLanguage(), defaultLocale.getCountry()), query, 1);
        call.enqueue(new Callback<ApiResult<Movie>>() {
            @Override
            public void onResponse(Call<ApiResult<Movie>> call, Response<ApiResult<Movie>> response) {
                if(response.code()==200) {
                    ApiResult<Movie> movieResult = response.body();
                    movies.clear();
                    if(movieResult.getTotalResults()!=0) {
                        mAdapter.setItems(movieResult.getResults());
                    }
                }

//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ApiResult<Movie>> call, Throwable t) {
//                mMoviesProgress.setVisibility(View.GONE);
                Log.d(LOG_TAG, t.getLocalizedMessage());
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Read in https://developer.android.com/training/material/lists-cards.html to improve performance
        recyclerView.setHasFixedSize(false);

        //Code to set a GridLayoutManager with columns each row instead of the default linearLayout
        mAdapter = new SearchMovieAdapter(this, false, isHereForARequest);
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
        maxElements = (int) Math.floor((metrics.widthPixels/metrics.density)/POSTER_WIDTH);
        return maxElements;
    }
}
