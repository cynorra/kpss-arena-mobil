package com.cynorra.kpssarena.presentation.common;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import timber.log.Timber;

public final class AdManager {

    public static final String BANNER_AD_UNIT_ID = "ca-app-pub-4674211063760769/2688135839";
    public static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4674211063760769/9158321208";
    // Standard test rewarded ad unit; replaced with production ID when configured
    public static final String REWARDED_AD_UNIT_ID = "ca-app-pub-4674211063760769/8916228065";

    private static long lastInterstitialShownTime = 0;
    private static final long INTERSTITIAL_COOLDOWN_MS = 45 * 1000L; // 45s cooldown

    public interface OnRewardEarnedListener {
        void onRewardEarned();
    }

    private AdManager() {
    }

    /**
     * Loads an Anchored Adaptive Banner into the given container.
     */
    public static AdView loadAdaptiveBanner(Activity activity, ViewGroup container) {
        if (activity == null || container == null)
            return null;

        try {
            AdView adView = new AdView(activity);
            adView.setAdUnitId(BANNER_AD_UNIT_ID);

            Runnable attachBanner = () -> {
                if (activity.isFinishing() || activity.isDestroyed())
                    return;
                DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                int containerWidth = container.getWidth();
                if (containerWidth <= 0) {
                    containerWidth = displayMetrics.widthPixels;
                }
                int adWidth = (int) (containerWidth / displayMetrics.density);
                if (adWidth <= 0)
                    adWidth = 320;

                AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
                adView.setAdSize(adSize);

                container.removeAllViews();
                container.addView(adView);

                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        container.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Timber.w("Banner failed to load: %s", loadAdError.getMessage());
                    }
                });

                adView.loadAd(new AdRequest.Builder().build());
            };

            if (container.getWidth() > 0) {
                attachBanner.run();
            } else {
                container.post(attachBanner);
            }

            return adView;
        } catch (Exception e) {
            Timber.e(e, "Error loading adaptive banner");
            return null;
        }
    }

    /**
     * Shows an interstitial ad with frequency capping.
     */
    public static void showInterstitial(Activity activity, Runnable onDismissed) {
        if (activity == null) {
            if (onDismissed != null)
                onDismissed.run();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastInterstitialShownTime < INTERSTITIAL_COOLDOWN_MS) {
            if (onDismissed != null)
                onDismissed.run();
            return;
        }

        InterstitialAd.load(activity, INTERSTITIAL_AD_UNIT_ID,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                lastInterstitialShownTime = System.currentTimeMillis();
                                if (onDismissed != null)
                                    onDismissed.run();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(
                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                Timber.w("Interstitial failed to show: %s", adError.getMessage());
                                if (onDismissed != null)
                                    onDismissed.run();
                            }
                        });
                        interstitialAd.show(activity);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Timber.w("Interstitial failed to load: %s", loadAdError.getMessage());
                        if (onDismissed != null)
                            onDismissed.run();
                    }
                });
    }

    /**
     * Loads and shows a Rewarded Ad. When completed, invokes onRewardEarned.
     */
    public static void showRewardedAd(Activity activity, OnRewardEarnedListener listener) {
        if (activity == null)
            return;

        RewardedAd.load(activity, REWARDED_AD_UNIT_ID,
                new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(
                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                Timber.w("Rewarded ad failed to show: %s", adError.getMessage());
                            }
                        });

                        rewardedAd.show(activity, rewardItem -> {
                            Timber.d("User earned reward");
                            if (listener != null) {
                                listener.onRewardEarned();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Timber.w("Rewarded ad failed to load: %s", loadAdError.getMessage());
                        // Fallback: If ad network fails to fill rewarded ad, grant reward anyway so
                        // candidate is not penalized
                        if (listener != null) {
                            listener.onRewardEarned();
                        }
                    }
                });
    }
}
