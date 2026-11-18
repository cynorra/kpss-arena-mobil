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
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class ExamSelectionBottomSheet extends BottomSheetDialogFragment {

    public interface OnExamSelectedListener {
        void onExamSelected(ExamType examType, int testNumber);
    }

    @Inject
    ExamResultStore examResultStore;

    @Inject
    FirebaseFirestoreSource firestoreSource;

    private BottomSheetExamSelectionBinding binding;
    private OnExamSelectedListener listener;
    private BranchExamAdapter branchAdapter;
    @Nullable
    private ExamType selectedBranch = null; // null means "Tümü" (All)

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

        // Load default (Tümü - all branch exams)
        loadBranchExams(selectedBranch);

        // Sync remote branch exams from Firestore if available
        syncRemoteBranchExams();
    }

    private void syncRemoteBranchExams() {
        if (firestoreSource == null) return;
        firestoreSource.getRemoteBranchExams()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && !snapshot.isEmpty() && isAdded() && binding != null) {
                        List<BranchExam> remoteList = new ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshot.getDocuments()) {
                            try {
                                String id = doc.getId();
                                String examTypeName = doc.getString("examType");
                                if (examTypeName == null) continue;
                                ExamType type = ExamType.valueOf(examTypeName);
                                Long testNum = doc.getLong("testNumber");
                                int testNumber = testNum != null ? testNum.intValue() : 1;
                                String title = doc.getString("title");
                                if (title == null || title.isEmpty()) {
                                    title = type.getTitle() + " #" + testNumber;
                                }
                                String subtitle = doc.getString("subtitle");
                                if (subtitle == null || subtitle.isEmpty()) {
                                    subtitle = type.getTotalQuestions() + " Soru • " + type.getDurationMinutes() + " Dk";
                                }
                                Long totalQ = doc.getLong("totalQuestions");
                                int questions = totalQ != null ? totalQ.intValue() : type.getTotalQuestions();
                                Long duration = doc.getLong("durationMinutes");
                                int mins = duration != null ? duration.intValue() : type.getDurationMinutes();

                                remoteList.add(new BranchExam(id, type, testNumber, title, subtitle, questions, mins));
                            } catch (Exception e) {
                                Timber.w(e, "Error parsing remote branch exam");
                            }
                        }
                        if (!remoteList.isEmpty()) {
                            BranchExamCatalog.registerRemoteExams(remoteList);
                            loadBranchExams(selectedBranch);
                        }
                    }
                })
                .addOnFailureListener(e -> Timber.d("Remote branch exams sync skipped: %s", e.getMessage()));
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

        // 1. "Tümü" Chip
        Chip allChip = new Chip(requireContext());
        allChip.setText(getString(R.string.exam_branch_all));
        allChip.setCheckable(true);
        allChip.setClickable(true);
        allChip.setFocusable(true);
        allChip.setCheckedIconVisible(false);
        allChip.setChipBackgroundColor(new ColorStateList(states, bgColors));
        allChip.setTextColor(new ColorStateList(states, textColors));
        allChip.setChipStrokeColor(new ColorStateList(states, strokeColors));
        allChip.setChipStrokeWidth(2f);
        allChip.setId(View.generateViewId());
        allChip.setTag(null);

        if (selectedBranch == null) {
            allChip.setChecked(true);
        }
        binding.chipGroupBranches.addView(allChip);

        // 2. Individual Subject Chips
        for (ExamType branchType : BRANCH_TYPES) {
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
            chip.setTag(branchType);

            if (branchType == selectedBranch) {
                chip.setChecked(true);
            }

            binding.chipGroupBranches.addView(chip);
        }

        binding.chipGroupBranches.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds == null || checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            Chip checkedChip = group.findViewById(checkedId);
            if (checkedChip != null) {
                ExamType type = (ExamType) checkedChip.getTag();
                selectedBranch = type;
                loadBranchExams(selectedBranch);
            }
        });
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

    private void loadBranchExams(@Nullable ExamType branchType) {
        if (binding == null) return;

        List<BranchExam> exams;
        if (branchType == null) {
            exams = BranchExamCatalog.getAllBranchExams();
            binding.tvBranchSummary.setText(getString(R.string.exam_branch_all_summary_format, exams.size()));
        } else {
            exams = BranchExamCatalog.getExamsForType(branchType);
            binding.tvBranchSummary.setText(getString(R.string.exam_branch_summary_format,
                    getBranchShortTitle(branchType),
                    branchType.getTotalQuestions(),
                    branchType.getDurationMinutes(),
                    exams.size()));
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        if (examResultStore != null && !uid.isEmpty()) {
            for (BranchExam exam : exams) {
                ScoreResult res = examResultStore.getResult(uid, exam.getExamType(), exam.getTestNumber());
                if (res != null) {
                    exam.setLastResult(res);
                }
            }
        }

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
