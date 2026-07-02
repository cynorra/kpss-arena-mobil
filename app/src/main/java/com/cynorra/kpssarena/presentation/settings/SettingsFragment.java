package com.cynorra.kpssarena.presentation.settings;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentSettingsBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding, Setting
    @Override
    protected FragmentSettingsBinding inflateBinding(LayoutInflater inflater, ViewGroup 
        return FragmentSettingsBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<SettingsViewModel> getViewModelClass() {
        return SettingsViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.itemNotifications.setOnClickListener(v -> 
                toggleNotificationSettings());
        
        binding.itemPrivacyPolicy.setOnClickListener(v -> 
                openUrl("https://cynorra.com/privacy"));
        
        binding.itemTermsOfService.setOnClickListener(v -> 
                openUrl("https://cynorra.com/terms"));
        
        binding.itemContactUs.setOnClickListener(v -> 
                sendFeedbackEmail());
        
        binding.itemAbout.setOnClickListener(v -> 
                showAboutDialog());
        
        binding.itemDeleteAccount.setOnClickListener(v -> 

                showDeleteAccountConfirmation());
    }
    private void toggleNotificationSettings() {
        Toast.makeText(getContext(), "Bildirim ayarları yakında!", Toast.LENGTH_SHORT).sh
    }
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@cynorra.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "KPSS Arena Geri Bildirim");
        startActivity(Intent.createChooser(intent, "E-posta gönder"));
    }
    private void showAboutDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext
                .setTitle("KPSS Arena Hakkında")
                .setMessage("Versiyon: 1.0.0\n\n" +
                        "KPSS Arena, KPSS Genel Yetenek sınavına hazırlanan adaylar için " +
                        "geliştirilmiş günlük test ve sıralama platformudur.\n\n" +
                        "© 2024 Cynorra. Tüm hakları saklıdır.")
                .setPositiveButton("Tamam", null)
                .show();
    }
    private void showDeleteAccountConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext
                .setTitle("Hesabı Sil")
                .setMessage("Hesabınızı silmek istediğinize emin misiniz? " +
                        "Bu işlem geri alınamaz ve tüm verileriniz kalıcı olarak silinir.")
                .setPositiveButton("Hesabımı Sil", (dialog, which) -> viewModel.deleteAccount())
                .setNegativeButton("Vazgeç", null)
                .show();
    }

    @Override
    protected void observeData() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(
        });
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }
}
