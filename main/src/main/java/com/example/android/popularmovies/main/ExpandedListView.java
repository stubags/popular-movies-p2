package com.example.android.popularmovies.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by stuartwhitcombe on 26/08/16.
 * Not actually using this, but not getting rid of it yet...
 * Couldn't get it to work properly, things were always getting cut off, or not scrolling right to the end.
 * Decided to switch to dynamically adding buttons and/or text views.
 */
public class ExpandedListView extends ListView {
    private android.view.ViewGroup.LayoutParams params;

    public ExpandedListView(Context context) {
        super(context);
    }

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        final int UNBOUNDED = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        int myHeight = hSpecSize;
        if(hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        }
        if(hSpecMode == MeasureSpec.AT_MOST) {
            myHeight = hSpecSize;
        }
        if (hSpecMode == MeasureSpec.UNSPECIFIED){
            // wrap content
            int items = getCount();
            ListAdapter adapter = getAdapter();
            int height = 0;
            for(int i = 0; i < items; ++i) {
                View view = adapter.getView(i, null, this);
                view.measure(UNBOUNDED, UNBOUNDED);
                height += view.getMeasuredHeight();
                height += getDividerHeight();
                // hmmm
                height += 5;
            }
            myHeight = height;
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int myWidth = wSpecSize;

        myHeight = Math.max(myHeight, getSuggestedMinimumHeight());
        myWidth = Math.max(myWidth, getSuggestedMinimumWidth());
        setMeasuredDimension(myWidth, myHeight);
    }
}
