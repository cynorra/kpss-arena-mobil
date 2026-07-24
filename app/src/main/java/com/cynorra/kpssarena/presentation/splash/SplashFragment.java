package com.cynorra.kpssarena.presentation.splash;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.airbnb.lottie.LottieDrawable;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentSplashBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class SplashFragment extends BaseFragment<FragmentSplashBinding, SplashViewModel> {
    @Override
    protected FragmentSplashBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSplashBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<SplashViewModel> getViewModelClass() {
        return SplashViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.lottieSplash.setAnimation(R.raw.splash_animation);
        binding.lottieSplash.setRepeatMode(LottieDrawable.RESTART);
        binding.lottieSplash.setRepeatCount(LottieDrawable.INFINITE);
        binding.lottieSplash.playAnimation();
    }
    @Override
    protected void observeData() {
        viewModel.getNavigationDestination().observe(getViewLifecycleOwner(), destination -> {
            if (destination != null) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    NavDirections action;
                    switch (destination) {
                        case ONBOARDING:
                            action = SplashFragmentDirections.actionSplashToOnboarding();
                            break;
                        case LOGIN:
                            action = SplashFragmentDirections.actionSplashToLogin();
                            break;
                        case HOME:
                        default:
                            action = SplashFragmentDirections.actionSplashToHome();
                            break;
                    }
                    Navigation.findNavController(requireView()).navigate(action);
                }, 2000);
            }
        });
    }
}
