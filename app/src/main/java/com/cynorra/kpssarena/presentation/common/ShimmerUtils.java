package com.cynorra.kpssarena.presentation.common;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
public class ShimmerUtils {
    private static final Shimmer DEFAULT_SHIMMER = new Shimmer.AlphaHighlightBuilde
            .setDuration(1000)
            .setBaseAlpha(0.6f)
            .setHighlightAlpha(0.8f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .build();
    public static void showShimmer(View... views) {
        for (View view : views) {

            if (view == null) continue;
            
            if (view instanceof ShimmerFrameLayout) {
                ShimmerFrameLayout shimmerLayout = (ShimmerFrameLayout) view;
                shimmerLayout.setShimmer(DEFAULT_SHIMMER);
                shimmerLayout.startShimmer();
                shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }
    public static void hideShimmer(View... views) {
        for (View view : views) {
            if (view == null) continue;
            
            if (view instanceof ShimmerFrameLayout) {
                ShimmerFrameLayout shimmerLayout = (ShimmerFrameLayout) view;
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
            }
        }
    }
    public static ShimmerFrameLayout createShimmerPlaceholder(ViewGroup parent, int
        ShimmerFrameLayout shimmerLayout = new ShimmerFrameLayout(parent.getCon
        shimmerLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        shimmerLayout.setShimmer(DEFAULT_SHIMMER);
        
        View placeholder = android.view.LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, shimmerLayout, false);
        shimmerLayout.addView(placeholder);
        
        return shimmerLayout;
    }
}
