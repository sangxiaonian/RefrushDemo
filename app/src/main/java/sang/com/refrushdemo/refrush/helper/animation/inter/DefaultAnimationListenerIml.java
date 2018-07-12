package sang.com.refrushdemo.refrush.helper.animation.inter;

/**
 * 作者： ${PING} on 2018/7/10.
 */

public abstract class DefaultAnimationListenerIml implements AnimationCollection.IAnimationListener {
    /**
     * 动画开始
     */
    @Override
    public void animationStart() {

    }

    @Override
    public void animationEnd() {

    }

    /**
     * @param animatedFraction 动画执行比例 0-1
     * @param animatedValue    动画执行当前值
     */
    @Override
    public abstract void animationUpdate(float animatedFraction, float animatedValue) ;
}
