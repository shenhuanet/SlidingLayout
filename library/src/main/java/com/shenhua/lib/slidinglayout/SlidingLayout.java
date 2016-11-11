package com.shenhua.lib.slidinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by shenhua on 11/11/2016.
 * Email shenhuanet@126.com
 */
public class SlidingLayout extends FrameLayout {

    private int mTouchSlop;//系统允许最小的滑动判断值
    private int mBackgroundViewLayoutId = 0;

    private View mBackgroundView;//背景View
    private View mTargetView;//正面View

    private boolean mIsBeingDragged;
    private float mInitialDownY;
    private float mInitialMotionY;
    private float mLastMotionY;
    private int mActivePointerId = INVALID_POINTER;

    private float mSlidingOffset = 0.5F;// 滑动阻力系数

    private static final int RESET_DURATION = 200;
    private static final int SMOOTH_DURATION = 1000;

    public static final int SLIDING_MODE_BOTH = 0;
    public static final int SLIDING_MODE_TOP = 1;
    public static final int SLIDING_MODE_BOTTOM = 2;

    public static final int SLIDING_POINTER_MODE_ONE = 0;
    public static final int SLIDING_POINTER_MODE_MORE = 1;

    private int mSlidingMode = SLIDING_MODE_BOTH;

    private int mSlidingPointerMode = SLIDING_POINTER_MODE_MORE;

    private static final int INVALID_POINTER = -1;

    private SlidingListener mSlidingListener;

    public static final int STATE_SLIDING = 2;
    public static final int STATE_IDLE = 1;

    private int mSlidingTopMaxDistance = SLIDING_DISTANCE_UNDEFINED;

    public static final int SLIDING_DISTANCE_UNDEFINED = -1;

    private OnTouchListener mDelegateTouchListener;

