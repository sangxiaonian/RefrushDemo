package sang.com.easyrefrush;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;

import sang.com.easyrefrush.refrush.BaseRefrushLayout;
import sang.com.easyrefrush.refrush.EnumCollections;


/**
 * 作者： ${PING} on 2018/6/22.
 * 视差特效
 */

public class TouchRefrushLayoutView extends BaseRefrushLayout {




    public TouchRefrushLayoutView(Context context) {
        super(context);
    }

    public TouchRefrushLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchRefrushLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
