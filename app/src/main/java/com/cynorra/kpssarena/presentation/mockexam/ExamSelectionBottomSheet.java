package com.cynorra.kpssarena.presentation.mockexam;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.data.local.ExamResultStore;
import com.cynorra.kpssarena.databinding.BottomSheetExamSelectionBinding;
import com.cynorra.kpssarena.domain.model.BranchExam;
import com.cynorra.kpssarena.domain.model.BranchExamCatalog;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.common.AnimUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExamSelectionBottomSheet extends BottomSheetDialogFragment {

    public interface OnExamSelectedListener {
        void onExamSelected(ExamType examType, int testNumber);
    }

    @Inject
    ExamResultStore examResultStore;

    private BottomSheetExamSelectionBinding binding;
    private OnExamSelectedListener listener;
    private BranchExamAdapter branchAdapter;
    private ExamType selectedBranch = ExamType.BRANCH_TURKCE;

    private static final ExamType[] BRANCH_TYPES = new ExamType[]{
            ExamType.BRANCH_TURKCE,
            ExamType.BRANCH_MATEMATIK,
            ExamType.BRANCH_TARIH,
            ExamType.BRANCH_COGRAFYA,
            ExamType.BRANCH_VATANDASLIK
    };

    public static ExamSelectionBottomSheet newInstance(OnExamSelectedListener listener) {
        ExamSelectionBottomSheet sheet = new ExamSelectionBottomSheet();
        sheet.listener = listener;
        return sheet;
    }

    public void setListener(OnExamSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetExamSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Flagship full exams
        binding.cardExamFull.setOnClickListener(v -> selectExam(ExamType.FULL_KPSS, 1));
        binding.cardExamGy.setOnClickListener(v -> selectExam(ExamType.GY, 1));
        binding.cardExamGk.setOnClickListener(v -> selectExam(ExamType.GK, 1));

        AnimUtils.scalePressEffect(binding.cardExamFull);
        AnimUtils.scalePressEffect(binding.cardExamGy);
        AnimUtils.scalePressEffect(binding.cardExamGk);

        // Branch exams adapter
        branchAdapter = new BranchExamAdapter(exam -> selectExam(exam.getExamType(), exam.getTestNumber()));
        binding.rvBranchExams.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBranchExams.setAdapter(branchAdapter);

        // Setup branch chips
        setupBranchChips();

        // Load default branch (Türkçe)
        loadBranchExams(selectedBranch);
    }

    private void setupBranchChips() {
        binding.chipGroupBranches.removeAllViews();

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] bgColors = new int[]{
                Color.parseColor("#1E3A8A"), // Dark blue when selected
                Color.parseColor("#111827")  // Dark surface when unselected
        };
        int[] textColors = new int[]{
                Color.parseColor("#60A5FA"), // Light blue text when selected
                Color.parseColor("#94A3B8")  // Muted slate when unselected
        };
        int[] strokeColors = new int[]{
                Color.parseColor("#3B82F6"),
                Color.parseColor("#1E293B")
        };

        for (int i = 0; i < BRANCH_TYPES.length; i++) {
            ExamType branchType = BRANCH_TYPES[i];
            Chip chip = new Chip(requireContext());
            chip.setText(getBranchShortTitle(branchType));
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setCheckedIconVisible(false);

            chip.setChipBackgroundColor(new ColorStateList(states, bgColors));
            chip.setTextColor(new ColorStateList(states, textColors));
            chip.setChipStrokeColor(new ColorStateList(states, strokeColors));
            chip.setChipStrokeWidth(2f);
            chip.setId(View.generateViewId());

            if (branchType == selectedBranch) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedBranch = branchType;
                    loadBranchExams(branchType);
                }
            });

            binding.chipGroupBranches.addView(chip);
        }
    }

    private String getBranchShortTitle(ExamType type) {
        switch (type) {
            case BRANCH_TURKCE:
                return getString(R.string.exam_branch_turkce);
            case BRANCH_MATEMATIK:
                return getString(R.string.exam_branch_matematik);
            case BRANCH_TARIH:
                return getString(R.string.exam_branch_tarih);
            case BRANCH_COGRAFYA:
                return getString(R.string.exam_branch_cografya);
            case BRANCH_VATANDASLIK:
                return getString(R.string.exam_branch_vatandaslik);
            default:
                return type.getTitle();
        }
    }

    private void loadBranchExams(ExamType branchType) {
        if (binding == null) return;

        List<BranchExam> exams = BranchExamCatalog.getExamsForType(branchType);
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        int completedCount = 0;
        if (examResultStore != null && !uid.isEmpty()) {
            for (BranchExam exam : exams) {
                ScoreResult res = examResultStore.getResult(uid, branchType, exam.getTestNumber());
                if (res != null) {
                    exam.setLastResult(res);
                    completedCount++;
                }
            }
        }

        binding.tvBranchSummary.setText(getString(R.string.exam_branch_summary_format,
                getBranchShortTitle(branchType),
                branchType.getTotalQuestions(),
                branchType.getDurationMinutes(),
                exams.size()));

        branchAdapter.submitList(exams);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            android.widget.FrameLayout bottomSheet =
                    dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<android.widget.FrameLayout> behavior =
                        BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);

                // Expand to 88% of screen height for optimal scroll experience
                android.view.ViewGroup.LayoutParams lp = bottomSheet.getLayoutParams();
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                lp.height = (int) (screenHeight * 0.88);
                bottomSheet.setLayoutParams(lp);
            }
        }
    }

    private void selectExam(ExamType type, int testNumber) {
        dismiss();
        if (listener != null) {
            listener.onExamSelected(type, testNumber);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
