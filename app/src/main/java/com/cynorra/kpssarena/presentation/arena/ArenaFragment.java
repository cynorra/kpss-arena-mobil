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
        binding.cardMatchHistory.setOnClickListener(v -> viewModel.loadMatchHistory());
        viewModel.loadActiveMatches();
    }
    private void showMatchHistory(java.util.List<com.cynorra.kpssarena.domain.model.Match> matches) {
        com.google.firebase.auth.FirebaseUser me = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String myUid = me != null ? me.getUid() : "";
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.arena_match_history_title)
                .setPositiveButton(R.string.action_close, null);
        if (matches.isEmpty()) {
            builder.setMessage(R.string.match_history_empty);
        } else {
            String[] lines = new String[matches.size()];
            for (int i = 0; i < lines.length; i++) {
                com.cynorra.kpssarena.domain.model.Match m = matches.get(i);
                int result = m.getWinnerUid() == null ? R.string.match_history_draw
                        : m.getWinnerUid().equals(myUid) ? R.string.match_history_win : R.string.match_history_loss;
                String opponent = m.getOpponentUsername(myUid);
                lines[i] = getString(R.string.match_history_line, opponent != null ? opponent : "?",
                        getString(result), m.getMyScore(myUid), m.getOpponentScore(myUid));
            }
            builder.setItems(lines, null);
        }
        builder.show();
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
        viewModel.getMatchHistory().observe(getViewLifecycleOwner(), matches -> {
            if (matches != null) {
                showMatchHistory(matches);
                viewModel.onMatchHistoryShown();
            }
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
