package com.example.android.popularmovies.main.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by stuartwhitcombe on 18/08/16.
 */
public class MovieProvider extends ContentProvider {
    private MovieDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int FAVOURITES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int OWNED_A_Z = 102;
    static final int WANTED_BY_RELEASE = 103;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final String sMovieIdSelection =
            MovieContract.FavouriteMovieEntry.TABLE_NAME +
                    "." + MovieContract.FavouriteMovieEntry.COLUMN_MOVIEDB_ID + " = ? ";

    private static final String sOwnedMovieSelection = MovieContract.FavouriteMovieEntry.TABLE_NAME + "." + MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT + " = 1 ";
    private static final String sOwnedMovieSortOrder = MovieContract.FavouriteMovieEntry.TABLE_NAME + "." + MovieContract.FavouriteMovieEntry.COLUMN_TITLE + " asc ";

    private static final String sWantItMovieSelection =  MovieContract.FavouriteMovieEntry.TABLE_NAME + "." + MovieContract.FavouriteMovieEntry.COLUMN_WANT_IT + " = 1 and " +
        MovieContract.FavouriteMovieEntry.TABLE_NAME + "." + MovieContract.FavouriteMovieEntry.COLUMN_OWN_IT + " = 0 ";
    private static final String sWantItMovieSortOrder = MovieContract.FavouriteMovieEntry.TABLE_NAME + "." + MovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE + " asc ";

    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieQueryBuilder.setTables(
                MovieContract.FavouriteMovieEntry.TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITES, FAVOURITES);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITES + "/*", MOVIE_WITH_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_OWNED, OWNED_A_Z);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_WANT, WANTED_BY_RELEASE);

        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case FAVOURITES:
            case MOVIE_WITH_ID:
            case OWNED_A_Z:
            case WANTED_BY_RELEASE:
                return MovieContract.FavouriteMovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        switch(sUriMatcher.match(uri)) {
            case FAVOURITES:
                retCursor = getAllFavourites(projection, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            case OWNED_A_Z:
                retCursor = getAllFavourites(projection, sOwnedMovieSelection, sOwnedMovieSortOrder);
                break;
            case WANTED_BY_RELEASE:
                retCursor = getAllFavourites(projection, sWantItMovieSelection, sWantItMovieSortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.FavouriteMovieEntry.getMovieIdFromUri(uri);
        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[] {movieId};
        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAllFavourites(String[] projection, String selection, String sortOrder) {
        // might have to add where favourite = 1 at some point if decide not to delete rows when
        // something is un-favourited...
        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVOURITES: {
                long _id = db.insert(MovieContract.FavouriteMovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavouriteMovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        if(selection == null)
            // this makes delete return the number of deleted rows
            selection = "1";
        switch (match) {
            case FAVOURITES:
                rowsDeleted = db.delete(MovieContract.FavouriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                String movieId = MovieContract.FavouriteMovieEntry.getMovieIdFromUri(uri);
                selection = sMovieIdSelection;
                selectionArgs = new String[] {movieId};
                rowsDeleted = db.delete(MovieContract.FavouriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }
        if(rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        // Student: return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match) {
            case FAVOURITES:
                rowsUpdated = db.update(MovieContract.FavouriteMovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                String movieId = MovieContract.FavouriteMovieEntry.getMovieIdFromUri(uri);
                selection = sMovieIdSelection;
                selectionArgs = new String[] {movieId};
                rowsUpdated = db.update(MovieContract.FavouriteMovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        if(rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();

        return rowsUpdated;
    }
}
