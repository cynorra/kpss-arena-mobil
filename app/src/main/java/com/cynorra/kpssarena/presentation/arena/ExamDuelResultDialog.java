package com.cynorra.kpssarena.presentation.arena;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.DialogExamDuelResultBinding;
import com.cynorra.kpssarena.domain.model.Match;
import com.google.firebase.auth.FirebaseAuth;

public class ExamDuelResultDialog extends Dialog {

    private final Match match;

    public ExamDuelResultDialog(@NonNull Context context, Match match) {
        super(context);
        this.match = match;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogExamDuelResultBinding binding = DialogExamDuelResultBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(
                    (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.92),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        String myUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        String p1Name = match.getPlayer1Username() != null ? match.getPlayer1Username() : "Aday 1";
        String p2Name = match.getPlayer2Username() != null ? match.getPlayer2Username() : "Aday 2";

        binding.tvPlayer1Name.setText(p1Name);
        binding.tvPlayer2Name.setText(p2Name);

        String examName = match.getExamTitle() != null ? match.getExamTitle() : "Deneme Düellosu";
        binding.tvExamBadge.setText(examName);

        binding.tvPlayer1Net.setText(String.format("%.2f", match.getPlayer1Net()));
        binding.tvPlayer2Net.setText(String.format("%.2f", match.getPlayer2Net()));

        binding.tvPlayer1Cw.setText(match.getPlayer1Correct() + "D " + match.getPlayer1Wrong() + "Y");
        binding.tvPlayer2Cw.setText(match.getPlayer2Correct() + "D " + match.getPlayer2Wrong() + "Y");

        int p1Min = Math.max(1, match.getPlayer1DurationSeconds() / 60);
        int p2Min = Math.max(1, match.getPlayer2DurationSeconds() / 60);
        binding.tvPlayer1Time.setText(p1Min + " dk");
        binding.tvPlayer2Time.setText(p2Min + " dk");

        String winnerUid = match.getWinnerUid();
        if (winnerUid == null || winnerUid.isEmpty()) {
            binding.tvResultHeader.setText(getContext().getString(R.string.duel_result_draw));
        } else if (winnerUid.equals(myUid)) {
            binding.tvResultHeader.setText(getContext().getString(R.string.duel_result_winner_you));
        } else {
            String winnerName = winnerUid.equals(match.getPlayer1Uid()) ? p1Name : p2Name;
            binding.tvResultHeader.setText(getContext().getString(R.string.duel_result_winner_opponent, winnerName));
        }

        binding.btnClose.setOnClickListener(v -> dismiss());
    }
}
