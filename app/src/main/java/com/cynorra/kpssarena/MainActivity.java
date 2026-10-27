package com.cynorra.kpssarena;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.cynorra.kpssarena.databinding.ActivityMainBinding;
import com.cynorra.kpssarena.presentation.common.ConsentManager;
import com.google.android.material.color.DynamicColors;
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
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Dynamic colors (Material You)
        DynamicColors.applyToActivityIfAvailable(this);
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
        // Bottom nav visibility control

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            boolean showBottomNav = destId == R.id.homeFragment ||
                    destId == R.id.leaderboardFragment ||
                    destId == R.id.arenaFragment ||
                    destId == R.id.feedFragment ||
                    destId == R.id.profileFragment;
            binding.bottomNavigation.setVisibility(showBottomNav ? View.VISIBLE : View.GONE);
        });
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
