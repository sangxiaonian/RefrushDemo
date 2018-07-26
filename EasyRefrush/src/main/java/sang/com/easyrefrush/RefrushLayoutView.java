package sang.com.easyrefrush;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
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

public class RefrushLayoutView extends BaseRefrushLayout {


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


    public RefrushLayoutView(Context context) {
        super(context);
    }

    public RefrushLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefrushLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
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
            if (!isTop && mBottomTotalUnconsumed > 0) {
                childTop = childTop - mBottomTotalUnconsumed;
            }
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
    private boolean mIsBeingDragged;
    private float mInitialDownY;
    private float mInitialMotionY;

    private static final int INVALID_POINTER = -1;//无效触摸点


    private boolean intercept;


    private float change;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        entryTargetView();
        final int action = ev.getActionMasked();
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {//如果动画正在执行，进行处理
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
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                intercept = false;
                mInitialDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                final float y = ev.getY();
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
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                lastDy = 0;
                intercept = false;
                break;
        }
        return intercept;

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        switch (action) {

            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE: {
                final float y = ev.getY();
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
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    intercept = false;
                    if (isTop) {
                        finishSpinner(mTotalUnconsumed);
                    } else {
                        finishSpinner(mBottomTotalUnconsumed);
                    }
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                intercept = false;
                return false;
        }

        return mIsBeingDragged;
    }

    /**
     * 移动头部数据
     *
     * @param dy
     */
    private void topRefrushMove(float dy) {
        if (!isTop) {
            isTop = true;
        }
        if (lastDy == 0) {
            lastDy = dy;
        }
        if (lastDy * dy < 0) {
            dy += lastDy;
        }
        if (topRefrush != null && dy != 0 && lastDy * dy > 0) {

            mTotalUnconsumed += (dy);
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
    private void bottomRefrushMove(float dy) {
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


    private int mTotalUnconsumed;
    private int mBottomTotalUnconsumed;


    private void resetScroll() {
        if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mTotalUnconsumed = 0;
        }
        if (bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mBottomTotalUnconsumed = 0;
        }
    }

    boolean isTop = true;
    private float lastDy;


}
