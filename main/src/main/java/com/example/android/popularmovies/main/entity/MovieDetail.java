package com.example.android.popularmovies.main.entity;

import android.net.Uri;

/**
 * Created by stuartwhitcombe on 28/06/16.
 */
public class MovieDetail extends MovieThumb {
    private String releaseDate;

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    private String synopsis;
    private double voteAverage;

    public MovieDetail(long id, String title, Uri image, String releaseDate, String synopsis, double voteAverage) {
        super(id, title, image);
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.voteAverage = voteAverage;
    }

    @Override
    public String toString() {
        return "MovieDetail{" +
                "releaseDate='" + releaseDate + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", voteAverage=" + voteAverage +
                "} " + super.toString();
    }
}
