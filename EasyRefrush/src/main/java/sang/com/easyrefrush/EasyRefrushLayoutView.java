package sang.com.easyrefrush;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import sang.com.easyrefrush.inter.OnRefreshListener;

/**
 * 作者： ${PING} on 2018/7/26.
 */

public class EasyRefrushLayoutView extends RefrushLayoutView {
    public EasyRefrushLayoutView(Context context) {
        super(context);
    }

    public EasyRefrushLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyRefrushLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 动态设置底部状态
     *
     * @param bottomRefrushView
     */
    @Override
    public void setBottomRefrushView(View bottomRefrushView) {
        super.setBottomRefrushView(bottomRefrushView);
    }

    /**
     * 动态这是顶部控件
     *
     * @param topRefrushView
     */
    @Override
    public void setTopRefrushView(View topRefrushView) {
        super.setTopRefrushView(topRefrushView);
    }

    /**
     * 设置刷新监听
     *
     * @param mListener
     */
    @Override
    public void setOnRefreshListener(OnRefreshListener mListener) {
        super.setOnRefreshListener(mListener);
    }

    /**
     * 刷新完成
     */
    @Override
    public void finishRefrush() {
        super.finishRefrush();
    }
}
