package sang.com.refrushdemo.refrush.inter;

/**
 * 作者： ${PING} on 2018/7/11.
 * 刷新控件
 */

public interface IRefrushView {

    /**
     * 根据传入的值，更改此时view的状态
     * @param overscroll
     */
    void changValue(  float overscroll);

}
