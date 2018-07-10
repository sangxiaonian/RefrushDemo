package sang.com.refrushdemo.inter;

/**
 * 作者： ${PING} on 2018/7/10.
 */

public interface IAnimationListener {

    /**
     * 动画开始
     */
    void onAnimationStart();

    void onAnimationEnd();

    /**
     *
     * @param animatedFraction 动画执行比例 0-1
     * @param animatedValue    动画执行当前值
     */
    void onAnimationUpdate(float animatedFraction, float animatedValue);
}
