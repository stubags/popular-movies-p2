package com.example.android.popularmovies.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.android.popularmovies.main.data.MovieContract;
import com.example.android.popularmovies.main.entity.MovieThumb;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback, MovieDetailActivityFragment.Callback  {
    private static final String LOG_TAG = "PopularMoviesMA";
    private MainActivityFragment fragment;
    private boolean mTwoPane;
    private final String MOVIE_DETAIL_FRAGMENT_TAG = "DAFT";
    private String mMovieStr = null;
    private ShareActionProvider mShareActionProvider;
    private static final String MOVIE_SHARE_HASHTAG = "#PopularMovies";
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            // in two pane mode, we have to dynamically add/replace the detail fragment using fragment transaction
            // note if this is not null it is probably because it has been rotated and the system has kept the fragment so don't need to add again
            if(savedInstanceState == null) {
                MovieDetailActivityFragment madf = new MovieDetailActivityFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                        new MovieDetailActivityFragment(), MOVIE_DETAIL_FRAGMENT_TAG).commit();
            }
        }
        else {
            getSupportActionBar().setElevation(0f);
            mTwoPane = false;
        }
        if(savedInstanceState != null) {
            String sortOrder = savedInstanceState.getString(getString(R.string.instance_state_sort));
            MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            fragment.setSortOrder(sortOrder);
        }
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.i(LOG_TAG, "onRestoreInstanceState");
        if(savedInstanceState != null) {
            String sortOrder = savedInstanceState.getString(getString(R.string.instance_state_sort));
            MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            fragment.setSortOrder(sortOrder);
            if(savedInstanceState.containsKey(getString(R.string.instance_state_position))) {
                Log.i(LOG_TAG, "Restoring position " + savedInstanceState.getInt(getString(R.string.instance_state_position)));
                fragment.setPosition(savedInstanceState.getInt(getString(R.string.instance_state_position)));
            }
            else {
                Log.i(LOG_TAG, "Position info not found");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        outState.putString(getString(R.string.instance_state_sort), fragment.getSortOrder());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Already inflated by fragment...
        // Retrieve the share menu item
        menuItem = menu.findItem(R.id.action_share);

        return true;
    }

    @Override
    public void onItemSelected(MovieThumb movie) {
        if(mTwoPane) {
            Bundle bundle = new Bundle();

            bundle.putParcelable(getString(R.string.movie_parcelable), MovieContract.FavouriteMovieEntry.buildMovieUri(movie.getId()));

            MovieDetailActivityFragment detailFragment = new MovieDetailActivityFragment();
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, detailFragment, MOVIE_DETAIL_FRAGMENT_TAG).commit();
        }
        else {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.setData(MovieContract.FavouriteMovieEntry.buildMovieUri(movie.getId()));
            startActivity(detailIntent);
        }
    }

    @Override
    public void setProvider(ShareActionProvider sharedProvider) {
        mShareActionProvider = sharedProvider;
    }

    @Override
    public boolean isTwoPane() {
        return mTwoPane;
    }

    @Override
    public void onMovieSelected(String movieUrl) {
        mMovieStr = movieUrl;

        if(mMovieStr != null && mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "I like this on " + MOVIE_SHARE_HASHTAG + ", " + mMovieStr);
        return shareIntent;
    }
}
