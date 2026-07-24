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
import com.cynorra.kpssarena.databinding.FragmentAdminBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class AdminFragment extends BaseFragment<FragmentAdminBinding, AdminViewModel> {

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
        binding.btnAddQuestion.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    AdminFragmentDirections.actionAdminToAddQuestion());
        });
        binding.btnScheduledTests.setOnClickListener(v -> {
            viewModel.loadScheduledTests();
        });
        binding.btnUserManagement.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Kullanıcı yönetimi yakında!", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void observeData() {
        viewModel.getScheduledTests().observe(getViewLifecycleOwner(), tests -> {
            // Update RecyclerView with scheduled tests
        });
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
