package sang.com.easyrefrush.refrush.inter;


import sang.com.easyrefrush.refrush.EnumCollections;

/**
 * 作者： ${PING} on 2018/7/11.
 * 刷新控件
 */

public interface IRefrushView {

    /**
     * 根据传入的值，更改此时view的状态
     *
     * @param offset 此次操作造成的该变量
     */
    void changValue(float offset);

    /**
     * 取消刷新，刷新成功等操作完成之后，恢复到初始状态
     */
    void reset();

    /**
     * 获取到View的初始状态值，一般为高度 或者Top值
     *
     * @return
     */
    int getOriginalValue();

    /**
     * 设置View的初始状态值，一般为高度 或者Top值
     *
     * @param mOriginalOffsetTop
     */
    void setOriginalValue(int mOriginalOffsetTop);

    /**
     * @return 允许被拖拽的最大距离
     */
    int getTotalDragDistance();

    /**
     * 设置允许被拖拽的最大距离
     *
     * @param totalDragDistance
     */
    void setTotalDragDistance(int totalDragDistance);

    /**
     * @return View当前的状态值，一般为高度或者Top值
     */
    int getCurrentValue();


    /**
     * 手指滑动时候的处理，触发MOVE事件
     *
     * @param overscrollTop 手指滑动的总距离
     */
    int moveSpinner(float overscrollTop);

    /**
     * 手指离开屏幕
     *
     * @param overscrollTop
     */
    void onFinishSpinner(float overscrollTop);


    /**
     * 对对应的布局进行布置
     *
     * @param parentWidth
     * @param parentHeight
     */
    void layoutChild(int parentWidth, int parentHeight);

    /**
     * 获取到头部类型
     *
     * @return 返回值为刷新控件类型
     */
    EnumCollections.HeadStyle getHeadStyle();


    /**
     * 获取停止滑动头部，将滑动数据交个其他控件的最小值
     *
     * @return
     */
    int getMinValueToScrollList();

    /**
     * 用来设置布局为头布局还是脚布局，UP ，Down
     *
     * @param loaction
     */
    void setLoaction(EnumCollections.Loaction loaction);

    /**
     * 获取当前布局为头布局还是脚布局
     *
     * @return 默认为头布局
     */
    EnumCollections.Loaction getLoaction();

}
