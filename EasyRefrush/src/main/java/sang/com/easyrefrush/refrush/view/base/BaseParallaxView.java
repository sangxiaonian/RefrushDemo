package sang.com.easyrefrush.refrush.view.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.inter.IRefrushView;
import sang.com.easyrefrush.refrushutils.JLog;


/**
 * 作者： ${PING} on 2018/7/12.
 * 视差特效
 */

public abstract class BaseParallaxView extends BasePickView implements IRefrushView {


    public BaseParallaxView(Context context) {
        this(context, null, 0);
    }

    public BaseParallaxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseParallaxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        ViewGroup.LayoutParams params = getLayoutParams();
        int height = getOriginalValue() + getCurrentValue();
        if (height < getOriginalValue()) {
            height = getOriginalValue();
        }
        params.height = height;
        onViewSizeChange(getCurrentValue(), params.height);
    }

    /**
     * 该控件变化监听
     *
     * @param currentValue 当前高度变化量
     * @param height       控件高度
     */
    protected void onViewSizeChange(int currentValue, int height) {

    }

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    @Override
    public void reset() {
        requestLayout();

    }

    /**
     * 手指滑动时候的处理
     *
     * @param overscrollTop 手指滑动的总距离
     */
    @Override
    public int moveSpinner(float overscrollTop) {
        final int targetY;
        targetY = (int) overscrollTop;
        changValue(targetY - getCurrentValue());
        return targetY;
    }

    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        final int childBottom;
        final int childTop;

        if (getLoaction().equals(EnumCollections.Loaction.Down)) {
            if (getCurrentValue() > 0) {
                childBottom = parentHeight - getPaddingBottom();
            } else {
                childBottom = parentHeight - getPaddingBottom() - getCurrentValue();
            }
            childTop = childBottom - circleHeight;
            layout((parentWidth / 2 - circleWidth / 2), childTop,
                    (parentWidth / 2 + circleWidth / 2), childBottom);
        } else {

            childBottom = getCurrentValue() + getPaddingTop() + getOriginalValue();
            childTop = childBottom - circleHeight;
            layout((parentWidth / 2 - circleWidth / 2), childTop,
                    (parentWidth / 2 + circleWidth / 2), childBottom);
        }
    }

    ;

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
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public abstract int getMinValueToScrollList();

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
        animationHelper.animationToRefrush(getCurrentValue(), 0);
    }


}
