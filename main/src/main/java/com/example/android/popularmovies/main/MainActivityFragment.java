package com.example.android.popularmovies.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.android.popularmovies.main.entity.MovieThumb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stuartwhitcombe on 29/06/16.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = "PopularMoviesMAF";
    private MovieThumbAdapter mtAdapter;
    Spinner spinner;
    private int mPosition =  ListView.INVALID_POSITION;
    private GridView mGridView = null;
    private String oldSortOrder = "";

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        spinner.setSelection(indexOf(sortOrder, R.array.sortOrders));
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    private int indexOf(String sortOrder, int array_id) {
        String[] sortOrderStrings = getResources().getStringArray(array_id);
        for(int i = 0; i < sortOrderStrings.length; ++i) {
            if(sortOrder.equals(sortOrderStrings[i])) {
                return i + 1; // indexes in spinners are 1 based...
            }
        }
        Log.e(LOG_TAG, "Invalid sort order, " + sortOrder + " passed to indexOf in MainActivityFragment");
        throw new IllegalStateException("Invalid sort order, " + sortOrder + " passed to indexOf in MainActivityFragment");
    }

    private String sortOrder;

    public MainActivityFragment() {

        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        if(mPosition != ListView.INVALID_POSITION) {
            mPosition = mGridView.getFirstVisiblePosition();
            outState.putInt(getString(R.string.instance_state_position), mPosition);
            Log.i(LOG_TAG, "Saving movie position of " + mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        if(mPosition != GridView.INVALID_POSITION && mGridView != null) {
            Log.i(LOG_TAG, "Smooth scrolling to " + mPosition);
            mGridView.smoothScrollToPosition(mPosition);
            mGridView.scrollTo(1, mPosition);
            Log.i(LOG_TAG, "First visible item is " + mGridView.getFirstVisiblePosition());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sortOrder = getString(R.string.sort_order_popular);
        oldSortOrder = sortOrder;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sort, menu);
        MenuItem item = menu.findItem(R.id.sort_spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sortOrders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(indexOf(sortOrder, R.array.sortOrders));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortOrder = spinner.getItemAtPosition(position).toString();
                if(!sortOrder.equals(oldSortOrder)) {
                    getMovies("1");
                    Log.i(LOG_TAG, "getMovies called from spinner.onItemSelected");
                    mPosition = 1;
                    oldSortOrder = sortOrder;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        MenuItem menuItem = menu.findItem(R.id.action_share);
        if(menuItem != null) {
            // Get the provider and hold onto it to set/change the share intent.

            ((Callback)getActivity()).setProvider((ShareActionProvider) MenuItemCompat.getActionProvider(menuItem));
        }

    }


//        MenuItem countryItem = menu.findItem(R.id.country_spinner);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//        country = prefs.getString(getString(R.string.country_pref), getString(R.string.country_default));
//        countrySpinner = (Spinner) MenuItemCompat.getActionView(countryItem);
//
//        ArrayAdapter<CharSequence> ctry_adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.countries, android.R.layout.simple_spinner_item);
//        ctry_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        countrySpinner.setAdapter(ctry_adapter);
//        countrySpinner.setSelection(indexOf(country, R.array.countries));
//        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                country = countrySpinner.getItemAtPosition(position).toString();
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//                prefs.edit().putString(getString(R.string.country_pref), country).apply();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // do nothing
//            }
//        });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if(mtAdapter == null)
            mtAdapter = new MovieThumbAdapter(getActivity(), new ArrayList<MovieThumb>());
        else {
            Log.i(LOG_TAG, "mtAdapter not null and has " + mtAdapter.getCount() + " items");
            Log.i(LOG_TAG, "mPosition is " + mPosition);
        }
        mGridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        mGridView.setAdapter(mtAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieThumb theMovie = (MovieThumb) mtAdapter.getItem(position);
                if (theMovie != null) {
                    ((Callback) getActivity()).onItemSelected(theMovie);
                }
                mPosition = position;
            }
        });

        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                if (sortOrder.equals(getString(R.string.sort_order_popular)) || sortOrder.equals(getString(R.string.sort_order_top_rated))) {
                    Log.i(LOG_TAG, "mGrigView onScrollListener calling getMovies with " + page);
                    getMovies(String.valueOf(page));
                    // or customLoadMoreDataFromApi(totalItemsCount);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                } else
                    return false;
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        if(mtAdapter.getCount() == 0) {
            Log.i(LOG_TAG, "getMovies called from onStart");
            getMovies("1");
        }
    }

    private void getMovies(String page) {
        new FetchMoviesTask().execute(sortOrder, page);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<MovieThumb>> {
        private boolean clear = true;

        @Override
        protected void onPostExecute(List<MovieThumb> thumbs) {
            if(thumbs != null) {
                if(clear) {
                    mtAdapter.clear();
                }
                for(MovieThumb thumb : thumbs) {
                    mtAdapter.add(thumb);
                }
                //mtAdapter.addAll(thumbs);  API 11 apparently....
            }
        }

        @Override
        protected List<MovieThumb> doInBackground(String... params) {
            if(params.length != 2) {
                return null;
            }
            String sortOrder = params[0];
            String page = params[1];
            clear = page.equals("1");
            return MovieAPIUtil.getMovies(getContext(), getResources(), sortOrder, page, getString(R.string.themoviedb_apikey));
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MovieThumb movieThumb);
        public void setProvider(ShareActionProvider sharedProvider);
    }


}
