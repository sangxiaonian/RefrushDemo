package sang.com.easyrefrush.refrush.view.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.helper.animation.AnimationRefrush;
import sang.com.easyrefrush.refrush.helper.animation.inter.AnimationCollection;
import sang.com.easyrefrush.refrush.helper.view.ViewHelper;
import sang.com.easyrefrush.refrush.inter.IRefrushView;


/**
 * 作者： ${PING} on 2018/7/12.
 */

 public abstract class BasePickView extends RelativeLayout implements IRefrushView,AnimationCollection.IAnimationHelper {
    protected IRefrushView helper;
    protected AnimationCollection.IAnimationHelper animationHelper;
    protected EnumCollections.Loaction loaction= EnumCollections.Loaction.UP;

    public BasePickView(Context context) {
        this(context, null, 0);
    }

    public BasePickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }




    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        helper = new ViewHelper();
        animationHelper=new AnimationRefrush();
        post(new Runnable() {
            @Override
            public void run() {
                if (getTotalDragDistance() == 0) {
                    setTotalDragDistance((int) (getMeasuredHeight() * 3f));
                }
                if (getOriginalValue() == 0) {
                    setOriginalValue(getMeasuredHeight());
                }
                reset();
            }
        });
    }


    @Override
    public void onFinishSpinner(float overscrollTop) {

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
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public int getMinValueToScrollList() {
        return 0;
    }

    /**
     * 用来设置布局为头布局还是脚布局，UP ，Down
     *
     * @param loaction
     */
    @Override
    public void setLoaction(EnumCollections.Loaction loaction) {
        this.loaction=loaction;
    }

    /**
     * 获取当前布局为头布局还是脚布局
     *
     * @return 默认为头布局
     */
    @Override
    public EnumCollections.Loaction getLoaction() {
        return loaction;
    }

    @Override
    public EnumCollections.HeadStyle getHeadStyle() {
        return EnumCollections.HeadStyle.REFRUSH;
    }

    /**
     * 设置动画监听
     *
     * @param listener
     */
    @Override
    public void setAnimationListener(AnimationCollection.IAnimationListener listener) {
        animationHelper.setAnimationListener(listener);
    }

    @Override
    public void finishSpinner(float overscrollTop) {

        if (getCurrentValue()>=getOriginalValue()){
            animationToRefrush();
        }else {
            animationToStart();
        }

    }
}
