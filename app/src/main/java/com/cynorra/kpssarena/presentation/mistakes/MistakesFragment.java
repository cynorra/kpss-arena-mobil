package com.cynorra.kpssarena.presentation.mistakes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMistakesBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Collections;

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

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Dynamic modern filter chips
        setupCategoryChips();

        binding.btnRetryAll.setOnClickListener(v -> viewModel.retryAll());
    }

    private void setupCategoryChips() {
        String[] categories = getResources().getStringArray(R.array.mistake_category_filters);
        binding.chipGroupCategories.removeAllViews();

        for (int i = 0; i < categories.length; i++) {
            final int index = i;
            final String cat = categories[i];
            Chip chip = new Chip(requireContext());
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setTextSize(13f);
            chip.setChipCornerRadius(24f);

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

            chip.setChipBackgroundColor(new ColorStateList(states, bgColors));
            chip.setTextColor(new ColorStateList(states, textColors));
            chip.setChipStrokeColor(new ColorStateList(states, strokeColors));
            chip.setChipStrokeWidth(2f);
            chip.setId(View.generateViewId());

            if (i == 0) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    String filter = index == 0 ? null : cat;
                    viewModel.filterByCategory(filter);
                }
            });

            binding.chipGroupCategories.addView(chip);
        }
    }

    @Override
    protected void observeData() {
        viewModel.getMistakes().observe(getViewLifecycleOwner(), mistakes -> {
            int count = mistakes != null ? mistakes.size() : 0;
            boolean empty = count == 0;

            binding.tvMistakeCount.setText(getString(R.string.mistakes_count_format, count));
            adapter.submitList(mistakes != null ? new ArrayList<>(mistakes) : Collections.emptyList());

            binding.tvEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvMistakes.setVisibility(empty ? View.GONE : View.VISIBLE);
            binding.containerRetryBar.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        viewModel.getRetryMistakesEvent().observe(getViewLifecycleOwner(), mistakesToRetry -> {
            if (mistakesToRetry != null && !mistakesToRetry.isEmpty()) {
                viewModel.onRetryHandled();
                try {
                    Navigation.findNavController(requireView()).navigate(R.id.action_mistakes_to_practice);
                } catch (Exception e) {
                    // Fallback
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refresh();
        }
    }
}
