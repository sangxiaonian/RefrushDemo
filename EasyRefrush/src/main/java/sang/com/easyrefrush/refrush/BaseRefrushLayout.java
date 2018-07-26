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
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

import sang.com.easyrefrush.inter.OnRefreshListener;
import sang.com.easyrefrush.refrush.helper.animation.inter.AnimationCollection;
import sang.com.easyrefrush.refrush.inter.IRefrushView;
import sang.com.easyrefrush.refrush.view.base.BasePickView;

/**
 * 作者： ${PING} on 2018/7/11.
 * <p>
 * 支持5.0 以上 nestScrollEnable 为true 的控件
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
            if (topRefrush != null && mTotalUnconsumed > topRefrush.getOriginalValue() && topRefrush.getHeadStyle().equals(EnumCollections.HeadStyle.REFRUSH)) {
                //开始刷新动画
                mRefreshing = true;
            } else {
                mRefreshing = false;
            }
            if (topRefrush != null) {
                topRefrush.finishSpinner(mTotalUnconsumed);
            }

        } else {
            if (bottomRefrush != null && mBottomTotalUnconsumed > bottomRefrush.getOriginalValue() && bottomRefrush.getHeadStyle().equals(EnumCollections.HeadStyle.REFRUSH)) {
                //开始刷新动画
                mRefreshing = true;
            } else {
                //取消刷新动画
                mRefreshing = false;
            }
            if (bottomRefrush != null) {
                bottomRefrush.finishSpinner(mBottomTotalUnconsumed);
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

    public void setRefreshing() {
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
                if (!isTop && !mTarget.isNestedScrollingEnabled() && mBottomTotalUnconsumed > 0) {
                    childTop = childTop - mBottomTotalUnconsumed;
                }
            } else {
                if (!isTop && mBottomTotalUnconsumed > 0) {
                    childTop = childTop - mBottomTotalUnconsumed;
                }
            }
        } else {
            childBottom = height - getPaddingBottom();
        }
        child.layout(childLeft, childTop, childLeft + childWidth, childBottom);

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
    protected boolean mNestedScrollInProgress;

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


        if (dy<0){
            if (topRefrush!=null&&!canChildScrollUp(-1)){
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
            }else if (bottomRefrush!=null&&mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList()){
                bottomRefrushMove(dy);
                consumed[1] = dy;
                if (isTop) {
                    isTop = false;
                }
                bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
            }

        }else if (dy>0){
            if (topRefrush!=null&&mTotalUnconsumed > topRefrush.getMinValueToScrollList()){
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
            }else if (bottomRefrush!=null&& !canChildScrollUp(1)){
                bottomRefrushMove(dy);
                consumed[1] = dy;
                if (isTop) {
                    isTop = false;
                }
                bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
            }

        }


//        if (topRefrush != null
//                && ((dy < 0 && !canChildScrollUp(-1))//如果是下拉操作，消耗掉所有的数据
//                || (dy > 0 && mTotalUnconsumed > topRefrush.getMinValueToScrollList())//如果是想上滑动
//        )) {
//            if (dy > mTotalUnconsumed - topRefrush.getMinValueToScrollList() && mTotalUnconsumed > topRefrush.getMinValueToScrollList()) {
//                consumed[1] = dy - (int) mTotalUnconsumed;
//                mTotalUnconsumed = topRefrush.getMinValueToScrollList();
//            } else {
//                topRefrushMove(-dy);
//            }
//            consumed[1] = dy;
//            if (!isTop) {
//                isTop = true;
//            }
//            topRefrush.moveSpinner(mTotalUnconsumed);
//        } else if (
//                bottomRefrush != null &&
//                        ((dy > 0 && !canChildScrollUp(1))//如果是上滑操作，消耗掉所有的数据
//                                || (dy < 0 && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList())//如果是想上滑动
//                        )) {
////            mBottomTotalUnconsumed = caculeUnConsum(-dy, mBottomTotalUnconsumed, bottomRefrush.getTotalDragDistance(), bottomRefrush.getMinValueToScrollList());
//            bottomRefrushMove(dy);
//            consumed[1] = dy;
//            if (isTop) {
//                isTop = false;
//            }
//            bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
//        }
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
