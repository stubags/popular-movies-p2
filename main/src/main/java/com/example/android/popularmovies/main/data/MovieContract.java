package com.example.android.popularmovies.main.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by stuartwhitcombe on 18/08/16.
 * Defines table and column names and uri details for the movie content provider
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITES = "favourites";
    public static final String PATH_OWNED = "owned";
    public static final String PATH_WANT = "want";

    public static final class FavouriteMovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;

        public static Uri favouritesUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();
        public static Uri ownedUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_OWNED).build();
        public static Uri wantedUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WANT).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "favourites";

        public static final String COLUMN_MOVIEDB_ID = "db_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "coord_lat";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_FAVOURITE = "favourite";
        public static final String COLUMN_OWN_IT = "own_it";
        public static final String COLUMN_WANT_IT = "want_it";

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
