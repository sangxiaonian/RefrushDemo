package sang.com.refrushdemo.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import sang.com.refrushdemo.refrush.EnumCollections;
import sang.com.refrushdemo.refrush.inter.IRefrushView;

/**
 * 作者： ${PING} on 2018/7/12.
 */

public class ParallaxView extends BaseRefrushView implements IRefrushView {



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
        helper.reset();
        changValue(helper.getCurrentValue());
    }

    /**
     * 手指滑动时候的处理
     *
     * @param overscrollTop 手指滑动的总距离
     */
    @Override
    public int moveSpinner(float overscrollTop) {
        final int targetY = helper.moveSpinner(overscrollTop);
        changValue(targetY - getCurrentValue());
        return targetY;
    }


    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        layout((parentWidth / 2 - circleWidth / 2), getPaddingTop(),
                (parentWidth / 2 + circleWidth / 2), getCurrentValue() + getPaddingTop() + circleHeight);
    }

    /**
     * 获取到头部类型
     *
     * @return 返回值为刷新控件类型
     */
    @Override
    public EnumCollections.HeadStyle getHeadStyle() {
        return EnumCollections.HeadStyle.PARALLAX;
    }


    /**
     * 移动到初始位置的动画，取消或者刷新完成后执行
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToStart(int... value) {
        animationHelper.animationToStart(getCurrentValue(),0);
    }

    /**
     * 移动到刷新位置的动画
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToRefrush(int... value) {
        animationHelper.animationToRefrush(getCurrentValue(),0);
    }
}
