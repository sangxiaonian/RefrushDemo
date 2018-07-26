package sang.com.easyrefrush.refrush;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import sang.com.easyrefrush.inter.OnRefreshListener;
import sang.com.easyrefrush.refrush.helper.animation.inter.AnimationCollection;
import sang.com.easyrefrush.refrush.inter.IRefrushView;
import sang.com.easyrefrush.refrush.view.base.BasePickView;
import sang.com.easyrefrush.refrushutils.JLog;

/**
 * 作者： ${PING} on 2018/7/11.
 */

public abstract class BaseRefrushLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild, AnimationCollection.IAnimationListener {

    protected OnRefreshListener mListener;

    protected View topRefrushView;//头部刷新控件
    protected IRefrushView topRefrush;
    protected AnimationCollection.IAnimationHelper topAnimationHelper;


    protected IRefrushView bottomRefrush;
    protected AnimationCollection.IAnimationHelper bottomAnimationHelper;
    protected View bottomRefrushView;//头部刷新控件

    protected NestedScrollingChildHelper mNestedScrollingChildHelper;


    protected int mTotalUnconsumed;//头布局拖动距离
    protected int mBottomTotalUnconsumed;//脚布局拖动距离

    //触发正在刷新或者取消刷新时候，头部刷新控件正在原始位置
    protected boolean mReturningToStart;
    //是否处于正在刷新状态
    protected boolean mRefreshing;

    /**
     * 目标View，通常为recycleView，listView等被刷新的控件
     */
    protected View mTarget;

    //是否是头布局
    protected boolean isTop = true;

    protected String LOG_TAG = "EasyRefrush";

    protected float mTouchSlop;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;


    public BaseRefrushLayout(Context context) {
        this(context, null, 0);
    }

