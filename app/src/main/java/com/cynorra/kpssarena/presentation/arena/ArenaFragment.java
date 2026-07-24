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
        binding.btnChallenge.setOnClickListener(v -> showChallengeDialog());
        binding.btnRandomMatch.setOnClickListener(v -> viewModel.findRandomMatch());
    }
    private void showChallengeDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_challenge, null);
        TextInputEditText etUsername = dialogView.findViewById(R.id.et_username);

        builder.setTitle("Rakibe Meydan Oku")
                .setView(dialogView)
                .setPositiveButton("Meydan Oku", (dialog, which) -> {
                    String username = etUsername.getText() != null ?
                            etUsername.getText().toString().trim() : "";
                    if (!username.isEmpty()) {
                        viewModel.challengeUser(username);
                    }
                })
                .setNegativeButton("Vazgeç", null)
                .show();
    }
    @Override
    protected void observeData() {
        viewModel.getActiveMatches().observe(getViewLifecycleOwner(), matches -> {
            // Update active matches RecyclerView
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
