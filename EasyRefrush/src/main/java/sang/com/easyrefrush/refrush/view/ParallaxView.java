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

public class ParallaxView extends BaseParallaxView implements IRefrushView {


    public ParallaxView(Context context) {
        this(context, null, 0);
    }

    public ParallaxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        final int childTop  ;
        final int childBottom ;
        if (getCurrentValue()>0) {
            childTop=getPaddingTop();
            childBottom=getCurrentValue() +childTop + getOriginalValue();
        }else {
            childTop=getPaddingTop()+getCurrentValue();
            childBottom= childTop + getOriginalValue();
        }
        layout((parentWidth / 2 - circleWidth / 2),childTop ,
                (parentWidth / 2 + circleWidth / 2), childBottom);
    }






}
