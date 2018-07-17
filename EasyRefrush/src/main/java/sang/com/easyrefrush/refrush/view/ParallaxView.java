package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.inter.IRefrushView;
import sang.com.easyrefrush.refrush.view.base.BaseParallaxView;


/**
 * 作者： ${PING} on 2018/7/12.
 * 视差特效
 *
 *
 */

public class ParallaxView extends BaseParallaxView implements IRefrushView {


    public ParallaxView(Context context) {
        this(context, null, 0);
    }

    public ParallaxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLoaction(EnumCollections.Loaction.UP);
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


}
