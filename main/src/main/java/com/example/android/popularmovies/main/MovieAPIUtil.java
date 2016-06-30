package com.example.android.popularmovies.main;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
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
    private static final String posterRoot = "http://image.tmdb.org/t/p/w185";
    private static final String PARAM_APIKEY = "api_key";

    private static final String MT_RESULTS = "results";
    private static final String MT_POSTER_PATH = "poster_path";
    private static final String MT_TITLE = "title";
    private static final String MT_MOVIE_ID = "id";
    private static final String MT_RELEASE_DATE = "release_date";
    private static final String MT_VOTE_AVERAGE = "vote_average";
    private static final String MT_SYNOPSIS = "overview";

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

    public static  MovieThumb[] getMovies(boolean byPopularity, String apiKey) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieInfoString = null;
        MovieThumb[] allThumbs = null;

        try {
            Uri geller = Uri.parse(byPopularity?popularityRoot:topRatedRoot).buildUpon()
                    .appendQueryParameter(PARAM_APIKEY, apiKey)
                    .build();
            Log.v(LOG_TAG, geller.toString());
            URL url = new URL(geller.toString());

            movieInfoString = getURL(url);
            if(movieInfoString == null)
                return null;
            allThumbs = getMoviesFromJSON(movieInfoString);
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
        return allThumbs;
    }

    public static  MovieDetail getMovie(long movieId, String apiKey) {

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
    private static MovieThumb[] getMoviesFromJSON(String movieString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        List<MovieThumb> allThumbs = new LinkedList<MovieThumb>();

        JSONObject movieJson = new JSONObject(movieString);
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
        MovieThumb[] thumbarray = new MovieThumb[allThumbs.size()];
        allThumbs.toArray(thumbarray);
        return thumbarray;
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

}
