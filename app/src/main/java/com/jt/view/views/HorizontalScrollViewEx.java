package com.jt.view.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * @name: com.jt.view.views
 * @description:
 * @author：Administrator
 * @date: 2017-07-01 17:20
 * @company: 上海若美科技有限公司
 */

public class HorizontalScrollViewEx extends ViewGroup {

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mLastXIntercept;
    private int mLastYIntercept;
    private int mLastX;
    private int mLastY;
    private int mChildIndex;
    private int mChildWidth;
    private int mChildrenSize;

    public HorizontalScrollViewEx(Context context) {
        super(context);
        init();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void smoothScrollBy(int dx, int dy){
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果屏幕的动画还没结束，你就按下了，我们就结束上一次动画，即开始这次新ACTION_DOWN的动画
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    intercepted = true;
                }
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = mLastX - x;
//                int deltaY = mLastY - y;
                int scrollX1 = getScrollX();
                //边界判断 禁止移动
                if (scrollX1 + deltaX <= 0 || scrollX1 + deltaX >= getMeasuredWidth()) {
                    break;
                }
                scrollBy(deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) >= 50) {
                    mChildIndex = xVelocity > 0 ? mChildIndex : mChildIndex - 1;
                }else {
                    mChildIndex = (scrollX + mChildWidth / 2) / mChildWidth;
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, mChildrenSize - 1));
                int dx = mChildIndex * mChildWidth - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;

        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount == 0) {
            setMeasuredDimension(getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight(),
                    getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom());
            return;
        }
        int meaureWidht = getPaddingLeft() + getPaddingRight();
        int meaureHeiht = 0;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heihtMode = MeasureSpec.getMode(heightMeasureSpec);
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int childViewMeasuredWidth = childView.getMeasuredWidth();
            int childViewMeasuredHeight = childView.getMeasuredHeight();
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            meaureWidht += childViewMeasuredWidth + layoutParams.leftMargin + layoutParams.rightMargin;
            meaureHeiht = Math.max(meaureHeiht, childViewMeasuredHeight + layoutParams.topMargin + layoutParams.bottomMargin);
        }
        meaureHeiht = Math.max(getSuggestedMinimumHeight() + getPaddingTop() + getPaddingLeft(), meaureHeiht + getPaddingBottom() + getPaddingTop());
        meaureWidht = Math.max(meaureWidht, getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight());
        if (widthMode == MeasureSpec.AT_MOST && heihtMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(meaureWidht, meaureHeiht);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(meaureWidht, heightSize);
        } else if (heihtMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, meaureHeiht);
        }else {
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = getPaddingLeft();
        int childCount = getChildCount();
        int lastRightMargin = 0;
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int left = childLeft + lastRightMargin + layoutParams.leftMargin;
                int top = getPaddingTop() + layoutParams.topMargin;
                int right = left + childView.getMeasuredWidth();
                int bottom = top + childView.getMeasuredHeight();
                childView.layout(left, top, right, bottom);
                lastRightMargin = layoutParams.rightMargin;
                childLeft = right;
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        if (p instanceof MarginLayoutParams) {
            return new MarginLayoutParams(p);
        }
        return new MarginLayoutParams(p.width, p.height);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
