package sang.com.easyrefrush.refrush;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sang.com.easyrefrush.R;
import sang.com.easyrefrush.inter.OnRefreshListener;
import sang.com.easyrefrush.refrush.helper.animation.inter.AnimationCollection;
import sang.com.easyrefrush.refrush.inter.IRefrushView;

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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }


    public abstract void finishRefrush();

    public void setTopRefrushView(View topRefrushView) {
        this.topRefrushView = topRefrushView;
        if (topRefrushView==null){
            return;
        }
        addView(topRefrushView);
        if (topRefrushView instanceof IRefrushView) {
            topRefrush = (IRefrushView) topRefrushView;
        }
        if (topRefrushView instanceof AnimationCollection.IAnimationHelper) {
            topAnimationHelper = (AnimationCollection.IAnimationHelper) topRefrushView;
            topAnimationHelper.setAnimationListener(this);
        }
    }

    public void setBottomRefrushView(View bottomRefrushView) {
        this.bottomRefrushView = bottomRefrushView;
        if (bottomRefrushView==null){
            return;
        }
        addView(bottomRefrushView);
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
