package com.example.android.popularmovies.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.android.popularmovies.main.entity.MovieThumb;

import java.util.List;

/**
 * Created by stuartwhitcombe on 22/06/16.
 */
public class MovieThumbAdapter extends ArrayAdapter<MovieThumb> {

    public MovieThumbAdapter(Context context, List<MovieThumb> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieThumb thumb = getItem(position);
        // inflate the overall view

        // how the view gets recycled i think...
        View rootView;
        if(convertView == null)
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.movie_grid_item_layout, parent, false);
        else
            rootView = convertView;

        // find the bits inside the view
        // and set them to the values within the MovieThumb at this location
        MovieImageView imageView = (MovieImageView)rootView.findViewById(R.id.movie_image_view);
        thumb.loadImageInto(imageView, getContext());
                //Picasso.with(getContext()).load(thumb.getUri()).into(imageView);
        TextView textView = (TextView)rootView.findViewById(R.id.movie_title_view);
        textView.setText(thumb.getTitle());
        // return the overall view
        return rootView;
    }
}
