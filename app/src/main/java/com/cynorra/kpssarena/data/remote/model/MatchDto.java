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
    private String roomTitle;
    private String matchType;
    private String examType;
    private String examTitle;
    private double player1Net;
    private int player1Correct;
    private int player1Wrong;
    private int player1Empty;
    private int player1DurationSeconds;
    private double player2Net;
    private int player2Correct;
    private int player2Wrong;
    private int player2Empty;
    private int player2DurationSeconds;
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
    public String getRoomTitle() { return roomTitle; }
    public void setRoomTitle(String roomTitle) { this.roomTitle = roomTitle; }
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
    public double getPlayer1Net() { return player1Net; }
    public void setPlayer1Net(double player1Net) { this.player1Net = player1Net; }
    public int getPlayer1Correct() { return player1Correct; }
    public void setPlayer1Correct(int player1Correct) { this.player1Correct = player1Correct; }
    public int getPlayer1Wrong() { return player1Wrong; }
    public void setPlayer1Wrong(int player1Wrong) { this.player1Wrong = player1Wrong; }
    public int getPlayer1Empty() { return player1Empty; }
    public void setPlayer1Empty(int player1Empty) { this.player1Empty = player1Empty; }
    public int getPlayer1DurationSeconds() { return player1DurationSeconds; }
    public void setPlayer1DurationSeconds(int player1DurationSeconds) { this.player1DurationSeconds = player1DurationSeconds; }
    public double getPlayer2Net() { return player2Net; }
    public void setPlayer2Net(double player2Net) { this.player2Net = player2Net; }
    public int getPlayer2Correct() { return player2Correct; }
    public void setPlayer2Correct(int player2Correct) { this.player2Correct = player2Correct; }
    public int getPlayer2Wrong() { return player2Wrong; }
    public void setPlayer2Wrong(int player2Wrong) { this.player2Wrong = player2Wrong; }
    public int getPlayer2Empty() { return player2Empty; }
    public void setPlayer2Empty(int player2Empty) { this.player2Empty = player2Empty; }
    public int getPlayer2DurationSeconds() { return player2DurationSeconds; }
    public void setPlayer2DurationSeconds(int player2DurationSeconds) { this.player2DurationSeconds = player2DurationSeconds; }
}
