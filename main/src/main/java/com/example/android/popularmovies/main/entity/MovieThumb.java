package com.example.android.popularmovies.main.entity;

import android.content.Context;
import android.net.Uri;
import com.example.android.popularmovies.main.MovieImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by stuartwhitcombe on 22/06/16.
 */
public class MovieThumb {
    public static final String EXTRA_MOVIE_ID = "movieId";

    private long id;

    private String title;

    private Uri geller;

    public long getId() { return id; }

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return geller;
    }

    public MovieThumb(long id, String title, Uri image) {
        this.id = id;
        this.title = title;
        this.geller = image;
    }


    @Override
    public String toString() {
        return "MovieThumb{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", geller=" + geller +
                '}';
    }

    public void loadImageInto(MovieImageView imageView, Context context) {
        Picasso.with(context).load(getUri()).into(imageView);
    }
}
