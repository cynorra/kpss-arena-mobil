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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentSettingsBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding, SettingsViewModel> {
    @Override
    protected FragmentSettingsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
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
                openUrl("https://cynorra.github.io/kpss-arena-mobil/privacy.html"));
        
        binding.itemTermsOfService.setOnClickListener(v ->
                openUrl("https://cynorra.github.io/kpss-arena-mobil/terms.html"));
        
        binding.itemContactUs.setOnClickListener(v -> 
                sendFeedbackEmail());
        
        binding.itemAbout.setOnClickListener(v -> 
                showAboutDialog());
        
        binding.itemDeleteAccount.setOnClickListener(v ->

                showDeleteAccountConfirmation());

        binding.itemLanguage.setOnClickListener(v -> showLanguageDialog());
        updateCurrentLanguageLabel();
    }
    private void showLanguageDialog() {
        String[] languageNames = {getString(R.string.language_turkish), getString(R.string.language_english)};
        String[] languageTags = {"tr", "en"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.settings_language)
                .setItems(languageNames, (dialog, which) ->
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTags[which])))
                .show();
    }
    private void updateCurrentLanguageLabel() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        boolean isEnglish = !locales.isEmpty() && "en".equals(locales.get(0).getLanguage());
        binding.tvCurrentLanguage.setText(isEnglish ? R.string.language_english : R.string.language_turkish);
    }
    private void toggleNotificationSettings() {
        Toast.makeText(getContext(), R.string.toast_notifications_coming_soon, Toast.LENGTH_SHORT).show();
    }
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@cynorra.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_feedback_subject));
        startActivity(Intent.createChooser(intent, getString(R.string.action_send_email)));
    }
    private void showAboutDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_about_title)
                .setMessage(R.string.dialog_about_message)
                .setPositiveButton(R.string.action_ok, null)
                .show();
    }
    private void showDeleteAccountConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_delete_account_title)
                .setMessage(R.string.dialog_delete_account_message)
                .setPositiveButton(R.string.action_delete_my_account, (dialog, which) -> viewModel.deleteAccount())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    protected void observeData() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }
}
