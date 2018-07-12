package sang.com.refrushdemo.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import sang.com.refrushdemo.refrush.helper.view.ViewHelper;
import sang.com.refrushdemo.refrush.inter.IRefrushView;

/**
 * 作者： ${PING} on 2018/7/12.
 */

public abstract class BaseRefrushView extends RelativeLayout implements IRefrushView {
    protected IRefrushView helper;

    public BaseRefrushView(Context context) {
        this(context, null, 0);
    }

    public BaseRefrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }




    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        helper = new ViewHelper();
        post(new Runnable() {
            @Override
            public void run() {
                if (getTotalDragDistance() == 0) {
                    setTotalDragDistance((int) (getMeasuredHeight() * 1.6f));
                }
                if (getOriginalValue() == 0) {
                    setOriginalValue(getMeasuredHeight());
                }
            }
        });
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




}
