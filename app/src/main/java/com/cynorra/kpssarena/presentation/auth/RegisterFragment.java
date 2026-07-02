package com.cynorra.kpssarena.presentation.auth;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentRegisterBinding;

import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class RegisterFragment extends BaseFragment<FragmentRegisterBinding, AuthVi
    @Override
    protected FragmentRegisterBinding inflateBinding(LayoutInflater inflater, ViewGroup 
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<AuthViewModel> getViewModelClass() {
        return AuthViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.etUsername.getText() != null ? 
                    binding.etUsername.getText().toString().trim() : "";
            String email = binding.etEmail.getText() != null ? 
                    binding.etEmail.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? 
                    binding.etPassword.getText().toString() : "";
            String confirmPassword = binding.etConfirmPassword.getText() != null ? 
                    binding.etConfirmPassword.getText().toString() : "";
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Şifreler eşleşmiyor.", Toast.LENGTH_SHORT).sho
                return;
            }
            viewModel.register(email, password, username);
        });
        binding.tvLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

    }
    @Override
    protected void observeData() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnRegister.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
            }
        });
        viewModel.getNavigateToHome().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate) {
                NavDirections action = RegisterFragmentDirections.actionRegisterToHome();
                Navigation.findNavController(requireView()).navigate(action);
                viewModel.onNavigationDone();
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
