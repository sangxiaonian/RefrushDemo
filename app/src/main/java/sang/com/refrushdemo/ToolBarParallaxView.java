package sang.com.refrushdemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.view.base.BaseParallaxView;

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
        toolbar=findViewById(R.id.toolbar);
        bgView=findViewById(R.id.bg);
        post(new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    /**
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public int getMinValueToScrollList() {
        return -getOriginalValue()/2;
    }

    @Override
    protected void onViewSizeChange(int currentValue, int height) {
        super.onViewSizeChange(currentValue, height);
    }

}
