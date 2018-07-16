package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.helper.animation.AnimationRefrush;
import sang.com.easyrefrush.refrush.helper.view.ViewHelper;
import sang.com.easyrefrush.refrush.inter.IRefrushView;


/**
 * 作者： ${PING} on 2018/7/12.
 * 弹性过量滑动
 */

public class OverScrollView extends BasePickView implements IRefrushView {


    public OverScrollView(Context context) {
        this(context, null, 0);
    }

    public OverScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        helper = new ViewHelper();
        animationHelper=new AnimationRefrush();
        post(new Runnable() {
            @Override
            public void run() {
                if (getTotalDragDistance() == 0) {
                    setTotalDragDistance((int) (getMeasuredHeight() * 1.6f));
                }
                    setOriginalValue(0);
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOriginalValue(0);
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
        helper.reset();
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = 0;
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
        layout((parentWidth / 2 - circleWidth / 2), getPaddingTop(),
                (parentWidth / 2 + circleWidth / 2), getCurrentValue() + getPaddingTop() + getOriginalValue());
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
