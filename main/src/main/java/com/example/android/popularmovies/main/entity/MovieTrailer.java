package com.example.android.popularmovies.main.entity;

/**
 * Created by stuartwhitcombe on 26/08/16.
 */
public class MovieTrailer {
    private final String id;
    private final String key;
    private final String site;
    private final String name;

    public MovieTrailer(String id, String key, String site, String name) {
        this.id = id;
        this.key = key;
        this.site = site;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

}
