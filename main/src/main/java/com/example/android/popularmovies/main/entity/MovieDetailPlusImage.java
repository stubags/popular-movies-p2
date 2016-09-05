package com.example.android.popularmovies.main.entity;

import android.content.Context;
import android.graphics.Bitmap;
import com.example.android.popularmovies.main.MovieImageView;


/**
 * Created by stuartwhitcombe on 23/08/16.
 */
public class MovieDetailPlusImage extends MovieDetail {
    private Bitmap bitmap;

    private boolean ownIt;
    private boolean favourite;
    private boolean wantIt;

    public MovieDetailPlusImage(long id, String title, String releaseDate, String synopsis, double voteAverage, Bitmap bitmap, boolean favourite, boolean wantIt, boolean ownIt) {
        super(id, title, null, releaseDate, synopsis, voteAverage);
        this.favourite = favourite;
        this.bitmap = bitmap;
        this.wantIt = wantIt;
        this.ownIt = ownIt;
    }

    @Override
    public void loadImageInto(MovieImageView imageView, Context context) {
        imageView.setImageBitmap(bitmap);
    }

    public Bitmap getPoster() {
        return bitmap;
    }

    public boolean isWanted() {
        return wantIt;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public boolean isOwned() {
        return ownIt;
    }


}
