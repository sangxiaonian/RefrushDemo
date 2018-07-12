package sang.com.refrushdemo.refrush.helper.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

import sang.com.refrushdemo.refrush.helper.animation.inter.AnimationCollection;

/**
 * 作者： ${PING} on 2018/7/12.
 */

public class AnimationRefrush implements AnimationCollection.IAnimationHelper {

    private DecelerateInterpolator mDecelerateInterpolator;
    ValueAnimator animator;
    public AnimationCollection.IAnimationListener mListener;


    public AnimationRefrush() {
        animator = ValueAnimator.ofInt();
        mDecelerateInterpolator = new DecelerateInterpolator(2f);
        animator
                .setInterpolator(mDecelerateInterpolator);
        animator
                .setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mListener != null) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    float animatedFraction = animation.getAnimatedFraction();
                    mListener.animationUpdate(animatedFraction, animatedValue);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mListener != null) {
                    mListener.animationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mListener != null) {
                    mListener.animationEnd();
                }
            }
        })
        ;
    }

    /**
     * 移动到初始位置的动画，取消或者刷新完成后执行
     */
    @Override
    public void animationToStart(int... value) {
        animator.resume();
        animator.cancel();
        animator.setIntValues(value);
        animator.start();
    }

    /**
     * 移动到刷新位置的动画
     *
     * @param value 动画经过的路径和初始值
     */
    @Override
    public void animationToRefrush(int... value) {
        animator.resume();
        animator.cancel();
        animator.setIntValues(value);
        animator.start();
    }

    /**
     * 设置动画监听
     *
     * @param listener
     */
    @Override
    public void setAnimationListener(AnimationCollection.IAnimationListener listener) {
        mListener = listener;
    }
}
