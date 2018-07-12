package sang.com.refrushdemo.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import sang.com.refrushdemo.refrush.inter.IRefrushView;

/**
 * 作者： ${PING} on 2018/7/12.
 */

public class ParallaxView extends RelativeLayout implements IRefrushView {
    private int mTotalDragDistance;
    private int mCurrentTargetOffsetTop;
    private int mOriginalOffsetTop;

    public ParallaxView(Context context) {
        this(context, null, 0);
    }

    public ParallaxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        post(new Runnable() {
            @Override
            public void run() {
                mTotalDragDistance = (int) (getMeasuredHeight() * 1.6f);
                mOriginalOffsetTop =  getMeasuredHeight();
            }
        });
    }


    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset 此次操作造成的该变量
     */
    @Override
    public void changValue(float offset) {

    }

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    @Override
    public void reset() {

    }

    /**
     * 获取到View的初始状态值，一般为高度 或者Top值
     *
     * @return
     */
    @Override
    public int getOriginalValue() {
        return mOriginalOffsetTop;
    }

    /**
     * @return 允许被拖拽的最大距离
     */
    @Override
    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    /**
     * @return View当前的状态值，一般为高度或者Top值
     */
    @Override
    public int getCurrentValue() {
        return mCurrentTargetOffsetTop;
    }

    /**
     * 手指滑动时候的处理
     *
     * @param overscrollTop 手指滑动的总距离
     */
    @Override
    public void moveSpinner(float overscrollTop) {
//拖拽距离到最大距离的百分比
        float originalDragPercent = overscrollTop / getTotalDragDistance();
        //确定百分比
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - getTotalDragDistance();
        //弹性距离
        float slingshotDist = getTotalDragDistance();
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;

        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = (int) ((slingshotDist * dragPercent) + extraMove);

        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
        changValue(targetY - getCurrentValue());
    }

    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        layout((parentWidth / 2 - circleWidth / 2), getCurrentValue() + getPaddingTop() ,
                (parentWidth / 2 + circleWidth / 2), getCurrentValue() + getPaddingTop()- circleHeight);
    }
}
