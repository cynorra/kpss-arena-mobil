package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.FirebaseRealtimeSource;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.data.remote.model.MatchDto;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
public class MatchRepositoryImpl implements MatchRepository {
    private final FirebaseFirestoreSource firestoreSource;
    private final FirebaseRealtimeSource realtimeSource;
    private final FirebaseAnalytics analytics;
    @Inject
    public MatchRepositoryImpl(FirebaseFirestoreSource firestoreSource,
                               FirebaseRealtimeSource realtimeSource,
                               FirebaseAnalytics analytics) {
        this.firestoreSource = firestoreSource;
        this.realtimeSource = realtimeSource;
        this.analytics = analytics;
    }
    @Override
    public Task<String> challengeByUsername(String username) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String myUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        return firestoreSource.searchUserByUsername(username)
                .continueWithTask(searchTask -> {
                    if (!searchTask.isSuccessful() || searchTask.getResult() == null || searchTask.getResult().isEmpty()) {
                        return Tasks.forException(new Exception("Kullanıcı bulunamadı."));
                    }
                    DocumentSnapshot opponentDoc = searchTask.getResult().getDocuments().get(0);
                    String opponentUid = opponentDoc.getId();
                    String opponentUsername = opponentDoc.getString("username");
                    return createReadyMatch(myUid, myUsername, opponentUid, opponentUsername);
                });
    }
    @Override
    public Task<String> findRandomMatch() {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String myUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        return firestoreSource.getOpenWaitingMatches(1)
                .continueWithTask(searchTask -> {
                    if (searchTask.isSuccessful() && searchTask.getResult() != null && !searchTask.getResult().isEmpty()) {
                        DocumentSnapshot matchDoc = searchTask.getResult().getDocuments().get(0);
                        String matchId = matchDoc.getId();
                        String opponentUid = matchDoc.getString("player1Uid");
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("player2Uid", myUid);
                        updates.put("player2Username", myUsername);
                        updates.put("status", "READY");
                        updates.put("players", Arrays.asList(opponentUid, myUid));
                        return firestoreSource.updateMatch(matchId, updates)
                                .continueWith(updateTask -> {
                                    if (!updateTask.isSuccessful()) {
                                        throw updateTask.getException() != null ? updateTask.getException() : new Exception("Maça katılınamadı.");
                                    }
                                    realtimeSource.setMatchStatus(matchId, "READY");
                                    analytics.logEvent("match_random_joined", null);
                                    return matchId;
                                });
                    }
                    return createWaitingMatch(myUid, myUsername);
                });
    }
    private Task<String> createReadyMatch(String myUid, String myUsername, String opponentUid, String opponentUsername) {
        return firestoreSource.getRandomQuestionsFromPool(10)
                .continueWithTask(poolTask -> {
                    List<DailyTestDto.QuestionDto> questionDtos = mapPoolToQuestionDtos(poolTask);
                    MatchDto matchDto = new MatchDto();
                    matchDto.setPlayer1Uid(myUid);
                    matchDto.setPlayer1Username(myUsername);
                    matchDto.setPlayer2Uid(opponentUid);
                    matchDto.setPlayer2Username(opponentUsername);
                    matchDto.setPlayers(Arrays.asList(myUid, opponentUid));
                    matchDto.setStatus("READY");
                    matchDto.setQuestions(questionDtos);
                    matchDto.setScores(new HashMap<>());
                    matchDto.setCreatedAt(System.currentTimeMillis());
                    matchDto.setTimePerQuestion(45);
                    return firestoreSource.createMatch(matchDto)
                            .continueWith(createTask -> {
                                if (!createTask.isSuccessful() || createTask.getResult() == null) {
                                    throw createTask.getException() != null ? createTask.getException() : new Exception("Maç oluşturulamadı.");
                                }
                                String matchId = createTask.getResult().getId();
                                realtimeSource.setMatchStatus(matchId, "READY");
                                analytics.logEvent("match_challenge_created", null);
                                return matchId;
                            });
                });
    }
    private Task<String> createWaitingMatch(String myUid, String myUsername) {
        return firestoreSource.getRandomQuestionsFromPool(10)
                .continueWithTask(poolTask -> {
                    List<DailyTestDto.QuestionDto> questionDtos = mapPoolToQuestionDtos(poolTask);
                    MatchDto matchDto = new MatchDto();
                    matchDto.setPlayer1Uid(myUid);
                    matchDto.setPlayer1Username(myUsername);
                    matchDto.setPlayer2Uid("");
                    matchDto.setPlayers(Arrays.asList(myUid));
                    matchDto.setStatus("WAITING");
                    matchDto.setQuestions(questionDtos);
                    matchDto.setScores(new HashMap<>());
                    matchDto.setCreatedAt(System.currentTimeMillis());
                    matchDto.setTimePerQuestion(45);
                    return firestoreSource.createMatch(matchDto)
                            .continueWith(createTask -> {
                                if (!createTask.isSuccessful() || createTask.getResult() == null) {
                                    throw createTask.getException() != null ? createTask.getException() : new Exception("Maç oluşturulamadı.");
                                }
                                String matchId = createTask.getResult().getId();
                                realtimeSource.setMatchStatus(matchId, "WAITING");
                                analytics.logEvent("match_waiting_created", null);
                                return matchId;
                            });
                });
    }
    private List<DailyTestDto.QuestionDto> mapPoolToQuestionDtos(Task<com.google.firebase.firestore.QuerySnapshot> poolTask) {
        List<DailyTestDto.QuestionDto> questionDtos = new ArrayList<>();
        if (poolTask.isSuccessful() && poolTask.getResult() != null) {
            for (DocumentSnapshot doc : poolTask.getResult().getDocuments()) {
                DailyTestDto.QuestionDto qDto = doc.toObject(DailyTestDto.QuestionDto.class);
                if (qDto != null) {
                    if (qDto.getId() == null) qDto.setId(doc.getId());
                    questionDtos.add(qDto);
                }
            }
        }
        return questionDtos;
    }
    @Override
    public Task<Match> getMatch(String matchId) {
        return firestoreSource.getMatch(matchId).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                MatchDto dto = task.getResult().toObject(MatchDto.class);
                return mapDtoToMatch(matchId, dto);
            }
            return null;
        });
    }
    private Match mapDtoToMatch(String matchId, MatchDto dto) {
        if (dto == null) return null;
        Match match = new Match();
        match.setMatchId(matchId);
        match.setPlayer1Uid(dto.getPlayer1Uid());
        match.setPlayer1Username(dto.getPlayer1Username());
        match.setPlayer2Uid(dto.getPlayer2Uid());
        match.setPlayer2Username(dto.getPlayer2Username());
        try {
            match.setStatus(Match.MatchStatus.valueOf(dto.getStatus()));
        } catch (Exception e) {
            match.setStatus(Match.MatchStatus.WAITING);
        }
        List<Question> questions = new ArrayList<>();
        if (dto.getQuestions() != null) {
            for (DailyTestDto.QuestionDto qDto : dto.getQuestions()) {
                questions.add(new Question(qDto.getId(), qDto.getText(), qDto.getOptionA(), qDto.getOptionB(),
                        qDto.getOptionC(), qDto.getOptionD(), qDto.getCorrect(), qDto.getCategory(), qDto.getExplanation()));
            }
        }
        match.setQuestions(questions);
        match.setScores(dto.getScores());
        match.setWinnerUid(dto.getWinnerUid());
        match.setCreatedAt(dto.getCreatedAt());
        match.setStartedAt(dto.getStartedAt());
        match.setFinishedAt(dto.getFinishedAt());
        match.setTimePerQuestion(dto.getTimePerQuestion() > 0 ? dto.getTimePerQuestion() : 45);
        return match;
    }
    @Override
    public Task<Void> submitAnswer(String matchId, String uid, int score) {
        realtimeSource.updateMatchScore(matchId, uid, score);
        return Tasks.forResult(null);
    }
    @Override
    public Task<Void> completeMatch(String matchId, String uid, int finalScore) {
        return firestoreSource.getMatch(matchId).continueWithTask(getTask -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("scores." + uid, finalScore);
            MatchDto current = getTask.isSuccessful() && getTask.getResult() != null && getTask.getResult().exists()
                    ? getTask.getResult().toObject(MatchDto.class) : null;
            if (current != null) {
                String opponentUid = uid.equals(current.getPlayer1Uid()) ? current.getPlayer2Uid() : current.getPlayer1Uid();
                boolean opponentDone = current.getScores() != null && current.getScores().containsKey(opponentUid);
                if (opponentDone) {
                    int opponentScore = current.getScores().get(opponentUid);
                    String winnerUid = finalScore > opponentScore ? uid : (opponentScore > finalScore ? opponentUid : null);
                    updates.put("status", "COMPLETED");
                    updates.put("winnerUid", winnerUid);
                    updates.put("finishedAt", System.currentTimeMillis());
                    realtimeSource.setMatchStatus(matchId, "COMPLETED");
                }
            }
            realtimeSource.updateMatchScore(matchId, uid, finalScore);
            analytics.logEvent("match_completed", null);
            return firestoreSource.updateMatch(matchId, updates);
        });
    }
    @Override
    public void listenForOpponentJoin(String matchId, MatchStatusListener listener) {
        realtimeSource.listenMatchStatus(matchId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if (status != null) {
                    listener.onStatusChanged(status);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }
    @Override
    public void listenOpponentScore(String matchId, String opponentUid, ScoreListener listener) {
        realtimeSource.listenMatchScore(matchId, opponentUid, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Integer score = snapshot.getValue(Integer.class);
                listener.onScoreChanged(score != null ? score : 0);
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
    @Override
    public void stopListening(String matchId) {
        realtimeSource.removeMatchListeners(matchId);
    }
}
