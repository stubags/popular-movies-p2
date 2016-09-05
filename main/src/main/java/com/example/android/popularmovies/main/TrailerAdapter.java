package com.example.android.popularmovies.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.android.popularmovies.main.entity.MovieTrailer;

import java.util.List;

/**
 * Created by stuartwhitcombe on 26/08/16.
 */
public class TrailerAdapter extends ArrayAdapter<MovieTrailer> {
    private Context context;
    private List<MovieTrailer> values;

    public TrailerAdapter(Context context, List<MovieTrailer> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trailer_list_item, parent, false);
        MovieTrailer trailer = values.get(position);
        TextView trailerText = (TextView)rowView.findViewById(R.id.trailer_text);
        trailerText.setText(trailer.getName());
        return rowView;
    }

}
