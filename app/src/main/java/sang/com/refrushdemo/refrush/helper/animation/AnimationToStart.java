package sang.com.refrushdemo.refrush.helper.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;

import sang.com.refrushdemo.refrush.helper.animation.inter.AnimationCollection;

/**
 * 作者： ${PING} on 2018/7/10.
 */

public class AnimationToStart {

    private ValueAnimator animator = ValueAnimator.ofInt();

    AnimationCollection.IAnimationListener mListener;





    public AnimationToStart addIntValues(int... values) {
        animator.setIntValues(values);
        return this;
    }

    public AnimationToStart addInterpolator(TimeInterpolator value) {
        animator.setInterpolator(value);
        return this;
    }

    public AnimationToStart reset() {
        animator.resume();
        animator.cancel();
        return this;
    }

    public AnimationToStart addDuration(long duration) {
        animator.setDuration(duration);
        return this;
    }



    public AnimationToStart addListener(final AnimationCollection.IAnimationListener listener) {

        mListener=listener;

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener!=null){
                    mListener.animationEnd();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (mListener!=null){
                    mListener.animationStart();
                }
            }

        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mListener!=null){
                    int animatedValue = (int) animation.getAnimatedValue();
                    float animatedFraction = animation.getAnimatedFraction();
                    mListener.animationUpdate(animatedFraction,animatedValue);
                }
            }
        });
        return this;
    }
    public void start( ) {
        animator.start();
    }

}
