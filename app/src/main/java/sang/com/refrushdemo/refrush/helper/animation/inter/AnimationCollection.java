package sang.com.refrushdemo.refrush.helper.animation.inter;

/**
 * 作者： ${PING} on 2018/7/12.
 */

public class AnimationCollection {

    /**
     * 动画的帮助类
     */
    public interface IAnimationHelper {

        /**
         * 移动到初始位置的动画，取消或者刷新完成后执行
         *
         * @param value 动画经过的路径和初始值
         */
        void animationToStart(int... value);

        /**
         * 移动到刷新位置的动画
         * @param value 动画经过的路径和初始值
         */
        void animationToRefrush(int... value);

        /**
         * 设置动画监听
         * @param listener
         */
        void setAnimationListener(IAnimationListener listener);

    }

    /**
     * 作者： ${PING} on 2018/7/10.
     * 动画执行的监听接口
     */

    public interface IAnimationListener {

        /**
         * 动画开始
         */
        void animationStart();

        /**
         * 动画结束
         */
        void animationEnd();

        /**
         *
         * @param animatedFraction 动画执行比例 0-1
         * @param animatedValue    动画执行当前值
         */
        void animationUpdate(float animatedFraction, float animatedValue);
    }

}
