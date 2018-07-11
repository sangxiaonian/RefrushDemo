package sang.com.refrushdemo;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

import sang.com.refrushdemo.inter.DefaultAnimationListenerIml;
import sang.com.refrushdemo.inter.OnRefreshListener;
import sang.com.refrushdemo.refrush.helper.NoninvasiveHoveringStyleHelper;
import sang.com.refrushdemo.refrush.helper.animation.AnimationToStart;
import sang.com.refrushdemo.utils.JLog;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

/**
 * 作者： ${PING} on 2018/6/22.
 */

public class XRefrushLayoutView extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    NoninvasiveHoveringStyleHelper helper;

    /**
     * 目标View，通常为recycleView，listView等被刷新的控件
     */
    private View mTarget;
    private View topRefrushView;//头部刷新控件


    /**
     * 是否是侵入式刷新布局
     */
    private boolean invasive = true;


    //默认情况下，控件应该停止滑动开始刷新的位置
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final int DEFAULT_TOP_SIZE = 40;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    /**
     * 头部刷新控件移动距离
     */
    private int mCurrentTargetOffsetTop;

    /**
     * 刷新控件大小
     */
    private int topSize;


    //触发正在刷新或者取消刷新时候，头部刷新控件正在原始位置
    private boolean mReturningToStart;
    //是否处于正在刷新状态
    private boolean mRefreshing;

    //

    //原始高度
    private int mOriginalOffsetTop;

    //触摸滑动时候的滑动比例
    private float DRAG_RATE = 0.5f;
    //拖拽的总共距离
    private int mTotalDragDistance;

    private String LOG_TAG = "XRefrush";

    private OnRefreshListener mListener;

    //动画执行时间
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;

    //减速差值器
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;


    private AnimationToStart animationToStart;
    private AnimationToStart animationToRefrush;


    //当前位置
    private int mFrom;
    private DecelerateInterpolator mDecelerateInterpolator;


    public XRefrushLayoutView(Context context) {
        this(context, null);
    }

    public XRefrushLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    private void initView(Context context, AttributeSet attrs) {

        helper = new NoninvasiveHoveringStyleHelper(context,this);
        animationToStart = new AnimationToStart()
                .addInterpolator(mDecelerateInterpolator)
                .addDuration(ANIMATE_TO_START_DURATION)
                .addListener(new DefaultAnimationListenerIml() {

                    @Override
                    public void onAnimationStart() {
                        mReturningToStart = true;
                    }

                    @Override
                    public void onAnimationUpdate(float animatedFraction, float animatedValue) {
                        int targetTop = 0;
                        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * animatedFraction));
                        int offset = targetTop - topRefrushView.getTop();
                        setTargetOffsetTopAndBottom(offset);
                    }

                    @Override
                    public void onAnimationEnd() {
                        super.onAnimationEnd();
                        topRefrushView.setVisibility(View.GONE);
                        setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop);
                        mCurrentTargetOffsetTop = mTarget.getTop();
                        mReturningToStart = false;
                    }
                })
                .addIntValues(mCurrentTargetOffsetTop, mFrom)
        ;
        animationToRefrush = new AnimationToStart()
                .addInterpolator(mDecelerateInterpolator)
                .addDuration(ANIMATE_TO_TRIGGER_DURATION)
                .addListener(new DefaultAnimationListenerIml() {

                    @Override
                    public void onAnimationUpdate(float animatedFraction, float animatedValue) {
                        int targetTop = 0;
                        int endTarget = 0;

                        endTarget = mTotalDragDistance;

                        targetTop = (mFrom + (int) ((endTarget - mFrom) * animatedFraction));
                        int offset = targetTop - topRefrushView.getTop();

                        setTargetOffsetTopAndBottom(offset);
                    }

                    @Override
                    public void onAnimationEnd() {
                        if (mRefreshing) {
                            if (mListener != null) {
                                mListener.onRefresh();
                            }
                        }
                        mCurrentTargetOffsetTop = topRefrushView.getTop();

                    }
                })
                .addIntValues(mCurrentTargetOffsetTop, mTotalDragDistance)
        ;


        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTotalDragDistance = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
        topSize = (int) (DEFAULT_TOP_SIZE * metrics.density);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mOriginalOffsetTop = mCurrentTargetOffsetTop - topSize;
        topRefrushView = helper.getRefrushView();
        addView(topRefrushView);
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
        //对子控件进行测量
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                targetWidth,
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                targetHeight, MeasureSpec.EXACTLY));
        if (topRefrushView != null) {
            measureChild(topRefrushView,widthMeasureSpec,heightMeasureSpec);
        }

        JLog.i("--------------------onMeasure-------------");

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
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();

        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int circleWidth = topRefrushView.getMeasuredWidth();
        int circleHeight = topRefrushView.getMeasuredHeight();
        topRefrushView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
                (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
        JLog.i("--------------------onLayout-------------");

    }

    /**
     * 确认初始化mTarget ，如果有多个子控件则取第一个View
     */
    private void entryTargetView() {
        if (mTarget == null) {
            int childCount = getChildCount();
            int basicCount = 0;
            if (topRefrushView != null) {
                basicCount++;
            }
            if (childCount == (basicCount + 1)) {//出了刷新控件外，至少要有一个子控件
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!child.equals(topRefrushView)) {
                        mTarget = child;
                        break;
                    }
                }
            } else {
                throw new RuntimeException(getClass().getName() + " can only be one childView");
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
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    public boolean canChildScrollUp() {
        return canChildScrollUp(1) && canChildScrollUp(-1);
    }

    //触摸点ID
    private int mActivePointerId;
    //是否开始拖拽
    private boolean mIsBeingDragged;
    //触摸的位置
    private float mInitialDownY;
    //触摸滑动的最小距离
    private int mTouchSlop;
    //垂直方向手指触摸开始滑动时候的坐标位置
    private float mInitialMotionY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        //如果正在滑动，正在刷新，或者取消刷新正在执行动画，在不可以再次刷新
        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - topRefrushView.getTop());
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    JLog.e("Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) { //如果是向下滑动
                        moveSpinner(overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: //另一个触摸点，则以此触摸点为主
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    JLog.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;

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
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return mIsBeingDragged;
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mTotalDragDistance) {
            //开始刷新动画
            setRefreshing(true);
        } else {
            //取消刷新动画
            finishRefrush();
        }
    }

    public void finishRefrush() {
        mRefreshing = false;
        //取消刷新动画
        animateOffsetToStartPosition(mCurrentTargetOffsetTop);
    }


    private void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            entryTargetView();
            animateOffsetToCorrectPosition(mCurrentTargetOffsetTop);
            mRefreshing = refreshing;
        }
    }

    /**
     * 执行动画，移动到刷新位置
     *
     * @param from 开始的位置
     */
    private void animateOffsetToCorrectPosition(int from) {
        mFrom = from;
        animationToRefrush.reset();
        animationToRefrush.addIntValues(from, mOriginalOffsetTop);
        animationToRefrush.start();

    }

    private void animateOffsetToStartPosition(int from) {
        mFrom = from;
        animationToStart.reset();
        animationToStart.addIntValues(from, mOriginalOffsetTop);
        animationToStart.start();
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

    /**
     * 开始进行滑动
     *
     * @param overscrollTop
     */
    private void moveSpinner(float overscrollTop) {
        //拖拽距离到最大距离的百分比
        float originalDragPercent = overscrollTop / mTotalDragDistance;
        //确定百分比
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
        //弹性距离
        float slingshotDist = mTotalDragDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;

        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);

        if (topRefrushView.getVisibility() != View.VISIBLE) {
            topRefrushView.setVisibility(View.VISIBLE);
        }

        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
    }

    /**
     * 开始数值方向拖拽
     *
     * @param y
     */
    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
        }
    }

    /**
     * 将头布局位移指定距离
     *
     * @param offset
     */
    private void setTargetOffsetTopAndBottom(int offset) {
        topRefrushView.bringToFront();
        ViewCompat.offsetTopAndBottom(topRefrushView, offset);
        mCurrentTargetOffsetTop = topRefrushView.getTop();
    }



    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    //是否处于嵌套滑动过程请
    private boolean mNestedScrollInProgress;
    private int mTotalUnconsumed;


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
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
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
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }
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
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
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
