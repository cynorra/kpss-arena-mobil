package com.cynorra.kpssarena.domain.model;
import java.util.List;
import java.util.Map;
public class Match {
    public enum MatchStatus {
        WAITING,
        READY,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    private String matchId;
    private String player1Uid;
    private String player1Username;
    private String player2Uid;
    private String player2Username;
    private MatchStatus status;
    private List<Question> questions;
    private Map<String, Integer> scores;
    private String winnerUid;
    private long createdAt;
    private long startedAt;
    private long finishedAt;
    private int timePerQuestion;
    public Match() {
        this.timePerQuestion = 45;
        this.status = MatchStatus.WAITING;
    }
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getPlayer1Uid() { return player1Uid; }
    public void setPlayer1Uid(String player1Uid) { this.player1Uid = player1Uid; }

    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) { this.player1Username = p
    public String getPlayer2Uid() { return player2Uid; }
    public void setPlayer2Uid(String player2Uid) { this.player2Uid = player2Uid; }
    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) { this.player2Username = p
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public Map<String, Integer> getScores() { return scores; }
    public void setScores(Map<String, Integer> scores) { this.scores = scores; }
    public String getWinnerUid() { return winnerUid; }
    public void setWinnerUid(String winnerUid) { this.winnerUid = winnerUid; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getStartedAt() { return startedAt; }
    public void setStartedAt(long startedAt) { this.startedAt = startedAt; }
    public long getFinishedAt() { return finishedAt; }
    public void setFinishedAt(long finishedAt) { this.finishedAt = finishedAt; }
    public int getTimePerQuestion() { return timePerQuestion; }
    public void setTimePerQuestion(int timePerQuestion) { this.timePerQuestion = timePe
    public String getOpponentUid(String myUid) {
        return player1Uid.equals(myUid) ? player2Uid : player1Uid;
    }
    public String getOpponentUsername(String myUid) {
        return player1Uid.equals(myUid) ? player2Username : player1Username;
    }

    public int getMyScore(String myUid) {
        return scores != null && scores.containsKey(myUid) ? scores.get(myUid) : 0;
    }
    public int getOpponentScore(String myUid) {
        String opponentUid = getOpponentUid(myUid);
        return scores != null && scores.containsKey(opponentUid) ? scores.get(opponentUi
    }
}
