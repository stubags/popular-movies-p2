package com.example.android.popularmovies.main;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by stuartwhitcombe on 27/06/16.
 */
public class MovieImageView extends AppCompatImageView {
    private boolean inGrid = true;

    public MovieImageView(Context context) {
        super(context);
    }

    public MovieImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovieImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setInGrid(boolean inGrid) {
        this.inGrid = inGrid;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(inGrid)
            // if it's in a grid, make it square (doesn't need to be square, but this seems to be most reliably the same size
            // all of the time because otherwise variations in height within the grid cause real issues.
            // hopefully some better way of making it good will come up in lesson 4 or 5!
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        else {
            // if it's not in a grid, try to size it in the right proportions, but still based off of its width.
            if(getDrawable() != null) {
                setMeasuredDimension(getMeasuredWidth(),
                        (int) (getMeasuredWidth() * (getDrawable().getIntrinsicHeight() / (double)getDrawable().getIntrinsicWidth())));
            }
        }
    }
}
