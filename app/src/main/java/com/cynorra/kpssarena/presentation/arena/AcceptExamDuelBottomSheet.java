package com.cynorra.kpssarena.presentation.arena;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.data.local.ExamResultStore;
import com.cynorra.kpssarena.databinding.BottomSheetAcceptExamDuelBinding;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AcceptExamDuelBottomSheet extends BottomSheetDialogFragment {

    public interface OnDuelAcceptedListener {
        void onDuelAccepted(Match match, ScoreResult myResult);
        void onStartRequiredExam(ExamType type);
    }

    @Inject
    ExamResultStore examResultStore;

    private BottomSheetAcceptExamDuelBinding binding;
    private Match match;
    private OnDuelAcceptedListener listener;

    public static AcceptExamDuelBottomSheet newInstance(Match match, OnDuelAcceptedListener listener) {
        AcceptExamDuelBottomSheet sheet = new AcceptExamDuelBottomSheet();
        sheet.match = match;
        sheet.listener = listener;
        return sheet;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setListener(OnDuelAcceptedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAcceptExamDuelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (match == null) {
            dismiss();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        ExamType type = ExamType.fromString(match.getExamType());
        ScoreResult myResult = examResultStore.getResult(uid, type);

        String creator = match.getPlayer1Username() != null ? match.getPlayer1Username() : "Aday";
        String examName = match.getExamTitle() != null ? match.getExamTitle() : type.getTitle();

        binding.tvDuelPrompt.setText(getString(R.string.dialog_accept_duel_prompt, creator, examName));
        binding.tvExamName.setText(examName + " (" + type.getTotalQuestions() + " Soru)");

        if (myResult != null) {
            int minutes = Math.max(1, (myResult.getTotalTimeUsed() / 1000) / 60);
            binding.tvMyScoreDesc.setText(getString(R.string.duel_score_summary,
                    (double) myResult.getNetScore(),
                    myResult.getCorrectCount(),
                    myResult.getWrongCount(),
                    minutes));
            binding.tvMissingScoreWarning.setVisibility(View.GONE);
            binding.btnAction.setText(getString(R.string.action_submit_duel_score));
            binding.btnAction.setOnClickListener(v -> {
                dismiss();
                if (listener != null) {
                    listener.onDuelAccepted(match, myResult);
                }
            });
        } else {
            binding.tvMyScoreDesc.setVisibility(View.GONE);
            binding.tvMyScoreHeader.setVisibility(View.GONE);
            binding.tvMissingScoreWarning.setVisibility(View.VISIBLE);
            binding.tvMissingScoreWarning.setText(getString(R.string.dialog_accept_duel_no_score_warning, examName));
            binding.btnAction.setText(getString(R.string.action_solve_first) + " (" + examName + ")");
            binding.btnAction.setOnClickListener(v -> {
                dismiss();
                if (listener != null) {
                    listener.onStartRequiredExam(type);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
