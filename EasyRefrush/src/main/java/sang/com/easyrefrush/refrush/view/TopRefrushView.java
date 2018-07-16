package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import sang.com.easyrefrush.refrush.inter.IRefrushView;


/**
 * 作者： ${PING} on 2018/7/11.
 * 刷新控件
 */
public class TopRefrushView extends BaseRefrushView implements IRefrushView {


    public TopRefrushView(Context context) {
        this(context, null, 0);
    }

    public TopRefrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopRefrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void layoutChild(int parentWidth, int parentHeight) {
        final int circleWidth = getMeasuredWidth();
        final int circleHeight = getMeasuredHeight();
        final int childTop = getCurrentValue() + getPaddingTop() - circleHeight;
        final int childBottom =childTop+circleHeight;
        layout((parentWidth / 2 - circleWidth / 2), childTop,
                (parentWidth / 2 + circleWidth / 2), childBottom);
    }




}
