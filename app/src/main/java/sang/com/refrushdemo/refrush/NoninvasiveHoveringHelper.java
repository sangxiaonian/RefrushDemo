package sang.com.refrushdemo.refrush;

import android.support.v4.view.ViewCompat;
import android.view.View;

import sang.com.refrushdemo.refrush.inter.IRefrushHelper;

/**
 * 作者： ${PING} on 2018/7/11.
 */

public class NoninvasiveHoveringHelper implements IRefrushHelper {
    /**
     * 能否在指定方向进行滑动
     *
     * @param direction
     * @return
     */
    @Override
    public boolean canScroll(EnumCollections.Direction direction) {
        return false;
    }

    /**
     * 手指开始放下，触发 ACTION_DOWN 时间
     */
    @Override
    public void startSpinner() {

    }

    /**
     * 手指滑动时候调用
     *
     * @param overscrollTop 手指滑动偏移量
     */
    @Override
    public float moveSpinner(int totaldragDistance, int originalTop, float overscrollTop) {
        //拖拽距离到最大距离的百分比
        float originalDragPercent = overscrollTop / totaldragDistance;
        //确定百分比
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) -totaldragDistance;
        //弹性距离
        float slingshotDist =totaldragDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;

        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = originalTop + (int) ((slingshotDist * dragPercent) + extraMove);

//        if (getRefrushView().getVisibility() != View.VISIBLE) {
//            getRefrushView().setVisibility(View.VISIBLE);
//        }
//
//        changView(targetY -getmCurrentTargetOffsetTop());
//        changeViewByMove(targetY,);
        return targetY;
    }

    /**
     * 手指停止滑动，抬起手指
     *
     * @param overscrollTop 手指滑动总偏移量
     */
    @Override
    public void finishSpinner(float overscrollTop,int mTotalDragDistance ) {
        if (overscrollTop > mTotalDragDistance) {

            //开始刷新动画

        } else {
            // cancel refresh
            //取消刷新动画
        }
    }

    /**
     * 根据滑动的距离更改当前控件
     *
     * @param offset
     * @param view
     * @return
     */
    @Override
    public int changeViewByMove(int offset, View view) {
        view.bringToFront();
        ViewCompat.offsetTopAndBottom(view, offset);
//        setmCurrentTargetOffsetTop(getRefrushView().getTop());
        return view.getTop();
    }

    /**
     * 初始化位移
     * @param refrushView
     */
    @Override
    public int resetMove(View refrushView) {

        return changeViewByMove(0,refrushView);
    }
}
