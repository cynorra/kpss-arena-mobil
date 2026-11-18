package com.cynorra.kpssarena.presentation.arena;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentArenaBinding;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ArenaFragment extends BaseFragment<FragmentArenaBinding, ArenaViewModel> {

    private final ActiveMatchAdapter activeAdapter = new ActiveMatchAdapter(this::onActiveMatchClicked);
    private final OpenChallengeAdapter openAdapter = new OpenChallengeAdapter(this::onOpenChallengeClicked);

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
        binding.rvActiveMatches.setAdapter(activeAdapter);

        binding.rvOpenChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOpenChallenges.setAdapter(openAdapter);

        binding.btnChallenge.setOnClickListener(v -> showChallengeDialog());
        binding.btnRandomMatch.setOnClickListener(v -> viewModel.findRandomMatch());
        binding.btnCreateExamDuel.setOnClickListener(v -> showCreateExamDuelSheet());
        binding.cardMatchHistory.setOnClickListener(v -> viewModel.loadMatchHistory());

        viewModel.loadActiveMatches();
    }

    private void onActiveMatchClicked(Match match) {
        if ("ASYNC_EXAM".equals(match.getMatchType())) {
            if (match.getStatus() == Match.MatchStatus.COMPLETED) {
                new ExamDuelResultDialog(requireContext(), match).show();
            } else if (match.getStatus() == Match.MatchStatus.WAITING) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(match.getDisplayTitle(requireContext()))
                        .setMessage(match.getExamTitle() + "\nNet Skorun: "
                                + String.format("%.2f", match.getPlayer1Net())
                                + "\n\nRakip bekleniyor... Başka bir aday aynı denemeyi çözüp katıldığında sonuç hesaplanacaktır.")
                        .setPositiveButton(R.string.action_close, null)
                        .setNegativeButton("Meydan Okumayı İptal Et", (dialog, which) -> {
                            viewModel.cancelMatch(match.getMatchId());
                        })
                        .show();
            }
        } else {
            viewModel.resumeMatch(match);
        }
    }

    private void onOpenChallengeClicked(Match match) {
        if ("ASYNC_EXAM".equals(match.getMatchType())) {
            AcceptExamDuelBottomSheet sheet = AcceptExamDuelBottomSheet.newInstance(match, new AcceptExamDuelBottomSheet.OnDuelAcceptedListener() {
                @Override
                public void onDuelAccepted(Match targetMatch, ScoreResult myResult) {
                    viewModel.acceptExamDuel(targetMatch, myResult);
                }

                @Override
                public void onStartRequiredExam(ExamType type) {
                    Bundle args = new Bundle();
                    args.putString("examType", type.name());
                    Navigation.findNavController(requireView()).navigate(R.id.action_arena_to_mock_exam, args);
                }
            });
            sheet.show(getChildFragmentManager(), "AcceptExamDuelBottomSheet");
        } else {
            viewModel.findRandomMatch();
        }
    }

    private void showCreateExamDuelSheet() {
        CreateExamDuelBottomSheet sheet = CreateExamDuelBottomSheet.newInstance(new CreateExamDuelBottomSheet.OnDuelCreatedListener() {
            @Override
            public void onChallengeCreated(ExamType type, ScoreResult result) {
                viewModel.createExamDuel(type, result);
            }

            @Override
            public void onStartExam(ExamType type) {
                Bundle args = new Bundle();
                args.putString("examType", type.name());
                Navigation.findNavController(requireView()).navigate(R.id.action_arena_to_mock_exam, args);
            }
        });
        sheet.show(getChildFragmentManager(), "CreateExamDuelBottomSheet");
    }

    private void showMatchHistory(List<Match> matches) {
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
                Match m = matches.get(i);
                int result = m.getWinnerUid() == null ? R.string.match_history_draw
                        : m.getWinnerUid().equals(myUid) ? R.string.match_history_win : R.string.match_history_loss;
                String opponent = m.getOpponentUsername(myUid);
                if ("ASYNC_EXAM".equals(m.getMatchType())) {
                    String exam = m.getExamTitle() != null ? m.getExamTitle() : "Deneme";
                    lines[i] = exam + " · " + (opponent != null ? opponent : "?") + " · " + getString(result);
                } else {
                    lines[i] = getString(R.string.match_history_line, opponent != null ? opponent : "?",
                            getString(result), m.getMyScore(myUid), m.getOpponentScore(myUid));
                }
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
            activeAdapter.submitList(matches);
            boolean empty = matches == null || matches.isEmpty();
            binding.tvActiveMatchesEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvActiveMatches.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        viewModel.getOpenChallenges().observe(getViewLifecycleOwner(), challenges -> {
            openAdapter.submitList(challenges);
            boolean empty = challenges == null || challenges.isEmpty();
            binding.tvOpenChallengesEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvOpenChallenges.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        viewModel.getExamDuelCompleted().observe(getViewLifecycleOwner(), completedMatch -> {
            if (completedMatch != null) {
                new ExamDuelResultDialog(requireContext(), completedMatch).show();
                viewModel.onExamDuelShown();
            }
        });

        viewModel.getDuelFeedbackMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                viewModel.onFeedbackShown();
            }
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

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadActiveMatches();
    }
}
