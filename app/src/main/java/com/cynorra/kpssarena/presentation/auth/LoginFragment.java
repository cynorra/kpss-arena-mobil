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
import com.cynorra.kpssarena.databinding.FragmentLoginBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends BaseFragment<FragmentLoginBinding, AuthViewModel> {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    @Override
    protected FragmentLoginBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<AuthViewModel> getViewModelClass() {
        return AuthViewModel.class;
    }
    @Override
    protected void initViews() {
        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
            viewModel.login(email, password);
        });
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        binding.tvRegister.setOnClickListener(v -> {
            NavDirections action = LoginFragmentDirections.actionLoginToRegister();
            Navigation.findNavController(v).navigate(action);
        });
        binding.tvForgotPassword.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";

            if (!email.isEmpty()) {
                viewModel.sendPasswordResetEmail(email);
            } else {
                Toast.makeText(getContext(), "Lütfen e-posta adresinizi girin.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    viewModel.signInWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Google ile giriş başarısız.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void observeData() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
            }

        });
        viewModel.getNavigateToHome().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate) {
                NavDirections action = LoginFragmentDirections.actionLoginToHome();
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
