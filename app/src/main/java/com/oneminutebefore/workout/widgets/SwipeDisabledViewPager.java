package com.oneminutebefore.workout.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class SwipeDisabledViewPager extends ViewPager {

    private boolean enabled;

    public SwipeDisabledViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.enabled && super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.enabled && super.onInterceptTouchEvent(event);

    }

    @SuppressWarnings("SameParameterValue")
    public void setPagingEnabled(@SuppressWarnings("SameParameterValue") boolean enabled) {
        this.enabled = enabled;
    }

}

