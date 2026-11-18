package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.Match;
import com.google.android.gms.tasks.Task;
import java.util.List;
public interface MatchRepository {
    Task<String> challengeByUsername(String username);
    Task<String> findRandomMatch();
    Task<Match> getMatch(String matchId);
    Task<List<Match>> getUserActiveMatches(String uid);
    Task<List<Match>> getUserMatchHistory(String uid);
    Task<Void> submitAnswer(String matchId, String uid, int score);
    Task<Void> completeMatch(String matchId, String uid, int finalScore);
    Task<List<Match>> getOpenWaitingMatches();
    Task<String> createExamDuel(String examType, com.cynorra.kpssarena.domain.model.ScoreResult result);
    Task<Match> acceptExamDuel(String matchId, com.cynorra.kpssarena.domain.model.ScoreResult opponentResult);
    Task<Void> cancelMatch(String matchId);
    Task<String> resolveCurrentUsername();
    void listenForOpponentJoin(String matchId, MatchStatusListener listener);
    void listenOpponentScore(String matchId, String opponentUid, ScoreListener listener);
    void stopListening(String matchId);
    interface MatchStatusListener {
        void onStatusChanged(String status);
        void onError(String error);
    }
    interface ScoreListener {
        void onScoreChanged(int score);
    }
}
