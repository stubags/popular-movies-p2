package com.example.android.popularmovies.main;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.example.android.popularmovies.main.data.MovieContract;
import com.example.android.popularmovies.main.entity.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stuartwhitcombe on 28/06/16.
 */
public class MovieAPIUtil {
    private static final String LOG_TAG = "MovieAPIUtil";
    private static final String popularityRoot = "https://api.themoviedb.org/3/discover/movie?certification_country=US&certification.ltR&sort_by=popularity.desc";
    private static final String topRatedRoot = "https://api.themoviedb.org/3/discover/movie?certification_country=US&certification.ltR&sort_by=vote_average.desc";
    private static final String getMovie = "https://api.themoviedb.org/3/movie/";
    private static final String publicGetMovie = "https://www.themoviedb.org/movie/";
    private static final String trailerPath = "videos";
    private static final String reviewPath = "reviews";
    private static final String releaseDatesPath = "release_dates";
    private static final String posterRoot = "http://image.tmdb.org/t/p/w185";
    private static final String PARAM_APIKEY = "api_key";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_VIDEO_TYPE = "type";

    private static final String MT_TOTAL_PAGES = "total_pages";
    private static final String MT_RESULTS = "results";
    private static final String MT_POSTER_PATH = "poster_path";
    private static final String MT_TITLE = "title";
    private static final String MT_MOVIE_ID = "id";
    private static final String MT_RELEASE_DATE = "release_date";
    private static final String MT_VOTE_AVERAGE = "vote_average";
    private static final String MT_SYNOPSIS = "overview";

    private static final String TRAILER_ID = "id";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_SITE = "site";
    private static final String TRAILER_NAME = "name";

    private static final String REVIEW_TOTAL_PAGES = "total_pages";
    private static final String REVIEW_TOTAL_RESULTS = "total_results";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_AUTHOR = "author";

    private static final String REL_DATES_COUNTRY = "iso_3166_1";
    private static final String REL_DATES = "release_dates";
    private static final String REL_DATE = "release_date";
    private static final String REL_CERT = "certification";
    private static final String REL_DATE_TYPE = "type";

    private static final String[] MOVIE_PROJECTION = {
            MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID,
            MovieContract.FavouriteMovieEntry.COLUMN_FAVOURITE,
            MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT,
            MovieContract.FavouriteMovieEntry.COLUMN_WANT_IT,
            MovieContract.FavouriteMovieEntry.COLUMN_POSTER,
            MovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavouriteMovieEntry.COLUMN_SYNOPSIS,
            MovieContract.FavouriteMovieEntry.COLUMN_TITLE,
            MovieContract.FavouriteMovieEntry.COLUMN_USER_RATING
    };

    private static int MOVIEDB_ID_COL = 0;
    private static int FAVOURITE_COL = 1;
    private static int OWN_IT_COL = 2;
    private static int WANT_IT_COL = 3;
    private static int POSTER_COL = 4;
    private static int RELEASE_DATE_COL = 5;
    private static int SYNOPSIS_COL = 6;
    private static int TITLE_COL = 7;
    private static int USER_RATING_COL = 8;

    private static int totalMoviePages = 0;

    private static String getURL(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return buffer.toString();
    }

    public static  List<MovieThumb> getMovies(Context context, Resources resources, String sortOrder, String page, String apiKey) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieInfoString = null;

