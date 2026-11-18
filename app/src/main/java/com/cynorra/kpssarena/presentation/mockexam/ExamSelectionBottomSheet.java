package com.cynorra.kpssarena.presentation.mockexam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cynorra.kpssarena.databinding.BottomSheetExamSelectionBinding;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ExamSelectionBottomSheet extends BottomSheetDialogFragment {

    public interface OnExamSelectedListener {
        void onExamSelected(ExamType examType);
    }

    private BottomSheetExamSelectionBinding binding;
    private OnExamSelectedListener listener;

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

        binding.cardExamFull.setOnClickListener(v -> selectExam(ExamType.FULL_KPSS));
        binding.cardExamGy.setOnClickListener(v -> selectExam(ExamType.GY));
        binding.cardExamGk.setOnClickListener(v -> selectExam(ExamType.GK));

        binding.cardBranchTurkce.setOnClickListener(v -> selectExam(ExamType.BRANCH_TURKCE));
        binding.cardBranchMatematik.setOnClickListener(v -> selectExam(ExamType.BRANCH_MATEMATIK));
        binding.cardBranchTarih.setOnClickListener(v -> selectExam(ExamType.BRANCH_TARIH));
        binding.cardBranchCografya.setOnClickListener(v -> selectExam(ExamType.BRANCH_COGRAFYA));
        binding.cardBranchVatandaslik.setOnClickListener(v -> selectExam(ExamType.BRANCH_VATANDASLIK));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() instanceof com.google.android.material.bottomsheet.BottomSheetDialog) {
            com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                    (com.google.android.material.bottomsheet.BottomSheetDialog) getDialog();
            android.widget.FrameLayout bottomSheet =
                    dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior<android.widget.FrameLayout> behavior =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }

    private void selectExam(ExamType type) {
        dismiss();
        if (listener != null) {
            listener.onExamSelected(type);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
