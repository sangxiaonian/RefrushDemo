package sang.com.refrushdemo.refrush.inter;

import android.view.View;

import sang.com.refrushdemo.refrush.EnumCollections;

/**
 * 作者： ${PING} on 2018/7/10.
 * 刷新控件的风格
 */

public interface IRefrushHelper {

    /**
     * 能否在指定方向进行滑动
     *
     * @param direction
     * @return
     */
    boolean canScroll(EnumCollections.Direction direction);

    /**
     * 手指开始放下，触发 ACTION_DOWN 时间
     */
    void startSpinner();


    /**
     * 手指滑动时候调用
     * @param totaldragDistance 能滑动的最大偏移量
     * @param originalTop       控件初始化时候，所处位置
     * @param overscrollTop     手指滑动偏移量
     */
    float moveSpinner(int totaldragDistance, int originalTop, float overscrollTop);

    /**
     * 手指停止滑动，抬起手指
     *
     * @param overscrollTop 手指滑动总偏移量
     * @param totaldragDistance 手指能够滑动的最大偏移量
     */
    void finishSpinner(float overscrollTop,int totaldragDistance);

    /**
     * 根据滑动的距离更改当前控件
     *
     * @param offset 偏移量
     * @param view   需要被更改的View
     * @return
     */
    int changeViewByMove(int offset, View view);


    /**
     * 初始化位移
     * @param refrushView
     */
    int resetMove(View refrushView);
}


