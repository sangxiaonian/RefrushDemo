package sang.com.easyrefrush;

import android.content.Context;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import sang.com.easyrefrush.refrush.BaseRefrushLayout;
import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrushutils.JLog;


/**
 * 作者： ${PING} on 2018/6/22.
 * 视差特效
 */

public class EasyRefrushLayoutView extends BaseRefrushLayout {


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


    public EasyRefrushLayoutView(Context context) {
        super(context);
    }

    public EasyRefrushLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyRefrushLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

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
            int basicCount = 0;
            if (topRefrushView != null) {
                basicCount++;
            }
            if (bottomRefrushView != null) {
                basicCount++;
            }
            if (childCount == (basicCount + 1)) {//出了刷新控件外，至少要有一个子控件
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!child.equals(topRefrushView) && !child.equals(bottomRefrushView)) {
                        mTarget = child;
                        break;
                    }
                }
            } else {
//                throw new RuntimeException(getClass().getName() + " can only be one childView");
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
        if (topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.REFRUSH) {
            mTotalUnconsumed = 0;
            mBottomTotalUnconsumed = 0;
        }
        mNestedScrollInProgress = true;
    }

    boolean isTop = true;

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
                mTotalUnconsumed = caculeUnConsum(dy, mTotalUnconsumed, topRefrush.getTotalDragDistance(), topRefrush.getMinValueToScrollList());
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

    private int lastDy;

    private int caculeUnConsum(int dy, int caculeNum, int maxValue, int minValue) {
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
