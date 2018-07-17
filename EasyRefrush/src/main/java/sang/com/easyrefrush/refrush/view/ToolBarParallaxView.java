package sang.com.easyrefrush.refrush.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import sang.com.easyrefrush.R;
import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.view.base.BaseParallaxView;
import sang.com.easyrefrush.refrushutils.JLog;

/**
 * 作者： ${PING} on 2018/7/17.
 */

public class ToolBarParallaxView extends BaseParallaxView {

    private Toolbar toolbar;
    private View bgView;


    public ToolBarParallaxView(Context context) {
        super(context);
    }

    public ToolBarParallaxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBarParallaxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLoaction(EnumCollections.Loaction.UP);
    }


    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);

        post(new Runnable() {
            @Override
            public void run() {
                toolbar = findViewById(R.id.toolbar);
                bgView = findViewById(R.id.bg);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (toolbar != null) {
            final int childTop = (getPaddingTop() - getCurrentValue()) < 0 ? 0 : getPaddingTop() - getCurrentValue();
            toolbar.layout(getPaddingLeft(), childTop, getPaddingLeft() + toolbar.getMeasuredWidth(), childTop + toolbar.getMeasuredHeight());
        }
    }

    /**
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public int getMinValueToScrollList() {
        return -getOriginalValue() + toolbar.getMeasuredHeight();
    }

    @Override
    protected void onViewSizeChange(int currentValue, int height) {
        super.onViewSizeChange(currentValue, height);




    }

}