        try {
            if(sortOrder.equals(resources.getString(R.string.sort_order_popular)) || sortOrder.equals(resources.getString(R.string.sort_order_top_rated))) {
                boolean byPopularity = sortOrder.equals(resources.getString(R.string.sort_order_popular));
                Uri geller = Uri.parse(byPopularity ? popularityRoot : topRatedRoot).buildUpon()
                        .appendQueryParameter(PARAM_APIKEY, apiKey)
                        .appendQueryParameter(PARAM_PAGE, page)
                        .build();
                Log.v(LOG_TAG, geller.toString());
                URL url = new URL(geller.toString());

                movieInfoString = getURL(url);
                if (movieInfoString == null)
                    return null;
                return getMoviesFromJSON(movieInfoString, page);
            }
            else {
                totalMoviePages = 1;
                // remaining options are database only...
                if(sortOrder.equals(resources.getString(R.string.sort_order_favourites))) {
                    return getMoviesFromCursor(context.getContentResolver().query(MovieContract.FavouriteMovieEntry.favouritesUri, MOVIE_PROJECTION, null, null, null));
                }
                else if(sortOrder.equals(resources.getString(R.string.sort_order_own_it_alphabetical))) {
                    return getMoviesFromCursor(context.getContentResolver().query(MovieContract.FavouriteMovieEntry.ownedUri, MOVIE_PROJECTION, null, null, null));
                }
                else if(sortOrder.equals(resources.getString(R.string.sort_order_want_it_by_release_date))) {
                    return getMoviesFromCursor(context.getContentResolver().query(MovieContract.FavouriteMovieEntry.wantedUri, MOVIE_PROJECTION, null, null, null));
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException from getMoviesFromJSON " + e.getMessage());
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private static List<MovieThumb> getMoviesFromCursor(Cursor cursor) {
        List<MovieThumb> movieList = new LinkedList<MovieThumb>();
        while(cursor.moveToNext()) {
            byte [] image = cursor.getBlob(POSTER_COL);

            MovieDetailPlusImage movieDetail = new MovieDetailPlusImage(
                cursor.getLong(MOVIEDB_ID_COL), cursor.getString(TITLE_COL), cursor.getString(RELEASE_DATE_COL),
                    cursor.getString(SYNOPSIS_COL), cursor.getDouble(USER_RATING_COL), BitmapFactory.decodeByteArray(image, 0, image.length),
                    cursor.getInt(FAVOURITE_COL) == 1, cursor.getInt(WANT_IT_COL) == 1, cursor.getInt(OWN_IT_COL) == 1);
            movieList.add(movieDetail);
        }
        return movieList;
    }

    public static  MovieDetailPlusImage getMovieFromProvider(Context context, long movieId, String apiKey) {
        List<MovieThumb> movies = getMoviesFromCursor(context.getContentResolver().query(MovieContract.FavouriteMovieEntry.buildMovieUri(movieId), MOVIE_PROJECTION, null, null, null));
        if (movies.size() == 1) {
            return (MovieDetailPlusImage) movies.get(0);
        }
        return null;
    }

    public static MovieDetail getMovie(Context context, long movieId, String apiKey) {
        // if the movie is in the database, use that
        MovieDetailPlusImage movie = getMovieFromProvider(context, movieId, apiKey);
        if(movie != null)
            return movie;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieInfoString = null;
        MovieDetail md = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri geller = Uri.parse(getMovie + movieId).buildUpon()
                    .appendQueryParameter(PARAM_APIKEY, apiKey)
                    .build();
            Log.v(LOG_TAG, geller.toString());
            URL url = new URL(geller.toString());

            movieInfoString = getURL(url);
            if(movieInfoString == null)
                return null;
            md = getMovieFromJSON(movieInfoString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException from getMoviesFromJSON " + e.getMessage());
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return md;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private static List<MovieThumb> getMoviesFromJSON(String movieString, String page)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        if(!page.equals("1") && Integer.valueOf(page) > totalMoviePages)
            return null;

        List<MovieThumb> allThumbs = new LinkedList<MovieThumb>();

        JSONObject movieJson = new JSONObject(movieString);
        if(page.equals("1")) {
            totalMoviePages = movieJson.getInt(MT_TOTAL_PAGES);
        }
        JSONArray movieArray = movieJson.getJSONArray(MT_RESULTS);

        for(int i = 0; i < movieArray.length(); ++i) {
            JSONObject movie = movieArray.getJSONObject(i);
            long id = movie.getLong(MT_MOVIE_ID);
            String poster_path = movie.getString(MT_POSTER_PATH);
            String title = movie.getString(MT_TITLE);
            MovieThumb thumb = new MovieThumb(id, title,
                    Uri.parse(posterRoot+poster_path));
            allThumbs.add(thumb);
        }
        return allThumbs;
    }

    private static MovieDetail getMovieFromJSON(String movieString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        JSONObject movie = new JSONObject(movieString);
        long id = movie.getLong(MT_MOVIE_ID);
        String poster_path = movie.getString(MT_POSTER_PATH);
        String title = movie.getString(MT_TITLE);
        String synopsis = movie.getString(MT_SYNOPSIS);
        String releaseDate = movie.getString(MT_RELEASE_DATE);
        double voteAverage = movie.getDouble(MT_VOTE_AVERAGE);
        MovieDetail md = new MovieDetail(id, title, Uri.parse(posterRoot+poster_path), releaseDate, synopsis, voteAverage);
        return md;
    }

    public static List<MovieTrailer> getTrailers(Context context, Resources resources, Long movieId, String apiKey) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailerInfoString = null;

        try {

            Uri geller = Uri.parse(getMovie).buildUpon().appendPath(String.valueOf(movieId)).appendPath(trailerPath)
                    .appendQueryParameter(PARAM_APIKEY, apiKey)
                    .appendQueryParameter(PARAM_VIDEO_TYPE, "trailer")
                    .build();
            Log.v(LOG_TAG, geller.toString());
            URL url = new URL(geller.toString());

            trailerInfoString = getURL(url);
            if (trailerInfoString == null)
                return null;
            return getTrailersFromJSON(trailerInfoString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException from getTrailersFromJSON " + e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private static List<MovieTrailer> getTrailersFromJSON(String trailerString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        List<MovieTrailer> allTrailers = new LinkedList<MovieTrailer>();

        JSONObject trailerJSON = new JSONObject(trailerString);
        JSONArray trailerArray = trailerJSON.getJSONArray(MT_RESULTS);

        for(int i = 0; i < trailerArray.length(); ++i) {
            JSONObject jt = trailerArray.getJSONObject(i);
            String id = jt.getString(TRAILER_ID);
            String key = jt.getString(TRAILER_KEY);
            String site = jt.getString(TRAILER_SITE);
            String name = jt.getString(TRAILER_NAME);
//            long id = movie.getLong(MT_MOVIE_ID);
//            String poster_path = movie.getString(MT_POSTER_PATH);
//            String title = movie.getString(MT_TITLE);
            MovieTrailer trailer = new MovieTrailer(id, key, site, name);
            allTrailers.add(trailer);
        }
        return allTrailers;
    }

    public static List<MovieReview> getReviews(Context context, Resources resources, Long movieId, String apiKey) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ReviewInfoString = null;

        try {

            Uri geller = Uri.parse(getMovie).buildUpon().appendPath(String.valueOf(movieId)).appendPath(reviewPath)
                    .appendQueryParameter(PARAM_APIKEY, apiKey)
                    .build();
            Log.v(LOG_TAG, geller.toString());
            URL url = new URL(geller.toString());

            ReviewInfoString = getURL(url);
            if (ReviewInfoString == null)
                return null;
            return getReviewsFromJSON(ReviewInfoString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException from getMoviesFromJSON " + e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private static List<MovieReview> getReviewsFromJSON(String movieString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        List<MovieReview> allReviews = new LinkedList<MovieReview>();

        JSONObject movieJson = new JSONObject(movieString);
        JSONArray movieArray = movieJson.getJSONArray(MT_RESULTS);

        for(int i = 0; i < movieArray.length(); ++i) {
            JSONObject jt = movieArray.getJSONObject(i);

            String author = jt.getString(REVIEW_AUTHOR);
            String content = jt.getString(REVIEW_CONTENT);

//            long id = movie.getLong(MT_MOVIE_ID);
//            String poster_path = movie.getString(MT_POSTER_PATH);
//            String title = movie.getString(MT_TITLE);

            MovieReview Review = new MovieReview(author, content);
            allReviews.add(Review);
        }
        return allReviews;
    }

    public static MovieReleaseDates getReleaseDates(Context context, Resources resources, Long movieId, String apiKey) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String releaseDateInfoString = null;

        try {

            Uri geller = Uri.parse(getMovie).buildUpon().appendPath(String.valueOf(movieId)).appendPath(releaseDatesPath)
                    .appendQueryParameter(PARAM_APIKEY, apiKey)
                    .build();
            Log.v(LOG_TAG, geller.toString());
            URL url = new URL(geller.toString());

            releaseDateInfoString = getURL(url);
            if (releaseDateInfoString == null)
                return null;
            return getReleaseDatesFromJSON(releaseDateInfoString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException from getMoviesFromJSON " + e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private static MovieReleaseDates getReleaseDatesFromJSON(String movieString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        MovieReleaseDates allReleaseDates = new MovieReleaseDates();

        JSONObject movieJson = new JSONObject(movieString);
        JSONArray movieArray = movieJson.getJSONArray(MT_RESULTS);

        for(int i = 0; i < movieArray.length(); ++i) {

            JSONObject jt = movieArray.getJSONObject(i);
            String isoCountry = jt.getString(REL_DATES_COUNTRY);
            JSONArray relDatesArray = jt.getJSONArray(REL_DATES);
            for(int j = 0; j < relDatesArray.length(); ++j) {
                JSONObject jo = relDatesArray.getJSONObject(j);
                String relDate = jo.getString(REL_DATE);
                int relType = jo.getInt(REL_DATE_TYPE);
                allReleaseDates.add(isoCountry, relType, relDate);
            }
        }
        return allReleaseDates;
    }


    public static String buildMovieUri(long movieId) {
        return publicGetMovie + movieId;
    }
}