    public SlidingLayout(Context context) {
        this(context, null);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingLayout);
        mBackgroundViewLayoutId = a.getResourceId(R.styleable.SlidingLayout_background_view, mBackgroundViewLayoutId);
        mSlidingMode = a.getInteger(R.styleable.SlidingLayout_sliding_mode, SLIDING_MODE_BOTH);
        mSlidingPointerMode = a.getInteger(R.styleable.SlidingLayout_sliding_pointer_mode, SLIDING_POINTER_MODE_MORE);
        mSlidingTopMaxDistance = a.getDimensionPixelSize(R.styleable.SlidingLayout_top_max, SLIDING_DISTANCE_UNDEFINED);
        a.recycle();
        if (mBackgroundViewLayoutId != 0) {
            View view = View.inflate(getContext(), mBackgroundViewLayoutId, null);
            setBackgroundView(view);
        }
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setBackgroundView(View view) {
        if (mBackgroundView != null)
            this.removeView(mBackgroundView);
        mBackgroundView = view;
        this.addView(view, 0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() == 0) {
            return;
        }
        if (mTargetView == null) {
            ensureTarget();
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mDelegateTouchListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        //判断拦截
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                Log.i("onInterceptTouchEvent", "down");
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
//                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }

                if (y > mInitialDownY) {
                    //判断是否是上拉操作
                    final float yDiff = y - mInitialDownY;
                    if (yDiff > mTouchSlop && !mIsBeingDragged && !canChildScrollUp()) {
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mLastMotionY = mInitialMotionY;
                        mIsBeingDragged = true;
                    }
                } else if (y < mInitialDownY) {
                    //判断是否是下拉操作
                    final float yDiff = mInitialDownY - y;
                    if (yDiff > mTouchSlop && !mIsBeingDragged && !canChildScrollDown()) {
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mLastMotionY = mInitialMotionY;
                        mIsBeingDragged = true;
                    }
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                Log.i("onInterceptTouchEvent", "up");
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTargetView != null) {
            mTargetView.clearAnimation();
        }
        mSlidingMode = 0;
        mTargetView = null;
        mBackgroundView = null;
        mSlidingListener = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDelegateTouchListener != null && mDelegateTouchListener.onTouch(this, event)) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float delta;
                float movemment;
                if (mSlidingPointerMode == SLIDING_POINTER_MODE_MORE) {
                    int activePointerId = MotionEventCompat.getPointerId(event, event.getPointerCount() - 1);
                    if (mActivePointerId != activePointerId) {
                        mActivePointerId = activePointerId;
                        mInitialDownY = getMotionEventY(event, mActivePointerId);
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mLastMotionY = mInitialMotionY;
                        if (mSlidingListener != null) {
                            mSlidingListener.onSlidingChangePointer(mTargetView, activePointerId);
                        }
                    }
                    delta = getMotionEventY(event, mActivePointerId) - mLastMotionY;
                    float tempOffset = 1 - (Math.abs(getInstrument().getTranslationY(mTargetView)
                            + delta) / mTargetView.getMeasuredHeight());
                    delta = getInstrument().getTranslationY(mTargetView)
                            + delta * mSlidingOffset * tempOffset;
                    mLastMotionY = getMotionEventY(event, mActivePointerId);
                    movemment = getMotionEventY(event, mActivePointerId) - mInitialMotionY;
                } else {
                    float tempOffset = 1 - Math.abs(getInstrument().getTranslationY(mTargetView) / mTargetView.getMeasuredHeight());
                    delta = (event.getY() - mInitialMotionY) * mSlidingOffset * tempOffset;
                    movemment = event.getY() - mInitialMotionY;
                }
                float distance = getSlidingDistance();
                switch (mSlidingMode) {
                    case SLIDING_MODE_BOTH:
                        getInstrument().slidingByDelta(mTargetView, delta);
                        break;
                    case SLIDING_MODE_TOP:
                        if (movemment >= 0 || distance > 0) {
                            //向下滑动
                            if (delta < 0) {
                                //如果还往上滑，就让它归零
                                delta = 0;
                            }
                            if (mSlidingTopMaxDistance == SLIDING_DISTANCE_UNDEFINED || delta < mSlidingTopMaxDistance) {
                                //滑动范围内 for todo
                            } else {
                                //超过滑动范围
                                delta = mSlidingTopMaxDistance;
                            }
                            getInstrument().slidingByDelta(mTargetView, delta);
                        }
                        break;
                    case SLIDING_MODE_BOTTOM:
                        if (movemment <= 0 || distance < 0) {
                            //向上滑动
                            if (delta > 0) {
                                //如果还往下滑，就让它归零
                                delta = 0;
                            }
                            getInstrument().slidingByDelta(mTargetView, delta);
                        }
                        break;
                }
                if (mSlidingListener != null) {
                    mSlidingListener.onSlidingStateChange(this, STATE_SLIDING);
                    mSlidingListener.onSlidingOffset(this, delta);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mSlidingListener != null) {
                    mSlidingListener.onSlidingStateChange(this, STATE_IDLE);
                }
                getInstrument().reset(mTargetView, RESET_DURATION);
                break;
        }
        return true;
    }

    public View getBackgroundView() {
        return this.mBackgroundView;
    }

    public void setSlidingDistance(int distance) {
        this.mSlidingTopMaxDistance = distance;
    }

    public int setSlidingDistance() {
        return this.mSlidingTopMaxDistance;
    }

    /**
     * 获得滑动阻力系数
     *
     * @return 滑动阻力系数
     */
    public float getSlidingOffset() {
        return this.mSlidingOffset;
    }

    /**
     * 设置滑动阻力系数
     *
     * @param slidingOffset 滑动阻力系数
     */
    public void setSlidingOffset(float slidingOffset) {
        this.mSlidingOffset = slidingOffset;
    }

    public void setSlidingListener(SlidingListener slidingListener) {
        this.mSlidingListener = slidingListener;
    }

    private void ensureTarget() {
        if (mTargetView == null) {
            mTargetView = getChildAt(getChildCount() - 1);
        }
    }

    public void setTargetView(View view) {
        if (mTargetView != null)
            this.removeView(mTargetView);
        mTargetView = view;
        this.addView(view);
    }

    public View getTargetView() {
        return this.mTargetView;
    }

    public float getSlidingDistance() {
        return getInstrument().getTranslationY(getTargetView());
    }

    public Instrument getInstrument() {
        return Instrument.getInstance();
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mTargetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTargetView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTargetView, -1) || mTargetView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTargetView, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mTargetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTargetView;
                return absListView.getChildCount() > 0 && absListView.getAdapter() != null
                        && (absListView.getLastVisiblePosition() < absListView.getAdapter().getCount() - 1 || absListView.getChildAt(absListView.getChildCount() - 1)
                        .getBottom() < absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mTargetView, 1) || mTargetView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTargetView, 1);
        }
    }

    public void setSlidingMode(int mode) {
        mSlidingMode = mode;
    }

    public int getSlidingMode() {
        return mSlidingMode;
    }

    public void smoothScrollTo(float y) {
        getInstrument().smoothTo(mTargetView, y, SMOOTH_DURATION);
    }

    public interface SlidingListener {
        // 不能操作繁重的任务在这里
        void onSlidingOffset(View view, float delta);

        void onSlidingStateChange(View view, int state);

        void onSlidingChangePointer(View view, int pointerId);
    }

}
