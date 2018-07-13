package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import sang.com.easyrefrush.refrush.inter.IRefrushView;


/**
 * 作者： ${PING} on 2018/7/11.
 * 刷新控件
 */
public class BottomRefrushView extends BaseRefrushView implements IRefrushView {


    public BottomRefrushView(Context context) {
        this(context, null, 0);
    }

    public BottomRefrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset
     */
    @Override
    public void changValue(float offset) {
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

    /**
     * 开始进行滑动
     *
     * @param overscrollTop
     */
    @Override
    public int moveSpinner(float overscrollTop) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        final int targetY = helper.moveSpinner(overscrollTop);
        int i = targetY - getCurrentValue();
        changValue(i);
        return targetY;
    }

    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
//        final int childTop = parentHeight - getCurrentValue() - getPaddingBottom() - circleHeight;
//        final int childBottom = childTop + circleHeight;

        final int childTop = 300;
        final int childBottom = 400;

        layout((parentWidth / 2 - circleWidth / 2), childTop,
                (parentWidth / 2 + circleWidth / 2), childBottom);

    }


    /**
     * 移动到初始位置的动画，取消或者刷新完成后执行
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToStart(int... value) {
        animationHelper.animationToStart(getCurrentValue(), 0);
    }

    /**
     * 移动到刷新位置的动画
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToRefrush(int... value) {
        animationHelper.animationToStart(getCurrentValue(), getTotalDragDistance());
    }

}
