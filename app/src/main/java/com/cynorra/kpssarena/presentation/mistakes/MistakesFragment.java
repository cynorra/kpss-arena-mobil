package com.cynorra.kpssarena.presentation.mistakes;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cynorra.kpssarena.databinding.FragmentMistakesBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MistakesFragment extends BaseFragment<FragmentMistakesBinding, MistakesViewModel> {
    private MistakesAdapter adapter;
    @Override
    protected FragmentMistakesBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMistakesBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<MistakesViewModel> getViewModelClass() {
        return MistakesViewModel.class;
    }
    @Override
    protected void initViews() {
        adapter = new MistakesAdapter();
        binding.rvMistakes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMistakes.setAdapter(adapter);
        // Filter spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Tümü", "Türkçe", "Matematik", "Muhakeme"}
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(spinnerAdapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = position == 0 ? null : parent.getItemAtPosition(position).toString();
                viewModel.filterByCategory(category);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.btnRetryAll.setOnClickListener(v -> viewModel.retryAll());
    }
    @Override
    protected void observeData() {
        viewModel.getMistakes().observe(getViewLifecycleOwner(), mistakes -> {
            if (mistakes != null) {
                adapter.submitList(mistakes);
                binding.tvEmptyState.setVisibility(mistakes.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvMistakes.setVisibility(mistakes.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
    }
}
