package com.example.android.popularmovies.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by stuartwhitcombe on 29/06/16.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = "PopularMovies";
    private MovieThumbAdapter mtAdapter;
    Spinner spinner;

    public boolean isSortByPopular() {
        return sortByPopular;
    }

    public void setSortByPopular(boolean sortByPopular) {
        this.sortByPopular = sortByPopular;
        spinner.setSelection(sortByPopular ?1:2);
    }

    private boolean sortByPopular = true;

    public MainActivityFragment() {

        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        spinner.setSelection(sortByPopular ?1:2);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getItemAtPosition(position).toString().equals(getString(R.string.sort_order_popular))) {
                    if(!sortByPopular) {
                        sortByPopular = true;
                        getMovies();
                    }
                }
                else {
                    if(sortByPopular) {
                        sortByPopular = false;
                        getMovies();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);
        mtAdapter = new MovieThumbAdapter(getActivity(), new ArrayList<MovieThumb>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView movieView = (GridView)rootView.findViewById(R.id.movie_grid_view);
        movieView.setAdapter(mtAdapter);

        movieView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieThumb theMovie = (MovieThumb)mtAdapter.getItem(position);
                if(theMovie != null) {
                    Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);
                    detailIntent.putExtra(MovieThumb.EXTRA_MOVIE_ID, ((MovieThumb) mtAdapter.getItem(position)).getId());
                    startActivity(detailIntent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovies();
    }

    private void getMovies() {
        new FetchMoviesTask().execute(sortByPopular);
    }

    public class FetchMoviesTask extends AsyncTask<Boolean, Void, MovieThumb[]> {

        @Override
        protected void onPostExecute(MovieThumb[] thumbs) {
            if(thumbs != null) {
                mtAdapter.clear();
                mtAdapter.addAll(thumbs);
            }
        }

        @Override
        protected MovieThumb[] doInBackground(Boolean... params) {

            if(params.length != 1) {
                return null;
            }
            boolean sortByPopular = params[0];
            return MovieAPIUtil.getMovies(sortByPopular, getString(R.string.themoviedb_apikey));
        }
    }


}
