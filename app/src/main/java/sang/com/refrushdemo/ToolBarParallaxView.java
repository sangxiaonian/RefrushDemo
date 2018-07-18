package sang.com.refrushdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.view.base.BaseParallaxView;

/**
 * 作者： ${PING} on 2018/7/17.
 */

public class ToolBarParallaxView extends BaseParallaxView {

    private Toolbar toolbar;
    private View bgView;
    private int bgHeight;
    private ColorImageView imageView;
    private TextView tvTitle;


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
                tvTitle=findViewById(R.id.tv_title);
                bgView = findViewById(R.id.bg);
                imageView =findViewById(R.id.img);
                bgHeight = bgView.getMeasuredHeight();
                toolbar.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }


    @Override
    public int getTotalDragDistance() {
        return (int) (getOriginalValue() * 0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (toolbar != null) {
            final int childTop = (getPaddingTop() - getCurrentValue()) < 0 ? 0 : getPaddingTop() - getCurrentValue();
            toolbar.layout(getPaddingLeft(), childTop, getPaddingLeft() + toolbar.getMeasuredWidth(), childTop + toolbar.getMeasuredHeight());
        }
    }


    @Override
    public int moveSpinner(float overscrollTop) {
        final int targetY;
        if (overscrollTop <= 0) {
            targetY = (int) overscrollTop - getCurrentValue();
        } else {
            targetY = (int) (overscrollTop - getCurrentValue()) / 2;
            ;
        }
        changValue(targetY);
        return targetY;
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
        if (currentValue > 0) {
            toolbar.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        //此时恰好到toolbar完全显示出来的位置
        final float correctValue = bgHeight - toolbar.getMeasuredHeight();
        final float percent = Math.abs(currentValue) / correctValue > 1 ? 1 : Math.abs(currentValue) / correctValue;//滑动的百分比
        final int color = evaluateColor(percent, Color.TRANSPARENT, Color.WHITE);
        toolbar.setBackgroundColor(color);
        tvTitle.setTextColor(evaluateColor(percent, Color.BLACK, Color.RED));
        imageView.setCurrentColor(evaluateColor(percent, Color.BLACK, Color.RED));

    }

    @Override
    public void onFinishSpinner(float overscrollTop) {
        super.onFinishSpinner(overscrollTop);

        if (getCurrentValue() > 0) {
            return;
        }
        final float correctValue = bgHeight - toolbar.getMeasuredHeight();
        if (Math.abs(getCurrentValue())>correctValue) {
            animationToStart();
            animationHelper.animationToStart(getCurrentValue(),-getOriginalValue()+toolbar.getMeasuredHeight());
        }else {
            animationToStart();
        }


    }

    /**
     * 颜色计算器
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public int evaluateColor(float fraction, Integer startValue, Integer endValue) {
        int startInt = (Integer) startValue;
        float startA = ((startInt >> 24) & 0xff) / 255.0f;
        float startR = ((startInt >> 16) & 0xff) / 255.0f;
        float startG = ((startInt >> 8) & 0xff) / 255.0f;
        float startB = (startInt & 0xff) / 255.0f;

        int endInt = (Integer) endValue;
        float endA = ((endInt >> 24) & 0xff) / 255.0f;
        float endR = ((endInt >> 16) & 0xff) / 255.0f;
        float endG = ((endInt >> 8) & 0xff) / 255.0f;
        float endB = (endInt & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }

}
