package nanodegree.diegobaldi.it.tonightmovie.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistColumns;
import nanodegree.diegobaldi.it.tonightmovie.data.WatchlistProvider;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;

/**
 * Created by diego on 15/04/2017.
 */

public class ListviewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, Loader.OnLoadCompleteListener<Cursor> {
    private static final String LOG_TAG = ListViewRemoteViewsFactory.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private List<Movie> mWidgetItems = new ArrayList<>();
    private Context mContext;
    private Loader<Cursor> mCursorLoader;
    private int mAppWidgetId;

    private Cursor mCursor;

    // A "projection" defines the columns that will be returned for each row
    String[] mProjection =
            {
                    WatchlistColumns._ID,    // Product class constant for the _ID column name
                    WatchlistColumns.THE_MOVIE_DB_ID,   // Product class constant for the product id column name
            };

    // A "projection" defines the columns that will be returned for each row
    String[] mProjectionWatchlist =
            {
                    WatchlistColumns._ID,    // Product class constant for the _ID column name
                    WatchlistColumns.THE_MOVIE_DB_ID,   // Product class constant for the product id column name
                    WatchlistColumns.POSTER_PATH,   // Product class constant for the product id column name
                    WatchlistColumns.ORIGINAL_TITLE,   // Product class constant for the product id column name
            };

    private int COL_ID = 0;
    private int COL_MOVIE_ID = 1;
    private int COL_POSTER_PATH = 2;
    private int COL_TITLE = 3;

    public ListViewRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // Initialize the data set.
    public void onCreate() {
        // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        mCursorLoader = new CursorLoader(mContext, WatchlistProvider.Watchlist.CONTENT_URI, mProjectionWatchlist, null, null, null);
        mCursorLoader.registerListener(LOADER_ID, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        // Bind data to UI, etc
        mWidgetItems.clear();
        mCursor = data;
        try{
            mCursor.moveToPosition(-1);
            while(mCursor.moveToNext()){
                Movie movie = new Movie(mCursor.getInt(COL_MOVIE_ID), mCursor.getString(COL_TITLE), mCursor.getString(COL_POSTER_PATH));
                mWidgetItems.add(movie);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.widget_listview);
        } catch (Exception exception){
            Log.d(LOG_TAG, "Something went wrong!");
        }
    }

    @Override
    public void onDestroy() {

        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }
//        ...

    // Given the position (index) of a WidgetItem in the array, use the item's text value in
    // combination with the app widget item XML file to construct a RemoteViews object.
    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        Movie movie = mWidgetItems.get(position);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int widgetWidth = getWidgetWidth(appWidgetManager, mAppWidgetId);
        int smallWidth = mContext.getResources().getDimensionPixelSize(R.dimen.widget_watchlist_small_breakpoint_width);
        int layoutId;
        boolean thereIsSpaceForTitle = false;
        if (widgetWidth >= smallWidth) {
            layoutId = R.layout.item_widget;
            thereIsSpaceForTitle = true;
        } else {
            layoutId = R.layout.item_widget_small;
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), layoutId);
        if(thereIsSpaceForTitle)
            rv.setTextViewText(R.id.movie_name, movie.getOriginalTitle());
        Uri uri = Uri.parse("https://image.tmdb.org/t/p/w342" + movie.getPosterPath());
        FutureTarget<Bitmap> futureTarget = Glide.with(mContext)
                .load(uri)
                .asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        try
        {
            rv.setImageViewBitmap(R.id.movie_poster, futureTarget.get());
            rv.setContentDescription(R.id.movie_poster, movie.getOriginalTitle());
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        Glide.clear(futureTarget);

        // Create an Intent to launch MainActivity


        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(WatchlistAppWidgetProvider.MOVIE_ID, movie.getId());
        fillInIntent.putExtra(WatchlistAppWidgetProvider.MOVIE_POSTER_PATH, movie.getPosterPath());
        fillInIntent.putExtra(WatchlistAppWidgetProvider.MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.movie_item, fillInIntent);


        // Return the RemoteViews object.
        return rv;
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  mContext.getResources().getDimensionPixelSize(R.dimen.widget_watchlist_default_width);
    }


    @Override
    public void onDataSetChanged() {
        Log.d(LOG_TAG, "ECCHIME");
    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return mWidgetItems.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


}
