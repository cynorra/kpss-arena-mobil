package com.cynorra.kpssarena.presentation.auth;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
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
                Toast.makeText(getContext(), R.string.error_google_sign_in_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void observeData() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnGoogleSignIn.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnGoogleSignIn.setEnabled(true);
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
