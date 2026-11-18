package com.cynorra.kpssarena.presentation.arena;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cynorra.kpssarena.data.local.ExamResultStore;
import com.cynorra.kpssarena.databinding.BottomSheetCreateExamDuelBinding;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateExamDuelBottomSheet extends BottomSheetDialogFragment {

    public interface OnDuelCreatedListener {
        void onChallengeCreated(ExamType type, ScoreResult result);
        void onStartExam(ExamType type);
    }

    @Inject
    ExamResultStore examResultStore;

    private BottomSheetCreateExamDuelBinding binding;
    private OnDuelCreatedListener listener;

    public static CreateExamDuelBottomSheet newInstance(OnDuelCreatedListener listener) {
        CreateExamDuelBottomSheet sheet = new CreateExamDuelBottomSheet();
        sheet.listener = listener;
        return sheet;
    }

    public void setListener(OnDuelCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCreateExamDuelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        Map<ExamType, ScoreResult> userScores = examResultStore.getAllResults(uid);

        ExamDuelSelectAdapter adapter = new ExamDuelSelectAdapter(
                Arrays.asList(ExamType.values()),
                userScores,
                new ExamDuelSelectAdapter.OnExamOptionClickListener() {
                    @Override
                    public void onChallengeWithScore(ExamType type, ScoreResult result) {
                        dismiss();
                        if (listener != null) {
                            listener.onChallengeCreated(type, result);
                        }
                    }

                    @Override
                    public void onSolveExamFirst(ExamType type) {
                        dismiss();
                        if (listener != null) {
                            listener.onStartExam(type);
                        }
                    }
                }
        );

        binding.rvExamOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExamOptions.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
