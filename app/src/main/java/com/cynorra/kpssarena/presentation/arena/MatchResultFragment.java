package com.cynorra.kpssarena.presentation.arena;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMatchResultBinding;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.ShareCardGenerator;
import com.cynorra.kpssarena.presentation.common.ShareHelper;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MatchResultFragment extends BaseFragment<FragmentMatchResultBinding, MatchResultViewModel> {
    private String opponentUsername;
    private int myScore;
    private int opponentScore;
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
        binding.btnShare.setOnClickListener(v -> shareResult());
        binding.btnShareStory.setOnClickListener(v -> shareResultToStory());
        viewModel.loadMatch(matchId);
    }
    @Override
    protected void observeData() {
        viewModel.getMatch().observe(getViewLifecycleOwner(), this::displayResult);
    }
    private void displayResult(Match match) {
        if (match == null) return;
        String myUid = viewModel.getMyUid();
        myScore = match.getMyScore(myUid);
        opponentScore = match.getOpponentScore(myUid);
        opponentUsername = match.getOpponentUsername(myUid);
        binding.tvMyScore.setText(String.valueOf(myScore));
        binding.tvOpponentScore.setText(String.valueOf(opponentScore));
        binding.tvMyUsername.setText(myUid.equals(match.getPlayer1Uid()) ? match.getPlayer1Username() : match.getPlayer2Username());
        binding.tvOpponentUsername.setText(opponentUsername);
        int resultTextRes;
        boolean won = myScore > opponentScore;
        if (won) {
            resultTextRes = R.string.match_result_win;
        } else if (myScore < opponentScore) {
            resultTextRes = R.string.match_result_lose;
        } else {
            resultTextRes = R.string.match_result_draw;
        }
        binding.tvResultTitle.setText(resultTextRes);
        // Viral paylasim sadece kazananda anlamli
        int shareVisibility = won ? View.VISIBLE : View.GONE;
        binding.btnShare.setVisibility(shareVisibility);
        binding.btnShareStory.setVisibility(shareVisibility);
    }
    private void shareResult() {
        String shareText = getString(R.string.share_match_win_template,
                opponentUsername, myScore, opponentScore, getString(R.string.play_store_url));
        ShareHelper.shareAsPost(this, shareText, null);
    }
    private void shareResultToStory() {
        android.graphics.Bitmap card = ShareCardGenerator.generateStoryCard(requireContext(),
                getString(R.string.share_story_match_win_title),
                myScore + " - " + opponentScore,
                getString(R.string.share_story_score_label),
                getString(R.string.share_story_cta));
        ShareHelper.shareToStory(this, card);
    }
}
