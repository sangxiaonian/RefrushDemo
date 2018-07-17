package sang.com.easyrefrush.refrush.view.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrushutils.JLog;

/**
 * 作者： ${PING} on 2018/7/16.
 */

public abstract class BaseRefrushView extends BasePickView {
    public BaseRefrushView(Context context) {
        super(context);
    }

    public BaseRefrushView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset 此次操作造成的该变量
     */
    @Override
    public void changValue(float offset) {
        bringToFront();
        helper.changValue(offset);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getCurrentValue()>getOriginalValue()?getCurrentValue():getOriginalValue();
        if (params.height < getOriginalValue()) {
            params.height = getOriginalValue();
        }
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
     * 手指滑动时候的处理
     *
     * @param overscrollTop 手指滑动的总距离
     */
    @Override
    public int moveSpinner(float overscrollTop) {
        final int targetY;

        if (overscrollTop>getTotalDragDistance()){
            JLog.e(overscrollTop+">>>"+getTotalDragDistance());
        }

        targetY = helper.moveSpinner(overscrollTop);
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        changValue(targetY - getCurrentValue());
        return targetY;
    }

    /**
     * 对对应的布局进行布置
     *
     * @param parentWidth
     * @param parentHeight
     */
    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        final int childBottom;
        final int childTop;
        if (getLoaction() == EnumCollections.Loaction.UP) {
            childBottom=getPaddingTop()+getCurrentValue();
            childTop=childBottom-circleHeight;
        } else {
            childTop = parentHeight -getPaddingBottom()-getCurrentValue();
            childBottom = childTop + circleHeight;

        }
        layout((parentWidth / 2 - circleWidth / 2), childTop,
                (parentWidth / 2 + circleWidth / 2), childBottom);

    }

    ;

    /**
     * 获取到头部类型
     *
     * @return 返回值为刷新控件类型
     */
    @Override
    public EnumCollections.HeadStyle getHeadStyle() {
        return EnumCollections.HeadStyle.REFRUSH;
    }

    /**
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public int getMinValueToScrollList() {
        return 0;

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
        animationHelper.animationToStart(getCurrentValue(), getOriginalValue());
    }
}
