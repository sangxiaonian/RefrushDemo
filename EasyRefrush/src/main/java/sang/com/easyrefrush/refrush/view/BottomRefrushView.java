package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.inter.IRefrushView;


/**
 * 作者： ${PING} on 2018/7/12.
 * 视差特效
 */

public class BottomRefrushView extends BaseRefrushView implements IRefrushView {


    public BottomRefrushView(Context context) {
        this(context, null, 0);
    }

    public BottomRefrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        final int childBottom;
        final int childTop = parentHeight - getCurrentValue();
        childBottom = childTop +circleHeight;
        layout((parentWidth / 2 - circleWidth / 2), childTop,
                (parentWidth / 2 + circleWidth / 2),childBottom);


    }

}
