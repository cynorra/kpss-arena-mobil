package com.cynorra.kpssarena.presentation.common;
import android.app.Activity;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;

import android.view.animation.DecelerateInterpolator;
import androidx.core.view.WindowCompat;
public class TransitionUtils {
    public static void setupFragmentTransitions(Window window) {
        TransitionSet enterTransition = new TransitionSet();
        
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(300);
        slide.setInterpolator(new DecelerateInterpolator());
        slide.addTarget(android.R.id.content);
        
        Fade fade = new Fade(Fade.IN);
        fade.setDuration(200);
        fade.setInterpolator(new AccelerateDecelerateInterpolator());
        
        enterTransition.addTransition(slide);
        enterTransition.addTransition(fade);
        
        window.setEnterTransition(enterTransition);
        window.setReturnTransition(new Fade());
        window.setExitTransition(new Fade());
    }
    public static void setupSharedAxisTransition(Window window, int axis) {
        // Material Shared Axis transition
        TransitionSet transitionSet = new TransitionSet();
        
        Slide slide;
        if (axis == 0) {
            slide = new Slide(Gravity.START); // X axis
        } else if (axis == 1) {
            slide = new Slide(Gravity.TOP); // Y axis
        } else {
            slide = new Slide(Gravity.BOTTOM); // Z axis
        }
        
        slide.setDuration(300);

        slide.setInterpolator(new DecelerateInterpolator());
        transitionSet.addTransition(slide);
        
        window.setEnterTransition(transitionSet);
        window.setExitTransition(new Fade());
    }
    public static void setupExplodeTransition(Window window) {
        android.transition.Explode explode = new android.transition.Explode();
        explode.setDuration(400);
        explode.setInterpolator(new DecelerateInterpolator());
        window.setEnterTransition(explode);
    }
}
