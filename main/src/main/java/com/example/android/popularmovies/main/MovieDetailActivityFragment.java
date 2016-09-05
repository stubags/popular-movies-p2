package com.example.android.popularmovies.main;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.android.popularmovies.main.data.MovieContract;
import com.example.android.popularmovies.main.entity.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    private static final String LOG_TAG = "MovieDetailAF";
    private long movieId = -1;
    private TextView title;
    private MovieImageView image;
    private TextView synopsis;
    private TextView releaseDate;
    private TextView averageVote;
    private boolean mFavourite = false;
    private boolean mWantIt = false;
    private boolean mOwnIt = false;
    private MovieDetail mMovie = null;
    private Bitmap poster = null;
    private AtomicBoolean posterIsReady = new AtomicBoolean(false);
    private RelativeLayout detailLayout;

    private static final int TRAILER_BASE_VIEW_ID = 250;
    private static final int REVIEW_BASE_VIEW_ID = 500;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args != null) {
            Uri movieUri = args.getParcelable(getString(R.string.movie_parcelable));
            movieId = Long.valueOf(movieUri.getLastPathSegment());
        }

        View theView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        title = (TextView)theView.findViewById(R.id.movie_title);
        image = (MovieImageView)theView.findViewById(R.id.movie_poster);
        image.setInGrid(false);

        detailLayout = (RelativeLayout)theView.findViewById(R.id.detail_relative_layout);

        // Get the movie from the provider if we can
        MovieDetailPlusImage movie = MovieAPIUtil.getMovieFromProvider(getContext(), movieId, getString(R.string.themoviedb_apikey));

        final ImageButton favouritesStar = (ImageButton)theView.findViewById(R.id.favourite_image);
        final ImageButton wantItStar = (ImageButton)theView.findViewById(R.id.wantit_image);
        final ImageButton ownItStar = (ImageButton)theView.findViewById(R.id.ownit_image);
        //ib.setImageResource(R.drawable.abc_ic_star_black_36dp);
        if(movie != null) {
            mFavourite = movie.isFavourite();
            mOwnIt = movie.isOwned();
            mWantIt = movie.isWanted();
        }
        else {
            mFavourite = false;
            mOwnIt = false;
            mWantIt = false;
        }
        favouritesStar.setImageResource(mFavourite ? R.mipmap.ic_thumgold : R.mipmap.ic_thumb);
        wantItStar.setImageResource(mWantIt ? R.mipmap.ic_wantred : R.mipmap.ic_wantit);
        ownItStar.setImageResource(mOwnIt ? R.mipmap.ic_owngreen : R.mipmap.ic_own);

        favouritesStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch it in the db to favourite or not
                if(posterIsReady.get()) {
                    mFavourite = !mFavourite;
                    favouritesStar.setImageResource(mFavourite ? R.mipmap.ic_thumgold : R.mipmap.ic_thumb);
                    // is it in the database?
                    if(MovieAPIUtil.getMovieFromProvider(getContext(), movieId, getString(R.string.themoviedb_apikey)) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavouriteMovieEntry.COLUMN_FAVOURITE, mFavourite ? 1 : 0);
                        getContext().getContentResolver().update(MovieContract.FavouriteMovieEntry.CONTENT_URI, values,
                                MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID + " = ? ", new String[] {Long.toString(movieId)});
                    }
                    else {
                        insertNewMovieInDB();
                    }
                    // TODO delete old non-favourite, non-owned, non-wanted....
                }
            }
        });

        wantItStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch it in the db to favourite or not
                if(posterIsReady.get()) {
                    mWantIt = !mWantIt;
                    wantItStar.setImageResource(mWantIt ? R.mipmap.ic_wantred : R.mipmap.ic_wantit);
                    // is it in the database?
                    if(MovieAPIUtil.getMovieFromProvider(getContext(), movieId, getString(R.string.themoviedb_apikey)) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavouriteMovieEntry.COLUMN_WANT_IT, mWantIt ? 1 : 0);
                        getContext().getContentResolver().update(MovieContract.FavouriteMovieEntry.CONTENT_URI, values,
                                MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID + " = ? ", new String[] {Long.toString(movieId)});
                    }
                    else {
                        insertNewMovieInDB();
                    }
                    // TODO delete old non-favourite, non-owned, non-wanted....
                }
            }
        });

        ownItStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch it in the db to favourite or not
                if(posterIsReady.get()) {
                    mOwnIt = !mOwnIt;
                    ownItStar.setImageResource(mOwnIt ? R.mipmap.ic_owngreen : R.mipmap.ic_own);
                    // is it in the database?
                    if(MovieAPIUtil.getMovieFromProvider(getContext(), movieId, getString(R.string.themoviedb_apikey)) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT, mOwnIt ? 1 : 0);
                        getContext().getContentResolver().update(MovieContract.FavouriteMovieEntry.CONTENT_URI, values,
                                MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID + " = ? ", new String[] {Long.toString(movieId)});
                    }
                    else {
                        insertNewMovieInDB();
                    }
                    // TODO delete old non-favourite, non-owned, non-wanted....
                }
            }
        });

        synopsis = (TextView)theView.findViewById(R.id.synopsis);
        releaseDate = (TextView)theView.findViewById(R.id.release_date);
        averageVote = (TextView)theView.findViewById(R.id.average_vote);

        if(movie == null)
            // go to the api if it's not in the database
            getMovie();
        else {
            mMovie = movie;
            title.setText(movie.getTitle());
            movie.loadImageInto(image, getContext());
            poster = movie.getPoster();
            posterIsReady.set(true);
            synopsis.setText(movie.getSynopsis());
            releaseDate.setText(movie.getReleaseDate());
            averageVote.setText(String.valueOf(movie.getVoteAverage()));
            ((Callback)getActivity()).onMovieSelected(movie.getTitle() + ", " + MovieAPIUtil.buildMovieUri(movieId));

        }

        // either way, get the trailers, reviews and release dates
        getExtraInfo();
        return theView;
    }

    public void makeToast(String text) {
        Context context = getContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void insertNewMovieInDB() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID, movieId);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_FAVOURITE, mFavourite ? 1 : 0);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT, mOwnIt ? 1 : 0);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_WANT_IT, mWantIt ? 1 : 0);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG, 0, stream);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_POSTER, stream.toByteArray());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_TITLE, mMovie.getTitle());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_USER_RATING, mMovie.getVoteAverage());
        getContext().getContentResolver().insert(MovieContract.FavouriteMovieEntry.CONTENT_URI, values);
    }

    private void getMovie() {
        new FetchMoviesTask().execute(movieId);
    }

    private void getExtraInfo() {
        new FetchTrailersTask().execute(movieId);
        new FetchReviewsTask().execute(movieId);
        new FetchReleaseDatesTask().execute(movieId);
    }

    private class BitmapAndImageTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            image.setImageBitmap(bitmap);
            poster = bitmap;
            posterIsReady.set(true);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.e(LOG_TAG, "Bitmap load failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    public class FetchMoviesTask extends AsyncTask<Long, Void, MovieDetail> {

        @Override
        protected void onPostExecute(MovieDetail md) {
            if(md != null) {
                mMovie = md;
                title.setText(md.getTitle());
                BitmapAndImageTarget target = new BitmapAndImageTarget();
                Picasso.with(getContext()).load(md.getUri()).into(target);
                synopsis.setText(md.getSynopsis());
                releaseDate.setText(md.getReleaseDate());
                averageVote.setText(String.valueOf(md.getVoteAverage()));
                ((Callback)getActivity()).onMovieSelected(md.getTitle() + ", " + MovieAPIUtil.buildMovieUri(md.getId()));
            }
        }

        @Override
        protected MovieDetail doInBackground(Long... params) {

            if(params.length != 1) {
                return null;
            }
            long movieId = params[0];
            return MovieAPIUtil.getMovie(getContext(), movieId, getString(R.string.themoviedb_apikey));
        }
    }

    public class FetchTrailersTask extends AsyncTask<Long, Void, List<MovieTrailer>> {

        @Override
        protected void onPostExecute(List<MovieTrailer> trailers) {
            if(trailers != null) {
                int nextId = TRAILER_BASE_VIEW_ID;

                for(final MovieTrailer trailer : trailers) {
                    Button trailerButton = new Button(getContext());
                    trailerButton.setText(trailer.getName());
                    trailerButton.setId(nextId++);
                    if (Build.VERSION.SDK_INT < 23) {
                        trailerButton.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
                    } else {
                        trailerButton.setTextAppearance(android.R.style.TextAppearance_Medium);
                    }
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, nextId == 251 ? R.id.textView5 : nextId - 2);
                    detailLayout.addView(trailerButton, layoutParams);

                    trailerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.youTube_video_root)).
                                buildUpon().appendQueryParameter(getString(R.string.youTube_video_key),
                                trailer.getKey()).build()));
                        }
                    });
                }
                TextView reviewText = (TextView)detailLayout.findViewById(R.id.textView6);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)reviewText.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, nextId - 1);
                reviewText.setLayoutParams(layoutParams);
                //mtAdapter.addAll(thumbs);  API 11 apparently....
            }
        }

        @Override
        protected List<MovieTrailer> doInBackground(Long... params) {
            if(params.length != 1) {
                return null;
            }
            Long movieId = params[0];
            return MovieAPIUtil.getTrailers(getContext(), getResources(), movieId, getString(R.string.themoviedb_apikey));
        }
    }

    public class FetchReviewsTask extends AsyncTask<Long, Void, List<MovieReview>> {

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            if(reviews != null) {
                int nextId = REVIEW_BASE_VIEW_ID;

                for(MovieReview review : reviews) {
                    TextView textViewAuthor = new TextView(getContext());
                    textViewAuthor.setText(review.getAuthor());
                    textViewAuthor.setId(nextId++);
                    if (Build.VERSION.SDK_INT < 23) {
                        textViewAuthor.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
                    } else {
                        textViewAuthor.setTextAppearance(android.R.style.TextAppearance_Medium);
                    }
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, nextId == 501 ? R.id.textView6 : nextId - 2);

                    detailLayout.addView(textViewAuthor, layoutParams);
                    TextView textView = new TextView(getContext());
                    textView.setText(review.getContent());
                    textView.setId(nextId++);
                    layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, nextId - 2);
                    detailLayout.addView(textView, layoutParams);

                    //reviewAdapter.add(review);
                }
                //mtAdapter.addAll(thumbs);  API 11 apparently....
            }
        }

        @Override
        protected List<MovieReview> doInBackground(Long... params) {
            if(params.length != 1) {
                return null;
            }
            Long movieId = params[0];
            return MovieAPIUtil.getReviews(getContext(), getResources(), movieId, getString(R.string.themoviedb_apikey));
        }
    }

    // Wanted to order dvd's that I want by dvd release date - unfortunately the api info in this area isn't good
    // even when keeping the region as US.

    public class FetchReleaseDatesTask extends AsyncTask<Long, Void, MovieReleaseDates> {

        @Override
        protected void onPostExecute(MovieReleaseDates releaseDates) {
            if(releaseDates != null) {
                String country = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.pref_country_key), getString(R.string.pref_country_default));
                String dvdRelease = releaseDates.getRelDate(country, 5);
                TextView dvdReleaseDate = (TextView) detailLayout.findViewById(R.id.dvd_release_date);
                if(dvdRelease != null) {
                    // set it on the screen
                    dvdReleaseDate.setText(dvdRelease.substring(0, 10));
                }
                else {
                    // set it to unknown
                    dvdReleaseDate.setText(getString(R.string.unknown_date));
                }
            }
        }

        @Override
        protected MovieReleaseDates doInBackground(Long... params) {
            if(params.length != 1) {
                return null;
            }
            Long movieId = params[0];
            return MovieAPIUtil.getReleaseDates(getContext(), getResources(), movieId, getString(R.string.themoviedb_apikey));
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
        public void onMovieSelected(String movieUrl);
    }


}
