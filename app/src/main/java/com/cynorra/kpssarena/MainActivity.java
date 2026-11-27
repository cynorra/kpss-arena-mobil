package com.cynorra.kpssarena;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.cynorra.kpssarena.databinding.ActivityMainBinding;
import com.cynorra.kpssarena.presentation.common.ConsentManager;
import com.google.firebase.auth.FirebaseAuth;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import timber.log.Timber;
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    @Inject
    ConsentManager consentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure KPSS Arena defaults to Turkish so English-locale devices never show mixed TR/EN UI.
        if (AppCompatDelegate.getApplicationLocales().isEmpty()) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("tr"));
        }
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Edge-to-edge: keep content clear of the status bar / cutouts, and lift the floating bottom nav above the gesture bar.
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        // Preserve KPSS Arena's Obsidian Midnight + Radiant Amber theme (do NOT let Material You overwrite it with grey).
        // If user is in guest mode without a Firebase session, silently establish anonymous auth in background.
        if (FirebaseAuth.getInstance().getCurrentUser() == null
                && getSharedPreferences("app_prefs", MODE_PRIVATE).getBoolean("is_guest_mode", false)) {
            FirebaseAuth.getInstance().signInAnonymously();
        }
        // AdMob/UMP: must request consent (and show the form where required, e.g. EEA/UK)
        // before any ad requests go out from HomeFragment/TestResultFragment.
        consentManager.initialize(this, new ConsentManager.ConsentCallback() {
            @Override
            public void onConsentObtained() {
                Timber.d("Consent obtained");
            }
            @Override
            public void onConsentNotRequired() {
                Timber.d("Consent not required or failed to load");
            }
        });
        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
        if (navController == null) return;
        // Bottom nav visibility control

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            boolean showBottomNav = destId == R.id.homeFragment ||
                    destId == R.id.practiceFragment ||
                    destId == R.id.arenaFragment ||
                    destId == R.id.settingsFragment;
            binding.bottomNavigation.setVisibility(showBottomNav ? View.VISIBLE : View.GONE);
        });

        com.cynorra.kpssarena.presentation.common.ReviewPromptHelper.trackLaunchAndMaybePrompt(this);
    }
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
