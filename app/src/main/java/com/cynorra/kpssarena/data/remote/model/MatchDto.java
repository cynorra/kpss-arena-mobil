package com.cynorra.kpssarena.data.remote.model;
import java.util.List;
import java.util.Map;
public class MatchDto {
    private List<String> players;
    private String player1Uid;
    private String player1Username;
    private String player2Uid;
    private String player2Username;
    private String status;
    private List<DailyTestDto.QuestionDto> questions;
    private Map<String, Integer> scores;
    private String winnerUid;
    private long createdAt;
    private long startedAt;
    private long finishedAt;
    private int timePerQuestion;
    public MatchDto() {}
    public List<String> getPlayers() { return players; }
    public void setPlayers(List<String> players) { this.players = players; }
    public String getPlayer1Uid() { return player1Uid; }
    public void setPlayer1Uid(String player1Uid) { this.player1Uid = player1Uid; }
    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) { this.player1Username = player1Username; }
    public String getPlayer2Uid() { return player2Uid; }
    public void setPlayer2Uid(String player2Uid) { this.player2Uid = player2Uid; }
    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) { this.player2Username = player2Username; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<DailyTestDto.QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<DailyTestDto.QuestionDto> questions) { this.questions = questions; }
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
    public void setTimePerQuestion(int timePerQuestion) { this.timePerQuestion = timePerQuestion; }
}
