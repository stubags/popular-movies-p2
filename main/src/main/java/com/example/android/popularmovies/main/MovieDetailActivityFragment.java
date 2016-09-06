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
    private TextView mTitle;
    private MovieImageView mImage;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mAverageVote;
    private boolean mFavourite = false;
    private boolean mWantIt = false;
    private boolean mOwnIt = false;
    private MovieDetail mMovie = null;
    private Bitmap mPoster = null;
    private AtomicBoolean mPosterIsReady = new AtomicBoolean(false);
    private RelativeLayout mDetailLayout;
    private Context mContext;

    private static final int TRAILER_BASE_VIEW_ID = 250;
    private static final int REVIEW_BASE_VIEW_ID = 500;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        mTitle = (TextView)theView.findViewById(R.id.movie_title);
        mImage = (MovieImageView)theView.findViewById(R.id.movie_poster);
        mImage.setInGrid(false);

        mDetailLayout = (RelativeLayout)theView.findViewById(R.id.detail_relative_layout);

        // Get the movie from the provider if we can
        MovieDetailPlusImage movie = MovieAPIUtil.getMovieFromProvider(mContext, movieId, getString(R.string.themoviedb_apikey));

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
                if(mPosterIsReady.get()) {
                    mFavourite = !mFavourite;
                    favouritesStar.setImageResource(mFavourite ? R.mipmap.ic_thumgold : R.mipmap.ic_thumb);
                    // is it in the database?
                    if(MovieAPIUtil.getMovieFromProvider(mContext, movieId, getString(R.string.themoviedb_apikey)) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavouriteMovieEntry.COLUMN_FAVOURITE, mFavourite ? 1 : 0);
                        mContext.getContentResolver().update(MovieContract.FavouriteMovieEntry.CONTENT_URI, values,
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
                if(mPosterIsReady.get()) {
                    mWantIt = !mWantIt;
                    wantItStar.setImageResource(mWantIt ? R.mipmap.ic_wantred : R.mipmap.ic_wantit);
                    // is it in the database?
                    if(MovieAPIUtil.getMovieFromProvider(mContext, movieId, getString(R.string.themoviedb_apikey)) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavouriteMovieEntry.COLUMN_WANT_IT, mWantIt ? 1 : 0);
                        mContext.getContentResolver().update(MovieContract.FavouriteMovieEntry.CONTENT_URI, values,
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
                if(mPosterIsReady.get()) {
                    mOwnIt = !mOwnIt;
                    ownItStar.setImageResource(mOwnIt ? R.mipmap.ic_owngreen : R.mipmap.ic_own);
                    // is it in the database?
                    if(MovieAPIUtil.getMovieFromProvider(mContext, movieId, getString(R.string.themoviedb_apikey)) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT, mOwnIt ? 1 : 0);
                        mContext.getContentResolver().update(MovieContract.FavouriteMovieEntry.CONTENT_URI, values,
                                MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID + " = ? ", new String[] {Long.toString(movieId)});
                    }
                    else {
                        insertNewMovieInDB();
                    }
                    // TODO delete old non-favourite, non-owned, non-wanted....
                }
            }
        });

        mSynopsis = (TextView)theView.findViewById(R.id.synopsis);
        mReleaseDate = (TextView)theView.findViewById(R.id.release_date);
        mAverageVote = (TextView)theView.findViewById(R.id.average_vote);

        if(movie == null)
            // go to the api if it's not in the database
            getMovie();
        else {
            mMovie = movie;
            mTitle.setText(movie.getTitle());
            movie.loadImageInto(mImage, mContext);
            mPoster = movie.getPoster();
            mPosterIsReady.set(true);
            mSynopsis.setText(movie.getSynopsis());
            mReleaseDate.setText(movie.getReleaseDate());
            mAverageVote.setText(String.valueOf(movie.getVoteAverage()));
            ((Callback)getActivity()).onMovieSelected(movie.getTitle() + ", " + MovieAPIUtil.buildMovieUri(movieId));

        }

        // either way, get the trailers, reviews and release dates
        getExtraInfo();
        return theView;
    }

    public void makeToast(String text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }

    private void insertNewMovieInDB() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID, movieId);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_FAVOURITE, mFavourite ? 1 : 0);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT, mOwnIt ? 1 : 0);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_WANT_IT, mWantIt ? 1 : 0);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mPoster.compress(Bitmap.CompressFormat.PNG, 0, stream);
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_POSTER, stream.toByteArray());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_TITLE, mMovie.getTitle());
        values.put(MovieContract.FavouriteMovieEntry.COLUMN_USER_RATING, mMovie.getVoteAverage());
        mContext.getContentResolver().insert(MovieContract.FavouriteMovieEntry.CONTENT_URI, values);
    }

    private void getMovie() {
        if(movieId != -1)
            new FetchMoviesTask().execute(movieId);
    }

    private void getExtraInfo() {
        if(movieId != -1) {
            new FetchTrailersTask().execute(movieId);
            new FetchReviewsTask().execute(movieId);
            new FetchReleaseDatesTask().execute(movieId);
        }
    }

    private class BitmapAndImageTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImage.setImageBitmap(bitmap);
            mPoster = bitmap;
            mPosterIsReady.set(true);
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
            try {
                if (md != null) {
                    mMovie = md;
                    mTitle.setText(md.getTitle());
                    BitmapAndImageTarget target = new BitmapAndImageTarget();
                    Picasso.with(mContext).load(md.getUri()).into(target);
                    mSynopsis.setText(md.getSynopsis());
                    mReleaseDate.setText(md.getReleaseDate());
                    mAverageVote.setText(String.valueOf(md.getVoteAverage()));
                    ((Callback) getActivity()).onMovieSelected(md.getTitle() + ", " + MovieAPIUtil.buildMovieUri(md.getId()));
                }
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to post execute the Fetch Movies task!  Maybe the user clicked another movie before this fetch completed...");
            }
        }

        @Override
        protected MovieDetail doInBackground(Long... params) {
            try {
                if (params.length != 1) {
                    return null;
                }
                long movieId = params[0];
                return MovieAPIUtil.getMovie(mContext, movieId, getString(R.string.themoviedb_apikey));
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to doInBackground the Fetch Movies task!  Maybe the user clicked another movie before this fetch completed...");
                return null;
            }
        }
    }

    public class FetchTrailersTask extends AsyncTask<Long, Void, List<MovieTrailer>> {

        @Override
        protected void onPostExecute(List<MovieTrailer> trailers) {
            try {
                if (trailers != null) {
                    int nextId = TRAILER_BASE_VIEW_ID;

                    for (final MovieTrailer trailer : trailers) {
                        Button trailerButton = new Button(mContext);
                        trailerButton.setText(trailer.getName());
                        trailerButton.setId(nextId++);
                        if (Build.VERSION.SDK_INT < 23) {
                            trailerButton.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
                        } else {
                            trailerButton.setTextAppearance(android.R.style.TextAppearance_Medium);
                        }
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.BELOW, nextId == 251 ? R.id.textView5 : nextId - 2);
                        mDetailLayout.addView(trailerButton, layoutParams);

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
                    TextView reviewText = (TextView) mDetailLayout.findViewById(R.id.textView6);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reviewText.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, nextId - 1);
                    reviewText.setLayoutParams(layoutParams);
                    //mtAdapter.addAll(thumbs);  API 11 apparently....
                }
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to post execute the Fetch Trailers task!  Maybe the user clicked another movie before this fetch completed...");
            }
        }

        @Override
        protected List<MovieTrailer> doInBackground(Long... params) {
            try {
                if (params.length != 1) {
                    return null;
                }
                Long movieId = params[0];
                return MovieAPIUtil.getTrailers(mContext, getResources(), movieId, getString(R.string.themoviedb_apikey));
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to doInBackground the Fetch Trailers task!  Maybe the user clicked another movie before this fetch completed...");
                return null;
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<Long, Void, List<MovieReview>> {

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            try {
                if (reviews != null) {
                    int nextId = REVIEW_BASE_VIEW_ID;

                    for (MovieReview review : reviews) {
                        TextView textViewAuthor = new TextView(mContext);
                        textViewAuthor.setText(review.getAuthor());
                        textViewAuthor.setId(nextId++);
                        if (Build.VERSION.SDK_INT < 23) {
                            textViewAuthor.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
                        } else {
                            textViewAuthor.setTextAppearance(android.R.style.TextAppearance_Medium);
                        }
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.BELOW, nextId == 501 ? R.id.textView6 : nextId - 2);

                        mDetailLayout.addView(textViewAuthor, layoutParams);
                        TextView textView = new TextView(mContext);
                        textView.setText(review.getContent());
                        textView.setId(nextId++);
                        layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.BELOW, nextId - 2);
                        mDetailLayout.addView(textView, layoutParams);

                        //reviewAdapter.add(review);
                    }
                    //mtAdapter.addAll(thumbs);  API 11 apparently....
                }
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to post execute the Fetch Reviews task!  Maybe the user clicked another movie before this fetch completed...");
            }
        }

        @Override
        protected List<MovieReview> doInBackground(Long... params) {
            try {
                if (params.length != 1) {
                    return null;
                }
                Long movieId = params[0];
                return MovieAPIUtil.getReviews(mContext, getResources(), movieId, getString(R.string.themoviedb_apikey));
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to doInBackground the Fetch Reviews task!  Maybe the user clicked another movie before this fetch completed...");
                return null;
            }
        }
    }

    // Wanted to order dvd's that I want by dvd release date - unfortunately the api info in this area isn't good
    // even when keeping the region as US.

    public class FetchReleaseDatesTask extends AsyncTask<Long, Void, MovieReleaseDates> {

        @Override
        protected void onPostExecute(MovieReleaseDates releaseDates) {
            try {
                if (releaseDates != null) {
                    String country = PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_country_key), getString(R.string.pref_country_default));
                    String dvdRelease = releaseDates.getRelDate(country, 5);
                    TextView dvdReleaseDate = (TextView) mDetailLayout.findViewById(R.id.dvd_release_date);
                    if (dvdRelease != null) {
                        // set it on the screen
                        dvdReleaseDate.setText(dvdRelease.substring(0, 10));
                    } else {
                        // set it to unknown
                        dvdReleaseDate.setText(getString(R.string.unknown_date));
                    }
                }
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to post execute the Fetch Releases task!  Maybe the user clicked another movie before this fetch completed...");
            }
        }

        @Override
        protected MovieReleaseDates doInBackground(Long... params) {
            try {
                if (params.length != 1) {
                    return null;
                }
                Long movieId = params[0];
                return MovieAPIUtil.getReleaseDates(mContext, getResources(), movieId, getString(R.string.themoviedb_apikey));
            }
            catch(Exception e) {
                Log.e(LOG_TAG, "Something went wrong when I tried to doInBackground the Fetch Releases task!  Maybe the user clicked another movie before this fetch completed...");
                return null;
            }
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
