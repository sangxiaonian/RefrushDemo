package sang.com.easyrefrush;

import android.content.Context;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import sang.com.easyrefrush.refrush.BaseRefrushLayout;
import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.view.base.BasePickView;


/**
 * 作者： ${PING} on 2018/6/22.
 * 视差特效
 */

public class TopRefrushLayoutViewBate extends BaseRefrushLayout {


    /**
     * 目标View，通常为recycleView，listView等被刷新的控件
     */
    private View mTarget;

    /**
     * 是否是侵入式刷新布局
     */
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    //触发正在刷新或者取消刷新时候，头部刷新控件正在原始位置
    private boolean mReturningToStart;
    //是否处于正在刷新状态
    private boolean mRefreshing;
    private String LOG_TAG = "EasyRefrush";
    private float mTouchSlop;
    private float DRAG_RATE = 0.5f;


    public TopRefrushLayoutViewBate(Context context) {
        super(context);
    }

    public TopRefrushLayoutViewBate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopRefrushLayoutViewBate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {//初始化mTarget
            entryTargetView();
        }
        if (mTarget == null) {//如果没有子控件，则直接返回，不再进行测量
            return;
        }

        int targetWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int targetHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (topRefrushView != null) {
            measureChild(topRefrushView, widthMeasureSpec, heightMeasureSpec);
            targetHeight -= topRefrush.getCurrentValue();
        }
        if (bottomRefrushView != null) {
            measureChild(bottomRefrushView, widthMeasureSpec, heightMeasureSpec);
            targetHeight -= bottomRefrush.getCurrentValue();
        }

