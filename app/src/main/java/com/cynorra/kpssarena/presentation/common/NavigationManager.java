package com.cynorra.kpssarena.presentation.common;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
public class NavigationManager {
    private final NavController navController;
    public NavigationManager(Fragment fragment) {
        this.navController = Navigation.findNavController(fragment.requireView());
    }
    public void navigate(@IdRes int destinationId) {
        navController.navigate(destinationId);
    }
    public void navigate(@IdRes int destinationId, Bundle args) {
        navController.navigate(destinationId, args);
    }
    public void navigate(NavDirections directions) {
        navController.navigate(directions);
    }
    public void navigateWithAnimation(@IdRes int destinationId) {
        NavOptions navOptions = new NavOptions.Builder()

                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build();
        navController.navigate(destinationId, null, navOptions);
    }
    public void navigateAndClearBackStack(@IdRes int destinationId) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .build();
        navController.navigate(destinationId, null, navOptions);
    }
    public void navigateWithSharedAxis(@IdRes int destinationId) {
        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.shared_axis_x_enter)
                .setExitAnim(R.anim.shared_axis_x_exit)
                .setPopEnterAnim(R.anim.shared_axis_x_pop_enter)
                .setPopExitAnim(R.anim.shared_axis_x_pop_exit)
                .build();
        navController.navigate(destinationId, null, navOptions);
    }
    public boolean goBack() {
        return navController.navigateUp();
    }
    public void popBackStack() {
        navController.popBackStack();
    }
    public void popBackStack(@IdRes int destinationId, boolean inclusive) {
        navController.popBackStack(destinationId, inclusive);
    }
    public boolean isOnDestination(@IdRes int destinationId) {

        return navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == destinationId;
    }
}
