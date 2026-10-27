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
        binding.btnChallenge.setOnClickListener(v -> {
            com.cynorra.kpssarena.domain.model.User target = viewModel.getUser().getValue();
            if (target != null) {
                viewModel.challengeUser(target.getUsername());
            }
        });
        binding.btnFollow.setOnClickListener(v -> {
            if (userId != null) {
                viewModel.toggleFollow(userId);
            }
        });
    }
    @Override
    protected void observeData() {
        viewModel.getChallengeMatchId().observe(getViewLifecycleOwner(), matchId -> {
            if (matchId != null) {
                Bundle args = new Bundle();
                args.putString("matchId", matchId);
                androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.matchWaitingFragment, args);
                viewModel.onChallengeHandled();
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.consumeMessages();
            }
        });
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvTotalScore.setText(String.valueOf(user.getTotalScore()));

                binding.tvStreak.setText(getResources().getQuantityString(R.plurals.streak_days, user.getStreak(), user.getStreak()));
                binding.tvBadgeCount.setText(String.valueOf(user.getBadges() != null ? user.getBadges().size() : 0));
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .circleCrop()
                            .into(binding.ivAvatar);
                }
                com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                boolean isFollowing = user.getFollowers() != null && currentUser != null &&
                        user.getFollowers().contains(currentUser.getUid());
                binding.btnFollow.setText(isFollowing ? R.string.action_unfollow : R.string.action_follow);
                boolean isSelf = currentUser != null && currentUser.getUid().equals(userId);
                binding.btnChallenge.setVisibility(isSelf ? View.GONE : View.VISIBLE);
                binding.btnFollow.setVisibility(isSelf ? View.GONE : View.VISIBLE);
            }
        });
    }
}
