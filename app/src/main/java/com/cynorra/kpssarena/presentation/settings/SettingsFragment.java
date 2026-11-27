package com.cynorra.kpssarena.presentation.settings;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.navigation.Navigation;

import com.cynorra.kpssarena.BuildConfig;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentSettingsBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding, SettingsViewModel> {
    private final com.cynorra.kpssarena.notification.ReminderPermissionRequester reminderPermission =
            new com.cynorra.kpssarena.notification.ReminderPermissionRequester(this, this::onReminderPermissionResult);

    private void onReminderPermissionResult(boolean allowed) {
        if (binding == null) return;
        com.cynorra.kpssarena.notification.ReminderScheduler.setEnabled(requireContext(), allowed);
        binding.switchNotifications.setChecked(allowed);
        if (!allowed) {
            Toast.makeText(getContext(), R.string.notif_permission_denied, Toast.LENGTH_LONG).show();
        }
    }

    private void updateFrequencyLabel() {
        if (binding == null) return;
        int[] labels = {R.string.notif_freq_low, R.string.notif_freq_normal, R.string.notif_freq_high};
        int f = com.cynorra.kpssarena.notification.ReminderScheduler.getFrequency(requireContext());
        binding.tvNotificationFrequency.setText(labels[Math.max(0, Math.min(2, f - 1))]);
    }

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
        binding.tvVersionBadge.setText("v" + BuildConfig.VERSION_NAME + " Lisans");

        // Bind Account Info Card
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if (name == null || name.trim().isEmpty()) {
                name = getString(R.string.default_username);
            }
            binding.tvUserName.setText(name);
            String email = user.getEmail();
            binding.tvUserEmail.setText(email != null && !email.isEmpty() ? email : "misafir@kpssarena.app");
            binding.tvUserInitials.setText(extractInitials(name));
        } else {
            binding.tvUserName.setText(R.string.default_username);
            binding.tvUserEmail.setText("misafir@kpssarena.app");
            binding.tvUserInitials.setText("KA");
        }
        binding.cardAccountInfo.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.profileFragment));

        // Notifications
        binding.switchNotifications.setChecked(
                com.cynorra.kpssarena.notification.ReminderScheduler.isEnabled(requireContext())
                        && com.cynorra.kpssarena.notification.NotificationHelper.canNotify(requireContext()));
        binding.itemNotifications.setOnClickListener(v -> {
            if (binding.switchNotifications.isChecked()) {
                com.cynorra.kpssarena.notification.ReminderScheduler.setEnabled(requireContext(), false);
                binding.switchNotifications.setChecked(false);
            } else {
                reminderPermission.request();
            }
        });

        updateFrequencyLabel();
        binding.itemNotificationFrequency.setOnClickListener(v -> {
            final android.content.Context ctx = requireContext();
            String[] labels = {
                    getString(R.string.notif_freq_low),
                    getString(R.string.notif_freq_normal),
                    getString(R.string.notif_freq_high)};
            int checked = com.cynorra.kpssarena.notification.ReminderScheduler.getFrequency(ctx) - 1;
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(ctx)
                    .setTitle(R.string.settings_notification_frequency)
                    .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                        com.cynorra.kpssarena.notification.ReminderScheduler.setFrequency(ctx, which + 1);
                        updateFrequencyLabel();
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        });

        // Segmented Language Toggle
        updateLanguageToggleUI();
        binding.btnLangTr.setOnClickListener(v ->
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("tr")));
        binding.btnLangEn.setOnClickListener(v ->
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en")));

        // Google Play Rate CTA
        binding.btnRatePlayStore.setOnClickListener(v -> openPlayStore());

        // Account & Privacy
        binding.itemPrivacyPolicy.setOnClickListener(v ->
                openUrl("https://cynorra.github.io/kpss-arena-mobil/privacy.html"));
        binding.itemTermsOfService.setOnClickListener(v ->
                openUrl("https://cynorra.github.io/kpss-arena-mobil/terms.html"));
        binding.itemContactUs.setOnClickListener(v -> sendFeedbackEmail());
        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            Navigation.findNavController(v).navigate(R.id.loginFragment);
        });
        binding.itemDeleteAccount.setOnClickListener(v -> showDeleteAccountConfirmation());
    }

    private String extractInitials(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return "KA";
        String[] parts = trimmed.split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(java.util.Locale.forLanguageTag("tr"));
        }
        return trimmed.substring(0, Math.min(2, trimmed.length())).toUpperCase(java.util.Locale.forLanguageTag("tr"));
    }

    private void updateLanguageToggleUI() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        String language = !locales.isEmpty()
                ? locales.get(0).getLanguage()
                : getResources().getConfiguration().getLocales().get(0).getLanguage();
        boolean isEnglish = "en".equals(language);

        int activeColor = ContextCompat.getColor(requireContext(), R.color.primary);
        int inactiveColor = ContextCompat.getColor(requireContext(), R.color.on_surface_variant);

        if (isEnglish) {
            binding.btnLangEn.setBackgroundResource(R.drawable.bg_segmented_active);
            binding.btnLangEn.setTextColor(activeColor);
            binding.btnLangTr.setBackground(null);
            binding.btnLangTr.setTextColor(inactiveColor);
        } else {
            binding.btnLangTr.setBackgroundResource(R.drawable.bg_segmented_active);
            binding.btnLangTr.setTextColor(activeColor);
            binding.btnLangEn.setBackground(null);
            binding.btnLangEn.setTextColor(inactiveColor);
        }
    }

    private void openPlayStore() {
        String pkg = requireContext().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
        } catch (android.content.ActivityNotFoundException e) {
            openUrl("https://play.google.com/store/apps/details?id=" + pkg);
        }
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:eren.gp54@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_feedback_subject));
        startActivity(Intent.createChooser(intent, getString(R.string.action_send_email)));
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
