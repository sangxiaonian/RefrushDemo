package sang.com.refrushdemo.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import sang.com.refrushdemo.refrush.inter.IRefrushView;

/**
 * 作者： ${PING} on 2018/7/11.
 */

public class TopRefrushView extends RelativeLayout implements IRefrushView {

    //原始高度
    private int mOriginalOffsetTop;
    //拖拽的总共距离
    private int mTotalDragDistance;
    /**
     * 当前所在位置
     */
    private int mCurrentTargetOffsetTop;


    public TopRefrushView(Context context) {
        this(context, null, 0);
    }

    public TopRefrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        post(new Runnable() {
            @Override
            public void run() {
                mTotalDragDistance = (int) (getMeasuredHeight() * 1.6f);
                mOriginalOffsetTop = mCurrentTargetOffsetTop - getMeasuredHeight();
            }
        });
    }


    private float lastOffset;

    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset
     */
    @Override
    public void changValue(float offset) {
        bringToFront();
        lastOffset = offset;
        mCurrentTargetOffsetTop += offset;
        requestLayout();
    }

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    @Override
    public void reset() {
        setVisibility(View.GONE);
        changValue(mOriginalOffsetTop - mCurrentTargetOffsetTop);
    }

    @Override
    public int getOriginalValue() {
        return mOriginalOffsetTop;
    }

    @Override
    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    @Override
    public int getCurrentValue() {
        return mCurrentTargetOffsetTop;
    }

    /**
     * 开始进行滑动
     *
     * @param overscrollTop
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

        int targetY = getOriginalValue() + (int) ((slingshotDist * dragPercent) + extraMove);

        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        changValue(targetY - getCurrentValue());
    }
}
