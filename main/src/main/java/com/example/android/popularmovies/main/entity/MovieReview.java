package com.example.android.popularmovies.main.entity;

/**
 * Created by stuartwhitcombe on 26/08/16.
 */
public class MovieReview {
    private String author;
    private String content;

    public MovieReview(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }
    public String getContent() {
        return content;
    }
}
