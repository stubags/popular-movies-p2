package com.example.android.popularmovies.main;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    private long movieId = -1;
    private TextView title;
    private MovieImageView image;
    private TextView synopsis;
    private TextView releaseDate;
    private TextView averageVote;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) {
            movieId = extras.getLong(MovieThumb.EXTRA_MOVIE_ID);
        }

        View theView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        title = (TextView)theView.findViewById(R.id.movie_title);
        image = (MovieImageView)theView.findViewById(R.id.movie_poster);
        image.setInGrid(false);
        synopsis = (TextView)theView.findViewById(R.id.synopsis);
        releaseDate = (TextView)theView.findViewById(R.id.release_date);
        averageVote = (TextView)theView.findViewById(R.id.average_vote);

        getMovie();
        return theView;
    }

    private void getMovie() {
        new FetchMoviesTask().execute(movieId);
    }

    public class FetchMoviesTask extends AsyncTask<Long, Void, MovieDetail> {

        @Override
        protected void onPostExecute(MovieDetail md) {
            if(md != null) {
                title.setText(md.getTitle());
                Picasso.with(getContext()).load(md.getUri()).into(image);
                synopsis.setText(md.getSynopsis());
                releaseDate.setText(md.getReleaseDate());
                averageVote.setText(String.valueOf(md.getVoteAverage()));
            }
        }

        @Override
        protected MovieDetail doInBackground(Long... params) {

            if(params.length != 1) {
                return null;
            }
            long movieId = params[0];
            return MovieAPIUtil.getMovie(movieId, getString(R.string.themoviedb_apikey));
        }
    }


}
