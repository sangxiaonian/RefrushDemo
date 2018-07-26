package sang.com.easyrefrush.refrush;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import sang.com.easyrefrush.R;
import sang.com.easyrefrush.inter.OnRefreshListener;
import sang.com.easyrefrush.refrush.helper.animation.inter.AnimationCollection;
import sang.com.easyrefrush.refrush.inter.IRefrushView;
import sang.com.easyrefrush.refrush.view.base.BasePickView;

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

    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    /**
     * 目标View，通常为recycleView，listView等被刷新的控件
     */
    protected View mTarget;



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


    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }


    public abstract void finishRefrush();

    private boolean hasView(View view){
        if (view==null){
            return false;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).equals(view)){
                return true;
            }
        }
        return false;
    }

    public void setTopRefrushView(View topRefrushView) {
        if (this.topRefrushView!=null){
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

        if (this.bottomRefrushView!=null){
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
