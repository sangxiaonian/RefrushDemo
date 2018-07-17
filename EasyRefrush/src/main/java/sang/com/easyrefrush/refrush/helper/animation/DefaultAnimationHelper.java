package sang.com.easyrefrush.refrush.helper.animation;

import sang.com.easyrefrush.refrush.helper.animation.inter.AnimationCollection;

/**
 * 作者： ${PING} on 2018/7/17.
 */

public class DefaultAnimationHelper implements AnimationCollection.IAnimationHelper {
    /**
     * 移动到初始位置的动画，取消或者刷新完成后执行
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToStart(int... value) {

    }

    /**
     * 移动到刷新位置的动画
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToRefrush(int... value) {

    }

    /**
     * 设置动画监听
     *
     * @param listener
     */
    @Override
    public void setAnimationListener(AnimationCollection.IAnimationListener listener) {

    }
}
