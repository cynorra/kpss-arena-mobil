package com.cynorra.kpssarena.presentation.arena;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMatchResultBinding;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MatchResultFragment extends BaseFragment<FragmentMatchResultBinding, MatchResultViewModel> {
    @Override
    protected FragmentMatchResultBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMatchResultBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<MatchResultViewModel> getViewModelClass() {
        return MatchResultViewModel.class;
    }
    @Override
    protected void initViews() {
        String matchId = requireArguments().getString("matchId");
        binding.btnBackToArena.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_match_result_to_arena));
        viewModel.loadMatch(matchId);
    }
    @Override
    protected void observeData() {
        viewModel.getMatch().observe(getViewLifecycleOwner(), this::displayResult);
    }
    private void displayResult(Match match) {
        if (match == null) return;
        String myUid = viewModel.getMyUid();
        int myScore = match.getMyScore(myUid);
        int opponentScore = match.getOpponentScore(myUid);
        binding.tvMyScore.setText(String.valueOf(myScore));
        binding.tvOpponentScore.setText(String.valueOf(opponentScore));
        binding.tvMyUsername.setText(myUid.equals(match.getPlayer1Uid()) ? match.getPlayer1Username() : match.getPlayer2Username());
        binding.tvOpponentUsername.setText(match.getOpponentUsername(myUid));
        int resultTextRes;
        if (myScore > opponentScore) {
            resultTextRes = R.string.match_result_win;
        } else if (myScore < opponentScore) {
            resultTextRes = R.string.match_result_lose;
        } else {
            resultTextRes = R.string.match_result_draw;
        }
        binding.tvResultTitle.setText(resultTextRes);
    }
}