    public BaseRefrushLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefrushLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }


    protected void finishSpinner() {
        if (isTop) {
            if (mTotalUnconsumed > 0) {
                if (topRefrush != null && mTotalUnconsumed > topRefrush.getOriginalValue() && topRefrush.getHeadStyle().equals(EnumCollections.HeadStyle.REFRUSH)) {
                    //开始刷新动画
                    setRefreshing();
                } else {
                    //取消刷新动画
                    finishRefrush();
                }
            } else {
                if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX) {
                    topRefrush.onFinishSpinner(mTotalUnconsumed);
                }
            }
        } else {
            if (mBottomTotalUnconsumed > 0) {
                if (bottomRefrush != null && mBottomTotalUnconsumed > bottomRefrush.getOriginalValue() && bottomRefrush.getHeadStyle().equals(EnumCollections.HeadStyle.REFRUSH)) {
                    //开始刷新动画
                    setRefreshing();
                } else {
                    //取消刷新动画
                    finishRefrush();
                }
            } else {
                if (bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX) {
                    bottomRefrush.onFinishSpinner(mBottomTotalUnconsumed);
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

    private void setRefreshing() {
        if (!mRefreshing) {
            entryTargetView();
            mRefreshing = true;
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

    private boolean hasView(View view) {
        if (view == null) {
            return false;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).equals(view)) {
                return true;
            }
        }
        return false;
    }

    public void setTopRefrushView(View topRefrushView) {
        if (this.topRefrushView != null) {
            removeView(this.topRefrushView);
        }

        if (!hasView(topRefrushView)) {
            addView(topRefrushView);
        }


        this.topRefrushView = topRefrushView;

        if (topRefrushView instanceof IRefrushView) {
            topRefrush = (IRefrushView) topRefrushView;
        }
        if (topRefrushView instanceof AnimationCollection.IAnimationHelper) {
            topAnimationHelper = (AnimationCollection.IAnimationHelper) topRefrushView;
            topAnimationHelper.setAnimationListener(this);
        }
    }

    public void setBottomRefrushView(View bottomRefrushView) {

        if (this.bottomRefrushView != null) {
            removeView(this.bottomRefrushView);
        }
        if (!hasView(bottomRefrushView)) {
            addView(bottomRefrushView);
        }
        this.bottomRefrushView = bottomRefrushView;

        if (bottomRefrushView instanceof IRefrushView) {
            bottomRefrush = (IRefrushView) bottomRefrushView;
        }
        if (bottomRefrushView instanceof AnimationCollection.IAnimationHelper) {
            bottomAnimationHelper = (AnimationCollection.IAnimationHelper) bottomRefrushView;
            bottomAnimationHelper.setAnimationListener(this);
        }
    }


    /**
     * 确认初始化mTarget ，如果有多个子控件则取第一个View
     */
    protected void entryTargetView() {
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


    // 布局
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
        int childTop;
        if (topRefrushView != null) {
            childTop = topRefrushView.getBottom();
        } else {
            childTop = getPaddingTop();
        }
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childBottom;

        if (bottomRefrushView != null) {
            childBottom = bottomRefrushView.getTop();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isTop&&!mTarget.isNestedScrollingEnabled() && mBottomTotalUnconsumed > 0) {
                    childTop = childTop - mBottomTotalUnconsumed;
                }
            }else {
                if (!isTop&& mBottomTotalUnconsumed > 0) {
                    childTop = childTop - mBottomTotalUnconsumed;
                }
            }
        } else {
            childBottom = height - getPaddingBottom();
        }
        child.layout(childLeft, childTop, childLeft + childWidth, childBottom);

    }

    //触摸事件分发拦截
    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    //滑动
    protected boolean mIsBeingDragged;
    protected float mInitialDownY;
    protected float mInitialMotionY;
    protected int mActivePointerId;
    protected static final int INVALID_POINTER = -1;//无效触摸点
    protected boolean intercept;
    protected float change;

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
            if (yDiff > 0) {
                mInitialMotionY = mInitialDownY + mTouchSlop;
            } else {
                mInitialMotionY = mInitialDownY - mTouchSlop;
            }
            mIsBeingDragged = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        entryTargetView();
        final int action = ev.getActionMasked();
        if (mNestedScrollInProgress) {
            return false;
        }
        if ((mReturningToStart && action == MotionEvent.ACTION_DOWN)) {//如果动画正在执行，进行处理
            mReturningToStart = false;
        }
        if (!isEnabled() || mRefreshing || mReturningToStart) {//如果此时正在刷新，或者控件处于UNEnable状态，则直接返回false，不去操作控件，交个子控件进行处理
            if (mRefreshing && !isTop && bottomRefrush != null && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList()) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        change = ev.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final float y = ev.getRawY();
                        final float dy = (y - change);//此处为了和nestScroll保持一致，取负值
                        change = y;
                        if (dy > 0 && !canChildScrollUp(-1)) {//向下滑动
                            //此时如果底部控件还留有外部空隙，此时需要先将底部控件滑动隐藏，此时也打断
                            bottomRefrushMove(-dy);
                            requestLayout();
                        }
                        break;
                }
            }
            return false;
        }

        int pointerIndex;


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                intercept = false;

                mActivePointerId = ev.getPointerId(0);

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
                startDragging(y);//到此处，开始滑动
                if (mIsBeingDragged) {//此时达到滑动距离
                    final float dy = y - mInitialMotionY;//此时滑动的真实距离

                    if (dy > 0) {//向下滑动
                        if (!canChildScrollUp(-1)) {//此时控件无法向下滑动
                            intercept = true;
                            ;
                            ;//自己消费触摸事件，不再向下传递
                        } else if (bottomRefrush != null && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList()) {
                            //此时如果底部控件还留有外部空隙，此时需要先将底部控件滑动隐藏，此时也打断
                            intercept = true;
                        } else {
                            intercept = false;
                        }
                    } else if (dy < 0) {//向上滑动
                        if (!canChildScrollUp(1)) {//此时控件已经到达底部，无法向上滑动
                            intercept = true;
                        } else if (topRefrush != null && mTotalUnconsumed > topRefrush.getMinValueToScrollList()) {
                            intercept = true;
                        } else {
                            intercept = false;
                        }
                    } else {
                        intercept = false;
                    }
                }
                if (mIsBeingDragged && intercept) {
                    mInitialMotionY = y;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                lastDy = 0;
                intercept = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return intercept;

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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        int pointerIndex;
        switch (action) {

            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                return true;
            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);//到此处，开始滑动
                if (mIsBeingDragged) {
                    final float dy = (y - mInitialMotionY);//此处为了和nestScroll保持一致，取负值
                    mInitialMotionY = y;
                    if (dy > 0) {//向下滑动
                        if (!canChildScrollUp(-1) && topRefrush != null) {//此时控件无法向下滑动
                            topRefrushMove(dy);
                            intercept = true;
                        } else if (bottomRefrush != null && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList()) {
                            //此时如果底部控件还留有外部空隙，此时需要先将底部控件滑动隐藏，此时也打断
                            bottomRefrushMove(-dy);
                            bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                            intercept = true;
                        } else {
                            intercept = false;
                        }
                    } else if (dy < 0) {//向上滑动
                        if (!canChildScrollUp(1)) {//此时控件已经到达底部，无法向上滑动
                            bottomRefrushMove(-dy);
                            bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                            intercept = true;
                        } else if (topRefrush != null && mTotalUnconsumed > topRefrush.getMinValueToScrollList()) {
                            topRefrushMove(dy);
                            intercept = true;
                        } else {
                            intercept = false;
                        }
                    } else {
                        intercept = false;
                    }
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
                mInitialDownY = ev.getY(pointerIndex);
                mInitialMotionY = mInitialDownY;
                mIsBeingDragged = false;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    if (isTop) {
                        finishSpinner();
                    } else {
                        finishSpinner();
                    }
                }
                mActivePointerId = INVALID_POINTER;
                lastDy = 0;
                return false;
            }
        }

        return mIsBeingDragged;
    }


    //动画执行

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
            resetScroll();
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
                if (mTotalUnconsumed != 0) {
                    mTotalUnconsumed = topRefrush.getCurrentValue();
                }
            }
        } else {
            if (bottomRefrush != null) {
                bottomRefrush.changValue(animatedValue - bottomRefrush.getCurrentValue());
                if (mBottomTotalUnconsumed != 0) {
                    mBottomTotalUnconsumed = bottomRefrush.getCurrentValue();
                }
            }
        }
    }

    private void resetScroll() {
        if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mTotalUnconsumed = 0;
        }
        if (bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mBottomTotalUnconsumed = 0;
        }
    }


    //数据计算
    protected float lastDy;

    /**
     * 移动头部数据
     *
     * @param dy
     */
    protected void topRefrushMove(float dy) {
        if (!isTop) {
            isTop = true;
        }
        JLog.e(dy+"++++"+lastDy);

        if (lastDy == 0) {
            lastDy = dy;
        }
        if (lastDy * dy < 0) {
            dy += lastDy;
        }
        if (topRefrush != null && dy != 0 && lastDy * dy > 0) {

            mTotalUnconsumed += (dy);
            JLog.e(mTotalUnconsumed+">>>>"+dy);
            mTotalUnconsumed = mTotalUnconsumed > topRefrush.getTotalDragDistance() ? topRefrush.getTotalDragDistance() :
                    (mTotalUnconsumed < topRefrush.getMinValueToScrollList() ? topRefrush.getMinValueToScrollList() : mTotalUnconsumed);
            topRefrush.moveSpinner(mTotalUnconsumed);
        }
        lastDy = dy;
    }

    /**
     * 移动头部数据
     *
     * @param dy
     */
    protected void bottomRefrushMove(float dy) {
        if (isTop) {
            isTop = false;
        }
        if (lastDy == 0) {
            lastDy = dy;
        }
        if (lastDy * dy < 0) {
            dy += lastDy;
        }
        if (bottomRefrush != null && dy != 0 && lastDy * dy > 0) {
            mBottomTotalUnconsumed += (dy);
            mBottomTotalUnconsumed = mBottomTotalUnconsumed > bottomRefrush.getTotalDragDistance() ? bottomRefrush.getTotalDragDistance() :
                    (mBottomTotalUnconsumed < bottomRefrush.getMinValueToScrollList() ? bottomRefrush.getMinValueToScrollList() : mBottomTotalUnconsumed);
        }
        lastDy = dy;
    }


    //NestedScrollParent
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    //是否处于嵌套滑动过程请
    private boolean mNestedScrollInProgress;

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
        if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mTotalUnconsumed = 0;
            mBottomTotalUnconsumed = 0;
        }
        mNestedScrollInProgress = true;
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        //对于下拉刷新，如果处于初始位置
        if (topRefrush != null
                && ((dy < 0 && !canChildScrollUp(-1))//如果是下拉操作，消耗掉所有的数据
                || (dy > 0 && mTotalUnconsumed > topRefrush.getMinValueToScrollList())//如果是想上滑动
        )) {
            if (dy > mTotalUnconsumed - topRefrush.getMinValueToScrollList() && mTotalUnconsumed > topRefrush.getMinValueToScrollList()) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = topRefrush.getMinValueToScrollList();
            } else {
                topRefrushMove(-dy);
            }
            consumed[1] = dy;
            if (!isTop) {
                isTop = true;
            }
            topRefrush.moveSpinner(mTotalUnconsumed);
        } else if (
                bottomRefrush != null &&
                        ((dy > 0 && !canChildScrollUp(1))//如果是上滑操作，消耗掉所有的数据
                                || (dy < 0 && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList())//如果是想上滑动
                        )) {
//            mBottomTotalUnconsumed = caculeUnConsum(-dy, mBottomTotalUnconsumed, bottomRefrush.getTotalDragDistance(), bottomRefrush.getMinValueToScrollList());
            bottomRefrushMove(dy);
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
            finishSpinner();
        } else {
            finishSpinner();
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
            topRefrushMove(-dy);
            topRefrush.moveSpinner(mTotalUnconsumed);
        } else if (bottomRefrush != null && dy > 0 && !canChildScrollUp(1)) {
            bottomRefrushMove(dy);
            bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
        }
    }




    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }


    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }


    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

}
