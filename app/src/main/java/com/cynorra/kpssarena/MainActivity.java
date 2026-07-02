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
import com.google.android.material.color.DynamicColors;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Dynamic colors (Material You)
        DynamicColors.applyToActivityIfAvailable(this);
        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentMa
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
        // Bottom nav visibility control

        navController.addOnDestinationChangedListener((controller, destination, argumen
            int destId = destination.getId();
            boolean showBottomNav = destId == R.id.homeFragment ||
                    destId == R.id.leaderboardFragment ||
                    destId == R.id.arenaFragment ||
                    destId == R.id.profileFragment;
            binding.bottomNavigation.setVisibility(showBottomNav ? View.VISIBLE : View.GON
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
