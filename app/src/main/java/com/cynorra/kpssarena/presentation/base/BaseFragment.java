package com.cynorra.kpssarena.presentation.base;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import androidx.viewbinding.ViewBinding;
import com.cynorra.kpssarena.presentation.common.ShimmerUtils;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
public abstract class BaseFragment<VB extends ViewBinding, VM extends ViewModel> extends Fragment {
    protected VB binding;
    protected VM viewModel;
    
    @Inject
    protected ViewModelProvider.Factory viewModelFactory;
    protected abstract VB inflateBinding(LayoutInflater inflater, ViewGroup container);
    protected abstract Class<VM> getViewModelClass();
    protected abstract void initViews();
    protected abstract void observeData();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = inflateBinding(inflater, container);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getViewModelClass() != null) {
            viewModel = new ViewModelProvider(this, viewModelFactory).get(getViewModelClass());
        }
        
        initViews();
        observeData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    protected void showShimmer(View... views) {
        ShimmerUtils.showShimmer(views);
    }
    protected void hideShimmer(View... views) {
        ShimmerUtils.hideShimmer(views);
    }
}