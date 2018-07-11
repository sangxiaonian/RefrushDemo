package sang.com.refrushdemo.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
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
        this(context,null,0);
    }

    public TopRefrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TopRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs,defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

    }


    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param overscroll
     */
    @Override
    public void changValue(float overscroll) {

    }
}