        //对子控件进行测量
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                targetWidth,
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                targetHeight, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            entryTargetView();
        }
        if (mTarget == null) {
            return;
        }

        if (topRefrush != null) {
            topRefrush.layoutChild(width, height);
        }

        if (bottomRefrush != null) {
            bottomRefrush.layoutChild(width, height);
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop;
        if (topRefrushView != null) {
            childTop = topRefrushView.getBottom();
        } else {
            childTop = getPaddingTop();
        }
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childBottom;

        if (bottomRefrushView != null) {
            childBottom = bottomRefrushView.getTop();
        } else {
            childBottom = height - getPaddingBottom();
        }
        child.layout(childLeft, childTop, childLeft + childWidth, childBottom);

    }

    /**
     * 确认初始化mTarget ，如果有多个子控件则取第一个View
     */
    private void entryTargetView() {
        if (mTarget == null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child instanceof BasePickView && ((BasePickView) child).getLoaction() == EnumCollections.Loaction.UP) {
                    setTopRefrushView(child);
                } else if (child instanceof BasePickView && ((BasePickView) child).getLoaction() == EnumCollections.Loaction.Down) {
                    setBottomRefrushView(child);
                } else if (!child.equals(topRefrushView) && !child.equals(bottomRefrushView)) {
                    mTarget = child;
                }
            }

        }
    }

    /**
     * 唯一的子控件是否可以继续滑动
     *
     * @param direction -1 ，可以向上滑动 1 向下滑动
     * @return true 表示可以滑动 false 表示不可以
     */
    public boolean canChildScrollUp(int direction) {

        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, direction);
        }
        return mTarget.canScrollVertically(direction);
    }


    private void finishSpinner(float overscrollTop) {
        if (isTop) {
            if (mTotalUnconsumed > 0) {
                if (topRefrush != null && overscrollTop > topRefrush.getOriginalValue() && topRefrush.getHeadStyle().equals(EnumCollections.HeadStyle.REFRUSH)) {
                    //开始刷新动画
                    setRefreshing(true);
                } else {
                    //取消刷新动画
                    finishRefrush();
                }
            } else {
                if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX) {
                    topRefrush.onFinishSpinner(overscrollTop);
                }
            }
        } else {
            if (mBottomTotalUnconsumed > 0) {
                if (bottomRefrush != null && overscrollTop > bottomRefrush.getOriginalValue() && bottomRefrush.getHeadStyle().equals(EnumCollections.HeadStyle.REFRUSH)) {
                    //开始刷新动画
                    setRefreshing(true);
                } else {
                    //取消刷新动画
                    finishRefrush();
                }
            } else {
                if (bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX) {
                    bottomRefrush.onFinishSpinner(overscrollTop);
                }
            }
        }
    }

    public void finishRefrush() {
        mRefreshing = false;
        //取消刷新动画
        if (isTop) {
            if (topAnimationHelper != null) {
                topAnimationHelper.animationToStart();
            }
        } else {
            if (bottomAnimationHelper != null) {
                bottomAnimationHelper.animationToStart();
            }
        }
    }

    private void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            entryTargetView();
            mRefreshing = refreshing;
            if (isTop) {
                if (topAnimationHelper != null) {
                    topAnimationHelper.animationToRefrush();
                }
            } else {
                if (bottomAnimationHelper != null) {
                    bottomAnimationHelper.animationToRefrush();
                }
            }
        }
    }


    /**
     * 动画开始
     */
    @Override
    public void animationStart() {
        mReturningToStart = true;
    }

    @Override
    public void animationEnd() {
        if (mRefreshing) {
            if (mListener != null) {
                mListener.onRefresh();
            }
        } else {
            if (topRefrush != null) {
                topRefrush.reset();
            }
        }
        mReturningToStart = false;
    }

    /**
     * @param animatedFraction 动画执行比例 0-1
     * @param animatedValue    动画执行当前值
     */
    @Override
    public void animationUpdate(float animatedFraction, float animatedValue) {
        if (isTop) {
            if (topRefrush != null) {
                topRefrush.changValue(animatedValue - topRefrush.getCurrentValue());
                mTotalUnconsumed = topRefrush.getCurrentValue();

            }
        } else {
            if (bottomRefrush != null) {
                bottomRefrush.changValue(animatedValue - bottomRefrush.getCurrentValue());
                mBottomTotalUnconsumed = bottomRefrush.getCurrentValue();
            }
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    //滑动
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialDownY;
    private float mInitialMotionY;

    private static final int INVALID_POINTER = -1;//无效触摸点

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        entryTargetView();

        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp(-1)
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetScroll();
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp(-1)
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float dy = -(y - mInitialMotionY) * DRAG_RATE;//此处为了和nestScroll保持一致，取负值
                    final int scrollState = topCanScroll(dy);
                    if (scrollState == 1)//如果是想上滑动
                    {
                        mTotalUnconsumed = caculeUnConsum(dy, mTotalUnconsumed, topRefrush.getTotalDragDistance(), topRefrush.getMinValueToScrollList());
                        if (!isTop) {
                            isTop = true;
                        }
                        topRefrush.moveSpinner(mTotalUnconsumed);
                    } else if (scrollState == 2) {
                        mBottomTotalUnconsumed = caculeUnConsum(-dy, mBottomTotalUnconsumed, bottomRefrush.getTotalDragDistance(), bottomRefrush.getMinValueToScrollList());
                        if (isTop) {
                            isTop = false;
                        }
                        bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                    }else {
                        return false;
                    }

                    mInitialMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    if (isTop) {
                        finishSpinner(mTotalUnconsumed);
                    } else {
                        finishSpinner(mBottomTotalUnconsumed);
                    }
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }


    //NestedParent
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    //是否处于嵌套滑动过程请
    private boolean mNestedScrollInProgress;
    private int mTotalUnconsumed;
    private int mBottomTotalUnconsumed;

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        resetScroll();
        mNestedScrollInProgress = true;
    }

    private void resetScroll() {
        if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mTotalUnconsumed = 0;
        }
        if (bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mBottomTotalUnconsumed = 0;
        }
    }

    boolean isTop = true;

    /**
     * 用来判断当前滑动处于的状态，1 表示头布局拉伸转台 2 表示脚布局拉伸状态 0 无状态
     *
     * @param dy
     * @return
     */
    private int topCanScroll(float dy) {
        if (topRefrush != null && ((dy < 0 && !canChildScrollUp(-1))//如果是下拉操作，消耗掉所有的数据
                || (dy > 0 && mTotalUnconsumed > topRefrush.getMinValueToScrollList()))) {
            return 1;
        } else if (bottomRefrush != null && ((dy > 0 && !canChildScrollUp(1))//如果是上滑操作，消耗掉所有的数据
                || (dy < 0 && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList())//如果是想上滑动
        )) {
            return 2;
        } else {
            return 0;
        }

    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        //对于下拉刷新，如果处于初始位置
        final int scrollState = topCanScroll(dy);
        if (scrollState == 1)//如果是想上滑动
        {
            if (dy > mTotalUnconsumed - topRefrush.getMinValueToScrollList() && mTotalUnconsumed > topRefrush.getMinValueToScrollList()) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = topRefrush.getMinValueToScrollList();
            } else {
                mTotalUnconsumed = caculeUnConsum(dy, mTotalUnconsumed, topRefrush.getTotalDragDistance(), topRefrush.getMinValueToScrollList());
            }
            consumed[1] = dy;
            if (!isTop) {
                isTop = true;
            }
            topRefrush.moveSpinner(mTotalUnconsumed);
        } else if (scrollState == 2) {
            mBottomTotalUnconsumed = caculeUnConsum(-dy, mBottomTotalUnconsumed, bottomRefrush.getTotalDragDistance(), bottomRefrush.getMinValueToScrollList());
            consumed[1] = dy;
            if (isTop) {
                isTop = false;
            }
            bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
        }
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }

    }

    private float lastDy;

    private int caculeUnConsum(float dy, int caculeNum, int maxValue, int minValue) {
        if (lastDy == 0) {
            lastDy = dy;
        }
        if (lastDy * dy < 0) {
            dy += lastDy;
        }
        if (dy != 0
                && lastDy * dy > 0
                ) {

            caculeNum -= (dy);
            caculeNum = caculeNum > maxValue ? maxValue : (caculeNum < minValue ? minValue : caculeNum);
        }
        lastDy = dy;
        return caculeNum;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (isTop) {
            finishSpinner(mTotalUnconsumed);
        } else {
            finishSpinner(mBottomTotalUnconsumed);
        }
        stopNestedScroll();
        lastDy = 0;
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (topRefrush != null && dy < 0 && !canChildScrollUp(-1)) {
            mTotalUnconsumed = caculeUnConsum(dy, mTotalUnconsumed, topRefrush.getTotalDragDistance(), topRefrush.getMinValueToScrollList());
            topRefrush.moveSpinner(mTotalUnconsumed);
        } else if (bottomRefrush != null && dy > 0 && !canChildScrollUp(1)) {
            mBottomTotalUnconsumed = caculeUnConsum(-dy, mBottomTotalUnconsumed, bottomRefrush.getTotalDragDistance(), bottomRefrush.getMinValueToScrollList());
            bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
        }
    }


}
