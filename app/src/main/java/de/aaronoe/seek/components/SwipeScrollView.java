package de.aaronoe.seek.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by aaron on 03.06.17.
 *
 */

public class SwipeScrollView extends ScrollView {

    private static final String TAG = "SwipeScrollView";
    private swipeScrollListener listener;

    public SwipeScrollView(Context context) {
        super(context);
    }

    public SwipeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwipeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setSwipeScrollListener(swipeScrollListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
        if (listener != null) {
            this.listener.scrolledToTop(t == 0);
        }
    }

    public interface swipeScrollListener {
        void scrolledToTop(boolean atTop);
    }
}
