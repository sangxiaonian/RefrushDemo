package sang.com.easyrefrush.refrush.helper.view;


import sang.com.easyrefrush.refrush.EnumCollections;
import sang.com.easyrefrush.refrush.inter.IRefrushView;

/**
 * 作者： ${PING} on 2018/7/12.
 */

public class ViewHelper implements IRefrushView {

    /**
     * 当前所在位置
     */
    private int mCurrentTargetOffsetTop;
    private int mOriginalOffsetTop;
    private int totalDragDistance;

    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset 此次操作造成的该变量
     */
    @Override
    public void changValue(float offset) {
        mCurrentTargetOffsetTop += offset;
    }

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    @Override
    public void reset() {
        mCurrentTargetOffsetTop = 0;
    }

    /**
     * 获取到View的初始状态值，一般为高度 或者Top值
     *
     * @return
     */
    @Override
    public int getOriginalValue() {
        return mOriginalOffsetTop;
    }

    /**
     * 设置View的初始状态值，一般为高度 或者Top值
     *
     * @param mOriginalOffsetTop
     */
    @Override
    public void setOriginalValue(int mOriginalOffsetTop) {
        this.mOriginalOffsetTop = mOriginalOffsetTop;
    }

    /**
     * @return 允许被拖拽的最大距离
     */
    @Override
    public int getTotalDragDistance() {
        return totalDragDistance;
    }

    /**
     * 设置允许被拖拽的最大距离
     *
     * @param totalDragDistance
     */
    @Override
    public void setTotalDragDistance(int totalDragDistance) {
        this.totalDragDistance = totalDragDistance;
    }

    /**
     * @return View当前的状态值，一般为高度或者Top值
     */
    @Override
    public int getCurrentValue() {
        return mCurrentTargetOffsetTop;
    }


    /**
     * 根据传入手指滑动总距离进行处理，返回合适的值
     *
     * @param overscrollTop 手指滑动的总距离
     */
    @Override
    public int moveSpinner(float overscrollTop) {

        //拖拽距离到最大距离的百分比
        float originalDragPercent = overscrollTop / getTotalDragDistance();
        //确定百分比
        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - getTotalDragDistance();
        //弹性距离
        float slingshotDist = getTotalDragDistance();
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;

        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = (int) ((slingshotDist * dragPercent) + extraMove);
        return targetY;
    }

    @Override
    public void layoutChild(int parentWidth, int parentHeight) {

    }

    /**
     * 获取到头部类型
     *
     * @return 返回值为刷新控件类型
     */
    @Override
    public EnumCollections.HeadStyle getHeadStyle() {
        return null;
    }

    /**
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    @Override
    public int getMinValueToScrollList() {
        return 0;
    }

    /**
     * 用来设置布局为头布局还是脚布局，UP ，Down
     *
     * @param loaction
     */
    @Override
    public void setLoaction(EnumCollections.Loaction loaction) {

    }

    /**
     * 获取当前布局为头布局还是脚布局
     *
     * @return 默认为头布局
     */
    @Override
    public EnumCollections.Loaction getLoaction() {
        return null;
    }


}
