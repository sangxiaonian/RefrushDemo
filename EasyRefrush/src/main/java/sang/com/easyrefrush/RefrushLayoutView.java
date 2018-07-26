package sang.com.easyrefrush;

import android.content.Context;
import android.os.Build;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.FloatValueHolder;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import sang.com.easyrefrush.refrush.BaseRefrushLayout;
import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrushutils.JLog;


/**
 * 作者： ${PING} on 2018/6/22.
 * 支持 5.0 一下的控件，并且支持惯性滑动
 */

public class RefrushLayoutView extends BaseRefrushLayout implements GestureDetector.OnGestureListener, DynamicAnimation.OnAnimationUpdateListener, DynamicAnimation.OnAnimationEndListener {


    private FlingAnimation flingAnimation;
    private GestureDetector mGestureDetector;
    protected boolean supportFling = true;

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
        mGestureDetector = new GestureDetector(context, this);
        flingAnimation = new FlingAnimation(new FloatValueHolder(0));
        flingAnimation.addUpdateListener(this);
        flingAnimation.addEndListener(this);

    }

    /**
     * 设置是否支持惯性滑动
     *
     * @param supportFling true 支持，false不支持
     */
    public void setSupportFling(boolean supportFling) {
        this.supportFling = supportFling;
    }

    //触摸事件分发拦截
    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {

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
        if (flingAnimation.isRunning()) {
            flingAnimation.cancel();
        }
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
                            //自己消费触摸事件，不再向下传递
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
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                moveY = 0;
                lastDy = 0;
                if (isTop) {
                    finishSpinner();
                } else {
                    finishSpinner();
                }
                break;
        }


        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {

        if (supportFling) {
            if (!mReturningToStart && topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
                startFling(-velocityY, mTotalUnconsumed, topRefrush.getOriginalValue(), topRefrush.getMinValueToScrollList());
                return true;
            } else if (!mReturningToStart && bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && !isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {
                startFling(velocityY, mBottomTotalUnconsumed, bottomRefrush.getOriginalValue(), bottomRefrush.getMinValueToScrollList());
                return true;
            } else {
                return super.onNestedPreFling(target, velocityX, velocityY);
            }
        } else {

            if (!mReturningToStart && topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
                return true;
            } else if (!mReturningToStart && bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && !isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {
                return true;
            } else {
                return super.onNestedPreFling(target, velocityX, velocityY);
            }
        }
    }

    private void startFling(float velocityY, float startValue, float maxValue, float minValue) {

        if (startValue < 0 && startValue > minValue) {
            flingAnimation.cancel();
            flingAnimation.setStartValue(startValue)
                    .setMaxValue(0)
                    .setStartVelocity(velocityY)
                    .setMinValue(minValue);
            flingAnimation.start();
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (supportFling && topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
            return true;
        } else if (supportFling && bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && !isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {

            return true;
        } else {
            if (!mReturningToStart && topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
                return true;
            } else if (!mReturningToStart && bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && !isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {
                return true;
            } else {
                return super.onNestedPreFling(target, velocityX, velocityY);
            }
        }

    }

    /**
     * Notifies the occurrence of another frame of the animation.
     *
     * @param animation     animation that the update listener is added to
     * @param animatedValue the current value of the animation
     * @param velocity      the current velocity of the animation
     */
    @Override
    public void onAnimationUpdate(DynamicAnimation animation, float animatedValue, float velocity) {
        if (isTop) {
            if (topRefrush != null) {
                if (animatedValue >= 0 || animatedValue <= topRefrush.getMinValueToScrollList()) {
                    flingAnimation.cancel();
                    if (animatedValue >= 0) {
                        mTotalUnconsumed = 0;
                        topRefrush.moveSpinner(mTotalUnconsumed);
                    } else if (animatedValue <= topRefrush.getMinValueToScrollList()) {
                        mTotalUnconsumed = topRefrush.getMinValueToScrollList();
                        topRefrush.moveSpinner(mTotalUnconsumed);
                    }

                } else {
                    mTotalUnconsumed = (int) animatedValue;
                    topRefrush.moveSpinner(mTotalUnconsumed);
                }
            }
        } else {
            if (bottomRefrush != null) {
                if (animatedValue >= 0 || animatedValue <= bottomRefrush.getMinValueToScrollList()) {
                    flingAnimation.cancel();
                    if (animatedValue >= 0) {
                        mBottomTotalUnconsumed = 0;
                        bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                    } else if (animatedValue <= bottomRefrush.getMinValueToScrollList()) {
                        mBottomTotalUnconsumed = bottomRefrush.getMinValueToScrollList();
                        bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                    }
                } else {
                    mBottomTotalUnconsumed = (int) animatedValue;
                    bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                }


            }
        }

    }

    @Override
    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
//        finishSpinner();
        lastDy = 0;
        moveY = 0;
    }

    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        if (flingAnimation.isRunning()) {
            flingAnimation.cancel();

        }
        moveY = 0;
        lastDy = 0;
        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }


    private float moveY;

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final float dy;
        float rawY = e2.getRawY();
        if (moveY == 0) {
            moveY = rawY;
            return true;
        } else {
            dy = -moveY + rawY;
        }
        moveY = rawY;
        if (dy > 0) {//向下滑动
            if (topRefrush != null && !canChildScrollUp(-1) && topRefrush != null) {//此时控件无法向下滑动
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
            if (bottomRefrush != null && !canChildScrollUp(1)) {//此时控件已经到达底部，无法向上滑动
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

        return intercept;
    }


    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        moveY = 0;
        if (!mReturningToStart && supportFling && topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
            startFling(velocityY, mTotalUnconsumed, topRefrush.getOriginalValue(), topRefrush.getMinValueToScrollList());
            return true;
        } else if (!mReturningToStart && supportFling && bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && !isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {

            startFling(-velocityY, mBottomTotalUnconsumed, bottomRefrush.getOriginalValue(), bottomRefrush.getMinValueToScrollList());

            return true;
        } else {
            if (!mReturningToStart && topRefrush != null && topRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
                return true;
            } else if (!mReturningToStart && bottomRefrush != null && bottomRefrush.getHeadStyle() == EnumCollections.HeadStyle.PARALLAX && !isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
