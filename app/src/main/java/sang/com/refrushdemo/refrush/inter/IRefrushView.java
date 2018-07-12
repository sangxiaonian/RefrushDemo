package sang.com.refrushdemo.refrush.inter;

/**
 * 作者： ${PING} on 2018/7/11.
 * 刷新控件
 */

public interface IRefrushView {

    /**
     * 根据传入的值，更改此时view的状态
     * @param offset 此次操作造成的该变量
     */
    void changValue(  float offset);

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    void reset();

    /**
     * 获取到View的初始状态值，一般为高度 或者Top值
     * @return
     */
    int getOriginalValue();

    /**
     * 设置View的初始状态值，一般为高度 或者Top值
     * @param mOriginalOffsetTop
     */
    void setOriginalValue(int mOriginalOffsetTop);

    /**
     *
     * @return 允许被拖拽的最大距离
     */
    int getTotalDragDistance();

    /**
     * 设置允许被拖拽的最大距离
     * @param totalDragDistance
     */
    void setTotalDragDistance(int totalDragDistance);

    /**
     *
     * @return View当前的状态值，一般为高度或者Top值
     */
    int getCurrentValue();

    /**
     * 手指滑动时候的处理
     * @param overscrollTop 手指滑动的总距离
     */
    int moveSpinner(float overscrollTop);

    void layoutChild(int parentWidth, int parentHeight);
}
