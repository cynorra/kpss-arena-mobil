package com.cynorra.kpssarena.presentation.admin;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.databinding.FragmentAddQuestionBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import java.util.UUID;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class AddQuestionFragment extends BaseFragment<FragmentAddQuestionBindi
    @Override
    protected FragmentAddQuestionBinding inflateBinding(LayoutInflater inflater, ViewG
        return FragmentAddQuestionBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<AdminViewModel> getViewModelClass() {
        return AdminViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.btnSave.setOnClickListener(v -> saveQuestion());
    }
    private void saveQuestion() {
        String text = binding.etQuestionText.getText() != null ? 
                binding.etQuestionText.getText().toString().trim() : "";
        String optionA = binding.etOptionA.getText() != null ? 
                binding.etOptionA.getText().toString().trim() : "";
        String optionB = binding.etOptionB.getText() != null ? 
                binding.etOptionB.getText().toString().trim() : "";
        String optionC = binding.etOptionC.getText() != null ? 
                binding.etOptionC.getText().toString().trim() : "";
        String optionD = binding.etOptionD.getText() != null ? 

                binding.etOptionD.getText().toString().trim() : "";
        String correctAnswer = binding.spinnerCorrect.getSelectedItem() != null ? 
                binding.spinnerCorrect.getSelectedItem().toString() : "A";
        String category = binding.spinnerCategory.getSelectedItem() != null ? 
                binding.spinnerCategory.getSelectedItem().toString() : "Türkçe";
        String explanation = binding.etExplanation.getText() != null ? 
                binding.etExplanation.getText().toString().trim() : "";
        String testDate = binding.etTestDate.getText() != null ? 
                binding.etTestDate.getText().toString().trim() : "";
        if (text.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || 
                optionC.isEmpty() || optionD.isEmpty() || testDate.isEmpty()) {
            Toast.makeText(getContext(), "Tüm alanları doldurun.", Toast.LENGTH_SHORT).sh
            return;
        }
        DailyTestDto.QuestionDto question = new DailyTestDto.QuestionDto();
        question.setId(UUID.randomUUID().toString());
        question.setText(text);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrect(correctAnswer);
        question.setCategory(category);
        question.setExplanation(explanation);
        viewModel.addQuestion(testDate, question);
    }
    @Override
    protected void observeData() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.btnSave.setEnabled(loading == null || !loading);
        });
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                clearFields();

            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(
        });
    }
    private void clearFields() {
        binding.etQuestionText.setText("");
        binding.etOptionA.setText("");
        binding.etOptionB.setText("");
        binding.etOptionC.setText("");
        binding.etOptionD.setText("");
        binding.etExplanation.setText("");
    }
}
