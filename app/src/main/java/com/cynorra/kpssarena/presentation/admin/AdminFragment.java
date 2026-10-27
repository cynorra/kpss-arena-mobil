package com.cynorra.kpssarena.presentation.admin;
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
import com.cynorra.kpssarena.databinding.FragmentAdminBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class AdminFragment extends BaseFragment<FragmentAdminBinding, AdminViewModel> {
    private final ScheduledTestAdapter adapter = new ScheduledTestAdapter();

    @Override
    protected FragmentAdminBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<AdminViewModel> getViewModelClass() {
        return AdminViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.rvScheduledTests.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvScheduledTests.setAdapter(adapter);
        binding.btnAddQuestion.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    AdminFragmentDirections.actionAdminToAddQuestion());
        });
        binding.btnScheduledTests.setOnClickListener(v -> {
            viewModel.loadScheduledTests();
        });
        viewModel.loadScheduledTests();
    }
    @Override
    protected void observeData() {
        viewModel.getScheduledTests().observe(getViewLifecycleOwner(), tests -> {
            adapter.submitList(tests);
            boolean empty = tests == null || tests.isEmpty();
            binding.tvScheduledTestsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvScheduledTests.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
