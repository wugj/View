package com.jt.view.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * @name: com.jt.view.views
 * @description:
 * @author：Administrator
 * @date: 2017-06-29 17:15
 * @company: 上海若美科技有限公司
 */

public class TraggleScrollerView extends RelativeLayout {

    private Scroller mScroller;

    public TraggleScrollerView(Context context) {
        super(context);
        inti();
    }

    public TraggleScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti();
    }

    public TraggleScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inti();
//        offsetLeftAndRight();
    }

    private void inti() {
        mScroller = new Scroller(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                scrollTo(-(int)event.getX(), -(int) event.getY());
                break;
        }
        return true;
    }

    public void smoothscrollTo(int destx, int destY){
        int scrollX = getScrollX();
        int deltaX =  scrollX - destx;
        int scrollY = getScrollY();
        int deltaY =  scrollY - destY;
        mScroller.startScroll(scrollX, scrollY, deltaX, deltaY, 100);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
