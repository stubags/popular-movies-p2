package com.example.android.popularmovies.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.android.popularmovies.main.entity.MovieReview;

import java.util.List;

/**
 * Created by stuartwhitcombe on 26/08/16.
 */
public class ReviewAdapter extends ArrayAdapter<MovieReview> {
    private Context context;
    private List<MovieReview> values;

    public ReviewAdapter(Context context, List<MovieReview> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.review_list_item, parent, false);
        MovieReview review = values.get(position);
//        TextView reviewAuthor = (TextView)rowView.findViewById(R.id.review_author);
//        reviewAuthor.setText(review.getAuthor());
        TextView reviewContent = (TextView)rowView.findViewById(R.id.review_content);
        reviewContent.setText(review.getContent());
        return rowView;
    }

}
