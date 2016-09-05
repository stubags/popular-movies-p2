package com.example.android.popularmovies.main.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.main.data.MovieContract.FavouriteMovieEntry;

/**
 * Created by stuartwhitcombe on 18/08/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + FavouriteMovieEntry.TABLE_NAME + " (" +
                FavouriteMovieEntry._ID + " INTEGER PRIMARY KEY ," +
                FavouriteMovieEntry.COLUMN_MOVIEDB_ID + " LONG UNIQUE NOT NULL, " +
                FavouriteMovieEntry.COLUMN_TITLE + " TEXT UNIQUE NOT NULL, " +
                FavouriteMovieEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                FavouriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavouriteMovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                FavouriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                FavouriteMovieEntry.COLUMN_FAVOURITE + " INTEGER NOT NULL," +
                FavouriteMovieEntry.COLUMN_OWN_IT + " INTEGER NOT NULL, " +
                FavouriteMovieEntry.COLUMN_WANT_IT + " INTEGER NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
