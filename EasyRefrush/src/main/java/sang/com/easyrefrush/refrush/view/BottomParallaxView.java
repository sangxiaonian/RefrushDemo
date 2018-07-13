package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.inter.IRefrushView;


/**
 * 作者： ${PING} on 2018/7/12.
 * 视差特效
 *
 *
 */

public class BottomParallaxView extends BaseRefrushView implements IRefrushView {


    public BottomParallaxView(Context context) {
        this(context, null, 0);
    }

    public BottomParallaxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomParallaxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        params.height = getOriginalValue() + getCurrentValue();
        if (params.height < 0) {
            params.height = 0;
        }
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
        if (getMeasuredHeight() > getOriginalValue()) {//正常情况下的变化
            targetY = helper.moveSpinner(overscrollTop);
        } else {//当向上滑动到原来位置之后，继续向上滑动
            targetY = (int) overscrollTop;
        }
        changValue(targetY - getCurrentValue());
        return targetY;
    }


    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();

        final int childBottom = parentHeight-getPaddingBottom() ;
        final int childTop = childBottom-getOriginalValue()-getCurrentValue();


        layout((parentWidth / 2 - circleWidth / 2), childTop,
                (parentWidth / 2 + circleWidth / 2), childBottom);
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
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public int getMinValueToScrollList() {
        return -getOriginalValue();
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
        animationHelper.animationToRefrush(getCurrentValue(), 0);
    }
}
