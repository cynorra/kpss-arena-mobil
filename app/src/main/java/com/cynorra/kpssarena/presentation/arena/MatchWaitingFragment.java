package com.cynorra.kpssarena.presentation.arena;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMatchWaitingBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MatchWaitingFragment extends BaseFragment<FragmentMatchWaitingBinding, MatchWaitingViewModel> {
    private String matchId;
    @Override
    protected FragmentMatchWaitingBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMatchWaitingBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<MatchWaitingViewModel> getViewModelClass() {
        return MatchWaitingViewModel.class;
    }
    @Override
    protected void initViews() {
        matchId = requireArguments().getString("matchId");
        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        viewModel.startWatching(matchId);
    }
    @Override
    protected void observeData() {
        viewModel.getOpponentReady().observe(getViewLifecycleOwner(), ready -> {
            if (ready != null && ready) {
                Bundle args = new Bundle();
                args.putString("matchId", matchId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_waiting_to_test, args);
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getTimedOut().observe(getViewLifecycleOwner(), timedOut -> {
            if (timedOut != null && timedOut) {
                binding.tvWaitingMessage.setText(R.string.error_opponent_not_found);
            }
        });
    }
}
