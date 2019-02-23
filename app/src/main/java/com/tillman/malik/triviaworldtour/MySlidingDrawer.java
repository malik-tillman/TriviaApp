package com.tillman.malik.triviaworldtour;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import java.util.jar.Attributes;

public class MySlidingDrawer extends SlidingDrawer {
    private View button;
    private int height;

    public MySlidingDrawer (Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        height = metrics.heightPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);

        return true;
    }
}
