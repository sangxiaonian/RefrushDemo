package sang.com.refrushdemo.refrush.helper;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import sang.com.refrushdemo.R;

/**
 * 作者： ${PING} on 2018/7/10.
 * 非侵入式悬浮下拉刷新控件
 */

public class NoninvasiveHoveringStyleHelper {

    private final ViewGroup parent;
    private Context context;
    private View refrushView;
    private final float DRAG_RATE = 1.6f;
    //拖拽的总共距离
    private int mTotalDragDistance;

    //记录起始位置，refrushView 刷新完成之后所处的位置
    private int mOriginalOffsetTop;

    /**
     * 头部刷新控件顶部当前所在位置
     */
    private int mCurrentTargetOffsetTop;


    public NoninvasiveHoveringStyleHelper(Context context, ViewGroup parent) {
        this.context = context;
        this.parent = parent;
        getRefrushView();
        refrushView.post(new Runnable() {
            @Override
            public void run() {
                int measuredHeight = refrushView.getMeasuredHeight();
                mTotalDragDistance = (int) (measuredHeight * DRAG_RATE);
                mOriginalOffsetTop = -measuredHeight;
            }
        });
    }

    public int getmCurrentTargetOffsetTop() {
        return mCurrentTargetOffsetTop;
    }

    public void setmCurrentTargetOffsetTop(int mCurrentTargetOffsetTop) {
        this.mCurrentTargetOffsetTop = mCurrentTargetOffsetTop;
    }

    public int getmOriginalOffsetTop() {
        return mOriginalOffsetTop;
    }

    public View getRefrushView() {
        if (refrushView == null) {
            refrushView = LayoutInflater.from(context).inflate(R.layout.item_top, parent, false);
        }
        return refrushView;
    }

    /**
     * 唯一的子控件是否可以继续滑动
     *
     * @param direction -1 ，可以向上滑动 1 向下滑动
     * @return true 表示可以滑动 false 表示不可以
     */
    public boolean canChildScrollUp(int direction, View mTarget) {

        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(direction);
    }

    /**
     * 获取拖拽总距离
     *
     * @return
     */
    public int getmTotalDragDistance() {
        return mTotalDragDistance;
    }

    public boolean canChildScrollUp(View mTarget) {
        return canChildScrollUp(-1, mTarget);
    }


    public void moveSpinner(float overscrollTop) {
        //拖拽距离到最大距离的百分比
        float originalDragPercent = overscrollTop / getmTotalDragDistance();
        //确定百分比
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - getmTotalDragDistance();
        //弹性距离
        float slingshotDist = getmTotalDragDistance();
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;

        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = getmOriginalOffsetTop() + (int) ((slingshotDist * dragPercent) + extraMove);

        if (getRefrushView().getVisibility() != View.VISIBLE) {
            getRefrushView().setVisibility(View.VISIBLE);
        }

        changView(targetY -getmCurrentTargetOffsetTop());
    }

    /**
     * 将头布局位移指定距离
     *
     * @param offset 当前位移的距离
     */
    public void changView(int offset) {
        getRefrushView().bringToFront();
        ViewCompat.offsetTopAndBottom(getRefrushView(), offset);
        setmCurrentTargetOffsetTop(getRefrushView().getTop());

    }
}
