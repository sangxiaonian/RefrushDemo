package sang.com.easyrefrush;

import android.content.Context;
import android.os.Build;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.FloatValueHolder;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import sang.com.easyrefrush.refrush.BaseRefrushLayout;
import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.view.base.BasePickView;
import sang.com.easyrefrush.refrushutils.JLog;


/**
 * 作者： ${PING} on 2018/6/22.
 * 视差特效
 */

public class RefrushLayoutView extends BaseRefrushLayout implements GestureDetector.OnGestureListener,DynamicAnimation.OnAnimationUpdateListener, DynamicAnimation.OnAnimationEndListener {


    private FlingAnimation flingAnimation;
    private GestureDetector mGestureDetector;

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
        mGestureDetector=new GestureDetector(context,this);
        flingAnimation = new FlingAnimation(new FloatValueHolder(0));
//        flingAnimation.setFriction();
        flingAnimation.addUpdateListener(this);
        flingAnimation.addEndListener(this);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (flingAnimation.isRunning()){
            flingAnimation.cancel();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                moveY=0;
                lastDy=0;
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

        if (topRefrush!=null&&topRefrush.getHeadStyle()== EnumCollections.HeadStyle.PARALLAX&&isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
            flingAnimation.setStartValue(mTotalUnconsumed)
                    .setMaxValue( topRefrush.getOriginalValue())
                    .setStartVelocity(-velocityY)
                    .setMinValue(topRefrush.getMinValueToScrollList());
            flingAnimation.start();
            return true;
        }else if (bottomRefrush!=null&&bottomRefrush.getHeadStyle()== EnumCollections.HeadStyle.PARALLAX&&!isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {
            flingAnimation.setStartValue(mBottomTotalUnconsumed)
                    .setMaxValue( bottomRefrush.getTotalDragDistance())
                    .setStartVelocity(velocityY)
                    .setMinValue(bottomRefrush.getMinValueToScrollList());
            flingAnimation.start();
            return true;
        }else {
            return super.onNestedPreFling(target, velocityX, velocityY);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (topRefrush!=null&&topRefrush.getHeadStyle()== EnumCollections.HeadStyle.PARALLAX&&isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
            return true;
        }else if (bottomRefrush!=null&&bottomRefrush.getHeadStyle()== EnumCollections.HeadStyle.PARALLAX&&!isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {

            return true;
        }else {
            return super.onNestedFling(target, velocityX, velocityY,consumed);
        }

    }

    /**
     * Notifies the occurrence of another frame of the animation.
     *
     * @param animation animation that the update listener is added to
     * @param animatedValue     the current value of the animation
     * @param velocity  the current velocity of the animation
     */
    @Override
    public void onAnimationUpdate(DynamicAnimation animation, float animatedValue, float velocity) {
        if (isTop) {
            if (topRefrush != null) {
                if (animatedValue>=topRefrush.getTotalDragDistance()||animatedValue<=topRefrush.getMinValueToScrollList()){
                    flingAnimation.cancel();
                    if (animatedValue>=topRefrush.getTotalDragDistance()){
                        mTotalUnconsumed=topRefrush.getTotalDragDistance();
                        finishSpinner();
                    }else if (animatedValue<=topRefrush.getMinValueToScrollList()){
                        mTotalUnconsumed=topRefrush.getMinValueToScrollList();
                        topRefrush.moveSpinner(mTotalUnconsumed);
                    }

                }else {
                    topRefrushMove(animatedValue-mTotalUnconsumed);
                }
            }
        } else {
            if (bottomRefrush != null) {
                if (animatedValue>=bottomRefrush.getTotalDragDistance()||animatedValue<=bottomRefrush.getMinValueToScrollList()){
                    flingAnimation.cancel();
                    if (animatedValue>=bottomRefrush.getTotalDragDistance()){
                        mBottomTotalUnconsumed=bottomRefrush.getTotalDragDistance();
                        finishSpinner();
                    }else if (animatedValue<=bottomRefrush.getMinValueToScrollList()){
                        mBottomTotalUnconsumed=bottomRefrush.getMinValueToScrollList();
                        bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                    }
                }else {
                    mBottomTotalUnconsumed = (int) animatedValue;
                    bottomRefrush.moveSpinner(mBottomTotalUnconsumed);
                }


            }
        }

    }

    @Override
    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        finishSpinner();
        lastDy=0;
        moveY=0;
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
        moveY=0;
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
        if (moveY==0){
            moveY= rawY;
            return true;
        }else {
            dy=-moveY+ rawY;
        }
        moveY=rawY;
        JLog.i(dy+">>>"+moveY+">>>"+rawY+">>>>"+mTotalUnconsumed);

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

        return intercept;
    }




    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        moveY=0;
        if (topRefrush!=null&&topRefrush.getHeadStyle()== EnumCollections.HeadStyle.PARALLAX&&isTop && mTotalUnconsumed > topRefrush.getMinValueToScrollList() && mTotalUnconsumed < topRefrush.getTotalDragDistance()) {
            flingAnimation.setStartValue(mTotalUnconsumed)
                    .setMaxValue( topRefrush.getOriginalValue())
                    .setStartVelocity(velocityY)
                    .setMinValue(topRefrush.getMinValueToScrollList());
            flingAnimation.start();
            return true;
        }else if (bottomRefrush!=null&&bottomRefrush.getHeadStyle()== EnumCollections.HeadStyle.PARALLAX&&!isTop && mBottomTotalUnconsumed > bottomRefrush.getMinValueToScrollList() && mBottomTotalUnconsumed < bottomRefrush.getTotalDragDistance()) {
            flingAnimation.setStartValue(mBottomTotalUnconsumed)
                    .setMaxValue( bottomRefrush.getTotalDragDistance())
                    .setStartVelocity(-velocityY)
                    .setMinValue(bottomRefrush.getMinValueToScrollList());
            flingAnimation.start();
            return true;
        }else {
            return false;
        }
    }
}
