package sang.com.refrushdemo.refrush.helper;

import android.content.Context;
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
    private View refrushView;

    private int refrushSize;


    public NoninvasiveHoveringStyleHelper(ViewGroup parent) {
        this.parent = parent;
    }

    public View getRefrushView(Context context) {
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

    public boolean canChildScrollUp(View mTarget) {
        return canChildScrollUp(-1, mTarget);
    }


    public void moveSpinner(int mTotalUnconsumed) {

    }
}
