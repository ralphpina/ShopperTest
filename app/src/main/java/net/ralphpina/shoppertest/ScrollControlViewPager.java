package net.ralphpina.shoppertest;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Got this from here: http://stackoverflow.com/questions/7814017/is-it-possible-to-disable-scrolling-on-a-viewpager
 */
public class ScrollControlViewPager extends ViewPager {

    private boolean mIsPagingEnabled = true;

    public ScrollControlViewPager(Context context) {
        super(context);
    }

    public ScrollControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mIsPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mIsPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.mIsPagingEnabled = b;
    }
}
