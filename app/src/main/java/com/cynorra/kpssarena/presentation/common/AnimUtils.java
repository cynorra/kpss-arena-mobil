package com.cynorra.kpssarena.presentation.common;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
public class AnimUtils {
    public static void scalePressEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:

                    v.animate()
                            .scaleX(0.96f)
                            .scaleY(0.96f)
                            .setDuration(100)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                    break;
            }
            return false;
        });
    }
    public static void fadeIn(View view, int duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    public static void fadeOut(View view, int duration) {
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    public static void slideInFromRight(View view, int duration) {
        view.setTranslationX(view.getWidth());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationX(0f)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    public static void slideInFromBottom(View view, int duration, int delay) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    public static void springAnimation(View view, float startScale, float endScale) {
        view.setScaleX(startScale);
        view.setScaleY(startScale);
        view.animate()
                .scaleX(endScale)
                .scaleY(endScale)
                .setDuration(400)
                .setInterpolator(new AnticipateOvershootInterpolator(1.5f))
                .start();
    }
    public static void highlightPulse(View view, int duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f);
        
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(duration);
        set.setInterpolator(new DecelerateInterpolator());

        set.start();
    }
    public static void staggeredRecyclerItemAnim(View view, int position, int delayPerItem) {
        view.setAlpha(0f);
        view.setTranslationY(50f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(position * delayPerItem)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    public static void countUpAnimation(ValueAnimator.AnimatorUpdateListener updateListener,
                                        int start, int end, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(updateListener);
        animator.start();
    }
}
