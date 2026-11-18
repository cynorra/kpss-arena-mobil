package com.cynorra.kpssarena.presentation.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lightweight, zero-dependency, hardware-accelerated confetti celebration particle system.
 */
public class ConfettiHelper {

    private static final int PARTICLE_COUNT = 75;
    private static final int ANIMATION_DURATION_MS = 2800;

    private static final int[] CONFETTI_COLORS = {
            Color.parseColor("#FFD700"), // Gold
            Color.parseColor("#C9A84C"), // Brand Gold
            Color.parseColor("#4CAF50"), // Emerald Green
            Color.parseColor("#64B5F6"), // Sky Blue
            Color.parseColor("#FF7043"), // Coral
            Color.parseColor("#BA68C8"), // Lavender
            Color.parseColor("#FFFFFF")  // Pure White sparkle
    };

    public static void celebrate(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        celebrate(decor);
    }

    public static void celebrate(ViewGroup container) {
        if (container == null) return;
        Context context = container.getContext();
        ConfettiView confettiView = new ConfettiView(context);
        container.addView(confettiView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        confettiView.startAnimation(() -> container.removeView(confettiView));
    }

    private static class ConfettiView extends View {
        private final List<Particle> particles = new ArrayList<>();
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();
        private ValueAnimator animator;

        public ConfettiView(Context context) {
            super(context);
            setClickable(false);
            setFocusable(false);
        }

        private void initParticles(int width, int height) {
            particles.clear();
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                Particle p = new Particle();
                p.x = random.nextFloat() * width;
                p.y = -random.nextFloat() * (height * 0.4f) - 20f;
                p.color = CONFETTI_COLORS[random.nextInt(CONFETTI_COLORS.length)];
                p.size = 12f + random.nextFloat() * 14f;
                p.speedY = 4f + random.nextFloat() * 7f;
                p.speedX = (random.nextFloat() - 0.5f) * 3.5f;
                p.rotation = random.nextFloat() * 360f;
                p.rotationSpeed = (random.nextFloat() - 0.5f) * 12f;
                p.isCircle = random.nextBoolean();
                p.wobblePhase = random.nextFloat() * 6.28f;
                p.wobbleSpeed = 0.05f + random.nextFloat() * 0.08f;
                particles.add(p);
            }
        }

        public void startAnimation(Runnable onEnd) {
            post(() -> {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) {
                    w = getResources().getDisplayMetrics().widthPixels;
                    h = getResources().getDisplayMetrics().heightPixels;
                }
                initParticles(w, h);

                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(ANIMATION_DURATION_MS);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(animation -> {
                    float fraction = animation.getAnimatedFraction();
                    for (Particle p : particles) {
                        p.y += p.speedY;
                        p.wobblePhase += p.wobbleSpeed;
                        p.x += p.speedX + (float) Math.sin(p.wobblePhase) * 2.2f;
                        p.rotation += p.rotationSpeed;
                        if (fraction > 0.7f) {
                            p.alpha = Math.max(0, (int) (255 * (1f - (fraction - 0.7f) / 0.3f)));
                        } else {
                            p.alpha = 255;
                        }
                    }
                    invalidate();
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onEnd != null) onEnd.run();
                    }
                });
                animator.start();
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (Particle p : particles) {
                paint.setColor(p.color);
                paint.setAlpha(p.alpha);
                canvas.save();
                canvas.translate(p.x, p.y);
                canvas.rotate(p.rotation);
                if (p.isCircle) {
                    canvas.drawCircle(0, 0, p.size / 2f, paint);
                } else {
                    RectF rect = new RectF(-p.size / 2f, -p.size / 3f, p.size / 2f, p.size / 3f);
                    canvas.drawRoundRect(rect, 4f, 4f, paint);
                }
                canvas.restore();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    private static class Particle {
        float x, y;
        int color;
        float size;
        float speedX, speedY;
        float rotation, rotationSpeed;
        boolean isCircle;
        float wobblePhase, wobbleSpeed;
        int alpha = 255;
    }
}
