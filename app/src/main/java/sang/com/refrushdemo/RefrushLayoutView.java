package sang.com.refrushdemo;

import android.content.Context;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

import sang.com.refrushdemo.inter.DefaultAnimationListenerIml;
import sang.com.refrushdemo.inter.OnRefreshListener;
import sang.com.refrushdemo.refrush.BaseRefrushLayout;
import sang.com.refrushdemo.refrush.helper.animation.AnimationToStart;
import sang.com.refrushdemo.refrush.inter.IRefrushView;
import sang.com.refrushdemo.utils.JLog;

/**
 * 作者： ${PING} on 2018/6/22.
 */

public class RefrushLayoutView extends BaseRefrushLayout {


    /**
     * 目标View，通常为recycleView，listView等被刷新的控件
     */
    private View mTarget;
    private View topRefrushView;//头部刷新控件


    /**
     * 是否是侵入式刷新布局
     */
    private boolean invasive = true;


    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    /**
     * 头部刷新控件移动距离
     */
//    private int mCurrentTargetOffsetTop;


    //触发正在刷新或者取消刷新时候，头部刷新控件正在原始位置
    private boolean mReturningToStart;
    //是否处于正在刷新状态
    private boolean mRefreshing;

    //


    //触摸滑动时候的滑动比例
    private float DRAG_RATE = 0.5f;

    private String LOG_TAG = "XRefrush";

    private OnRefreshListener mListener;

    //动画执行时间
    private static final int ANIMATE_TO_START_DURATION = 200;

    //减速差值器
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;


    private AnimationToStart animationToStart;

    private DecelerateInterpolator mDecelerateInterpolator;
    private IRefrushView refrushView;


    public RefrushLayoutView(Context context) {
        this(context, null);
    }

    public RefrushLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    private void initView(Context context, AttributeSet attrs) {
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
                        refrushView.changValue(animatedValue - refrushView.getCurrentValue());
                    }

                    @Override
                    public void onAnimationEnd() {
                        super.onAnimationEnd();
                        if (mRefreshing) {
                            if (mListener != null) {
                                mListener.onRefresh();
                            }
                        } else {
                            refrushView.reset();
                        }
                        mReturningToStart = false;
                    }
                })
        ;


        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        topRefrushView = LayoutInflater.from(context).inflate(R.layout.item_top, this, false);
        addView(topRefrushView);

         invasive=false;

        if (topRefrushView instanceof IRefrushView) {
            refrushView = (IRefrushView) topRefrushView;
        }


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
        if (topRefrushView != null) {
            measureChild(topRefrushView, widthMeasureSpec, heightMeasureSpec);
        }
        int targetWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int targetHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - refrushView.getCurrentValue();
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

        int circleWidth = topRefrushView.getMeasuredWidth();
        int circleHeight = topRefrushView.getMeasuredHeight();
        topRefrushView.layout((width / 2 - circleWidth / 2), refrushView.getCurrentValue() + getPaddingTop(),
                (width / 2 + circleWidth / 2), refrushView.getCurrentValue() + getPaddingTop() + circleHeight);


        final View child = mTarget;

        if (invasive) {
            final int childLeft = getPaddingLeft();
            final int childTop = topRefrushView.getBottom();
            final int childWidth = width - getPaddingLeft() - getPaddingRight();
            final int childHeight = height - getPaddingBottom();
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        } else {
            final int childLeft = getPaddingLeft();
            final int childTop = getPaddingTop();
            final int childWidth = width - getPaddingLeft() - getPaddingRight();
            final int childHeight = height - getPaddingTop() - getPaddingBottom();
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
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
        return canChildScrollUp(-1);
    }

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

        if (mInitialDownY == 0) {
            mInitialDownY = ev.getRawY();
        }

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        JLog.i(canChildScrollUp() + ">>>");
        //如果正在滑动，正在刷新，或者取消刷新正在执行动画，在不可以再次刷新
        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                refrushView.reset();
                mIsBeingDragged = false;

                mInitialDownY = ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE: {

                final float y = ev.getRawY();

                JLog.d("ACTION_MOVE:" + y);

                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) { //如果是向下滑动
                        refrushView.moveSpinner(overscrollTop);

                    } else {
                        return false;
                    }
                }
                break;
            }


            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mIsBeingDragged) {
                    final float y = ev.getRawY();
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mInitialDownY = 0;
                return false;
            }
        }

        return mIsBeingDragged;
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
    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > refrushView.getTotalDragDistance()) {
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
        animateOffsetToStartPosition(refrushView.getCurrentValue());
    }
    private void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            entryTargetView();
            animateOffsetToCorrectPosition(refrushView.getCurrentValue());
            mRefreshing = refreshing;
        }
    }
    /**
     * 执行动画，移动到刷新位置
     *
     * @param from 开始的位置
     */
    private void animateOffsetToCorrectPosition(int from) {
        animationToStart.reset();
        animationToStart.addIntValues(from, refrushView.getTotalDragDistance());
        animationToStart.start();

    }
    private void animateOffsetToStartPosition(int from) {
        animationToStart.reset();
        animationToStart.addIntValues(from, refrushView.getOriginalValue());
        animationToStart.start();
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

//        //对于下拉刷新，如果处于初始位置
        if (invasive) {
            if (dy<0&&!canChildScrollUp()){//如果是下拉操作，消耗掉所有的数据
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    caculeUnConsum(dy);
                    consumed[1] = dy;
                }
                refrushView.moveSpinner(mTotalUnconsumed);
            }else if (dy>0&&mTotalUnconsumed>0){//如果是想上滑动
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    caculeUnConsum(dy);
                    consumed[1] = dy;
                }
                refrushView.moveSpinner(mTotalUnconsumed);
            }
        }else {
            if (dy > 0 && mTotalUnconsumed > 0) {
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    mTotalUnconsumed -= dy;
                    consumed[1] = dy;
                }
                refrushView.moveSpinner(mTotalUnconsumed);
            }
        }

        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }

    }

    private int lastDy;
    private  void caculeUnConsum(int dy) {
        if (lastDy==0){
            lastDy=dy;
        }
        if (dy!=0&&lastDy/dy!=-1){
            mTotalUnconsumed -= dy;
            mTotalUnconsumed= mTotalUnconsumed>2*refrushView.getTotalDragDistance()? (int) (2* refrushView.getTotalDragDistance()) :mTotalUnconsumed;
        }
        lastDy=dy;
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
        lastDy=0;
    }
    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
           refrushView. moveSpinner(mTotalUnconsumed);
        }
    }

}
