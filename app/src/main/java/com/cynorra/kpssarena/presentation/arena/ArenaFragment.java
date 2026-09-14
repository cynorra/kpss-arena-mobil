package com.cynorra.kpssarena.presentation.arena;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentArenaBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class ArenaFragment extends BaseFragment<FragmentArenaBinding, ArenaViewModel> {
    private final ActiveMatchAdapter adapter = new ActiveMatchAdapter(match -> viewModel.resumeMatch(match));
    @Override
    protected FragmentArenaBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentArenaBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<ArenaViewModel> getViewModelClass() {
        return ArenaViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.rvActiveMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvActiveMatches.setAdapter(adapter);
        binding.btnChallenge.setOnClickListener(v -> showChallengeDialog());
        binding.btnRandomMatch.setOnClickListener(v -> viewModel.findRandomMatch());
        binding.cardMatchHistory.setOnClickListener(v ->
                Toast.makeText(getContext(), R.string.toast_match_history_coming_soon, Toast.LENGTH_SHORT).show());
        viewModel.loadActiveMatches();
    }
    private void showChallengeDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_challenge, null);
        TextInputEditText etUsername = dialogView.findViewById(R.id.et_username);

        builder.setTitle(R.string.dialog_challenge_title)
                .setView(dialogView)
                .setPositiveButton(R.string.action_send_challenge, (dialog, which) -> {
                    String username = etUsername.getText() != null ?
                            etUsername.getText().toString().trim() : "";
                    if (!username.isEmpty()) {
                        viewModel.challengeUser(username);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
    @Override
    protected void observeData() {
        viewModel.getActiveMatches().observe(getViewLifecycleOwner(), matches -> {
            adapter.submitList(matches);
            boolean empty = matches == null || matches.isEmpty();
            binding.tvActiveMatchesEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvActiveMatches.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
        viewModel.getNavigateToWaiting().observe(getViewLifecycleOwner(), matchId -> {
            if (matchId != null) {
                Bundle args = new Bundle();
                args.putString("matchId", matchId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.matchWaitingFragment, args);
                viewModel.onNavigated();
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
