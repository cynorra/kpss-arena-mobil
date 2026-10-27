package com.cynorra.kpssarena.presentation.profile;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentProfileBinding;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class ProfileFragment extends BaseFragment<FragmentProfileBinding, ProfileViewModel> {
    @Override
    protected FragmentProfileBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<ProfileViewModel> getViewModelClass() {
        return ProfileViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.btnSettings.setOnClickListener(v -> {
            NavDirections action = ProfileFragmentDirections.actionProfileToSettings();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnStats.setOnClickListener(v -> {
            NavDirections action = ProfileFragmentDirections.actionProfileToStats();

            Navigation.findNavController(v).navigate(action);
        });
        binding.btnEditProfile.setOnClickListener(v -> showEditUsernameDialog());
        binding.btnLogout.setOnClickListener(v -> viewModel.logout());
        // Hidden admin trigger: 5 taps on logo
        binding.ivAvatar.setOnClickListener(new View.OnClickListener() {
            private int tapCount = 0;
            private long lastTapTime = 0;
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTapTime > 2000) {
                    tapCount = 0;
                }
                tapCount++;
                lastTapTime = currentTime;
                if (tapCount >= 5) {
                    tapCount = 0;
                    viewModel.checkAdminAccess();
                }
            }
        });
    }
    private void showEditUsernameDialog() {
        User current = viewModel.getUser().getValue();
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setSingleLine(true);
        input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(20)});
        if (current != null && current.getUsername() != null) {
            input.setText(current.getUsername());
            input.setSelection(input.getText().length());
        }
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        container.setPadding(pad, pad / 2, pad, 0);
        container.addView(input);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_edit_profile_title)
                .setView(container)
                .setPositiveButton(R.string.action_save, (d, w) -> viewModel.updateUsername(input.getText().toString()))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
    @Override
    protected void observeData() {
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.consumeMessages();
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.consumeMessages();
            }
        });
        viewModel.getUser().observe(getViewLifecycleOwner(), this::displayUserProfile);
        
        viewModel.getNavigateToAdmin().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                NavDirections action = ProfileFragmentDirections.actionProfileToAdmin();
                Navigation.findNavController(requireView()).navigate(action);

                viewModel.onAdminNavigationDone();
            }
        });
        viewModel.getLogoutEvent().observe(getViewLifecycleOwner(), shouldLogout -> {
            if (shouldLogout != null && shouldLogout) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.loginFragment);
            }
        });
    }
    private void displayUserProfile(User user) {
        if (user == null) return;
        binding.tvUsername.setText(user.getUsername());
        binding.tvEmail.setText(user.getEmail());
        binding.tvTotalScore.setText(String.valueOf(user.getTotalScore()));
        binding.tvStreak.setText(getResources().getQuantityString(R.plurals.streak_days, user.getStreak(), user.getStreak()));
        binding.tvBadgeCount.setText(String.valueOf(user.getBadges() != null ? user.getBadges().size() : 0));
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(binding.ivAvatar);
        }
    }
}
