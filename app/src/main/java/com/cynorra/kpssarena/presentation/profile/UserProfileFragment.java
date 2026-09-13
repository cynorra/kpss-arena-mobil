package com.cynorra.kpssarena.presentation.profile;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentUserProfileBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserProfileFragment extends BaseFragment<FragmentUserProfileBinding, ProfileViewModel> {
    private String userId;
    @Override
    protected FragmentUserProfileBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentUserProfileBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<ProfileViewModel> getViewModelClass() {
        return ProfileViewModel.class;
    }
    @Override
    protected void initViews() {
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            viewModel.loadOtherUserProfile(userId);
        }
        binding.btnFollow.setOnClickListener(v -> {
            if (userId != null) {
                viewModel.toggleFollow(userId);
            }
        });
        binding.btnChallenge.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.toast_challenge_coming_soon, Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void observeData() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvTotalScore.setText(String.valueOf(user.getTotalScore()));

                binding.tvStreak.setText(getString(R.string.streak_days_format, user.getStreak()));
                binding.tvBadgeCount.setText(getString(R.string.badge_count_format, user.getBadges().size()));
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .circleCrop()
                            .into(binding.ivAvatar);
                }
                boolean isFollowing = user.getFollowers() != null &&
                        user.getFollowers().contains(com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid());
                binding.btnFollow.setText(isFollowing ? R.string.action_unfollow : R.string.action_follow);
            }
        });
    }
}
