package sang.com.refrushdemo.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import sang.com.refrushdemo.refrush.helper.view.ViewHelper;
import sang.com.refrushdemo.refrush.inter.IRefrushView;
import sang.com.refrushdemo.utils.JLog;

/**
 * 作者： ${PING} on 2018/7/11.
 */

public class TopRefrushView extends RelativeLayout implements IRefrushView {




    private IRefrushView helper;

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
        helper = new ViewHelper();
        post(new Runnable() {
            @Override
            public void run() {
                if (getTotalDragDistance()==0) {
                    setTotalDragDistance((int) (getMeasuredHeight() * 1.6f));
                }
                setOriginalValue(getMeasuredHeight());
                reset();
            }
        });
    }


    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset
     */
    @Override
    public void changValue(float offset) {
        JLog.i("----------------");
        bringToFront();
        helper.changValue(offset);
        requestLayout();
    }

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    @Override
    public void reset() {
        setVisibility(View.GONE);
        helper.reset();
        changValue(helper.getCurrentValue());
    }

    @Override
    public int getOriginalValue() {
        return helper.getOriginalValue();
    }

    /**
     * 设置View的初始状态值，一般为高度 或者Top值
     *
     * @param mOriginalOffsetTop
     */
    @Override
    public void setOriginalValue(int mOriginalOffsetTop) {
        helper.setOriginalValue(mOriginalOffsetTop);
    }

    @Override
    public int getTotalDragDistance() {
        return helper.getTotalDragDistance();
    }

    /**
     * 设置允许被拖拽的最大距离
     *
     * @param totalDragDistance
     */
    @Override
    public void setTotalDragDistance(int totalDragDistance) {
        helper.setTotalDragDistance(totalDragDistance);
    }

    @Override
    public int getCurrentValue() {
        return helper.getCurrentValue();
    }



    /**
     * 开始进行滑动
     *
     * @param overscrollTop
     */
    @Override
    public int moveSpinner(float overscrollTop) {
        if (getVisibility()!=VISIBLE){
            setVisibility(VISIBLE);
        }
        final int targetY = helper.moveSpinner(overscrollTop );
        int i = targetY - getCurrentValue();
        JLog.i("==="+i);
        changValue(i);
        return targetY;
    }

    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        layout((parentWidth / 2 - circleWidth / 2), getCurrentValue() + getPaddingTop() - circleHeight,
                (parentWidth / 2 + circleWidth / 2), getCurrentValue() + getPaddingTop());
    }
}
