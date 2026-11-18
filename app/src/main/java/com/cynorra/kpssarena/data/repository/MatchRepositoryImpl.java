package com.cynorra.kpssarena.data.repository;
import android.content.Context;
import com.cynorra.kpssarena.R;
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
import java.util.concurrent.Executor;
import javax.inject.Inject;
import dagger.hilt.android.qualifiers.ApplicationContext;
public class MatchRepositoryImpl implements MatchRepository {
    private static final Executor DIRECT_EXECUTOR = Runnable::run;
    private final FirebaseFirestoreSource firestoreSource;
    private final FirebaseRealtimeSource realtimeSource;
    private final FirebaseAnalytics analytics;
    private final Context context;
    @Inject
    public MatchRepositoryImpl(FirebaseFirestoreSource firestoreSource,
                               FirebaseRealtimeSource realtimeSource,
                               FirebaseAnalytics analytics,
                               @ApplicationContext Context context) {
        this.firestoreSource = firestoreSource;
        this.realtimeSource = realtimeSource;
        this.analytics = analytics;
        this.context = context;
    }
    @Override
    public Task<String> challengeByUsername(String username) {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception(context.getString(R.string.error_no_session)));
        }
        String myUid = currentUser.getUid();
        return resolveCurrentUsername().continueWithTask(nameTask -> {
            String myUsername = nameTask.getResult();
            return firestoreSource.searchUserByUsername(username)
                    .continueWithTask(searchTask -> {
                        if (!searchTask.isSuccessful() || searchTask.getResult() == null || searchTask.getResult().isEmpty()) {
                            return Tasks.forException(new Exception(context.getString(R.string.error_user_not_found)));
                        }
                        DocumentSnapshot opponentDoc = searchTask.getResult().getDocuments().get(0);
                        String opponentUid = opponentDoc.getId();
                        if (opponentUid.equals(myUid)) {
                            return Tasks.forException(new Exception(context.getString(R.string.error_cannot_challenge_self)));
                        }
                        String opponentUsername = opponentDoc.getString("username");
                        return createReadyMatch(myUid, myUsername, opponentUid, opponentUsername);
                    });
        });
    }
    @Override
    public Task<String> findRandomMatch() {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception(context.getString(R.string.error_no_session)));
        }
        String myUid = currentUser.getUid();
        return resolveCurrentUsername().continueWithTask(nameTask -> {
            String myUsername = nameTask.getResult();
            return firestoreSource.getOpenWaitingMatches(10)
                    .continueWithTask(searchTask -> {
                        DocumentSnapshot matchDoc = null;
                        if (searchTask.isSuccessful() && searchTask.getResult() != null) {
                            // only join LIVE matches, never join our own waiting match
                            for (DocumentSnapshot candidate : searchTask.getResult().getDocuments()) {
                                String type = candidate.getString("matchType");
                                if (!myUid.equals(candidate.getString("player1Uid")) && (type == null || "LIVE".equals(type))) {
                                    matchDoc = candidate;
                                    break;
                                }
                            }
                        }
                        if (matchDoc != null) {
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
                                            throw updateTask.getException() != null ? updateTask.getException() : new Exception(context.getString(R.string.error_match_join_failed));
                                        }
                                        realtimeSource.joinMatchAsPlayer2(matchId, myUid);
                                        analytics.logEvent("match_random_joined", null);
                                        return matchId;
                                    });
                        }
                        return createWaitingMatch(myUid, myUsername);
                    });
        });
    }
    private Task<String> createReadyMatch(String myUid, String myUsername, String opponentUid, String opponentUsername) {
        return firestoreSource.getRandomQuestionsFromPool(10)
                .continueWithTask(poolTask -> {
                    List<DailyTestDto.QuestionDto> questionDtos = mapPoolToQuestionDtos(poolTask);
                    MatchDto matchDto = new MatchDto();
                    matchDto.setRoomTitle(context.getString(R.string.match_room_title, myUsername));
                    matchDto.setMatchType("LIVE");
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
                                    throw createTask.getException() != null ? createTask.getException() : new Exception(context.getString(R.string.error_match_create_failed));
                                }
                                String matchId = createTask.getResult().getId();
                                realtimeSource.initMatchParticipants(matchId, myUid, opponentUid, "READY");
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
                    matchDto.setRoomTitle(context.getString(R.string.match_room_title, myUsername));
                    matchDto.setMatchType("LIVE");
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
                                    throw createTask.getException() != null ? createTask.getException() : new Exception(context.getString(R.string.error_match_create_failed));
                                }
                                String matchId = createTask.getResult().getId();
                                realtimeSource.initMatchParticipants(matchId, myUid, null, "WAITING");
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
    @Override
    public Task<List<Match>> getUserActiveMatches(String uid) {
        return firestoreSource.getUserMatches(uid)
                .continueWith(DIRECT_EXECUTOR, task -> {
                    List<Match> activeMatches = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            MatchDto dto = doc.toObject(MatchDto.class);
                            Match match = mapDtoToMatch(doc.getId(), dto);
                            if (match != null
                                    && match.getStatus() != Match.MatchStatus.COMPLETED
                                    && match.getStatus() != Match.MatchStatus.CANCELLED) {
                                activeMatches.add(match);
                            }
                        }
                    }
                    return activeMatches;
                });
    }
    @Override
    public Task<List<Match>> getUserMatchHistory(String uid) {
        return firestoreSource.getUserMatches(uid)
                .continueWith(DIRECT_EXECUTOR, task -> {
                    List<Match> history = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Match match = mapDtoToMatch(doc.getId(), doc.toObject(MatchDto.class));
                            if (match != null && match.getStatus() == Match.MatchStatus.COMPLETED) {
                                history.add(match);
                            }
                        }
                    }
                    return history;
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
        match.setRoomTitle(dto.getRoomTitle());
        match.setMatchType(dto.getMatchType() != null ? dto.getMatchType() : "LIVE");
        match.setExamType(dto.getExamType());
        match.setExamTitle(dto.getExamTitle());
        match.setPlayer1Net(dto.getPlayer1Net());
        match.setPlayer1Correct(dto.getPlayer1Correct());
        match.setPlayer1Wrong(dto.getPlayer1Wrong());
        match.setPlayer1Empty(dto.getPlayer1Empty());
        match.setPlayer1DurationSeconds(dto.getPlayer1DurationSeconds());
        match.setPlayer2Net(dto.getPlayer2Net());
        match.setPlayer2Correct(dto.getPlayer2Correct());
        match.setPlayer2Wrong(dto.getPlayer2Wrong());
        match.setPlayer2Empty(dto.getPlayer2Empty());
        match.setPlayer2DurationSeconds(dto.getPlayer2DurationSeconds());
        return match;
    }
    @Override
    public Task<Void> submitAnswer(String matchId, String uid, int score) {
        return realtimeSource.updateMatchScore(matchId, uid, score);
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
                    if (uid.equals(winnerUid)) {
                        String myUsername = uid.equals(current.getPlayer1Uid()) ? current.getPlayer1Username() : current.getPlayer2Username();
                        String opponentUsername = uid.equals(current.getPlayer1Uid()) ? current.getPlayer2Username() : current.getPlayer1Username();
                        postMatchWinFeedItem(uid, myUsername, opponentUsername);
                    }
                }
            }
            realtimeSource.updateMatchScore(matchId, uid, finalScore);
            analytics.logEvent("match_completed", null);
            return firestoreSource.updateMatch(matchId, updates);
        });
    }
    private void postMatchWinFeedItem(String uid, String myUsername, String opponentUsername) {
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        com.cynorra.kpssarena.data.remote.model.FeedPostDto dto = new com.cynorra.kpssarena.data.remote.model.FeedPostDto();
        dto.setUid(uid);
        dto.setUsername(myUsername);
        dto.setPhotoUrl(user != null && user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        dto.setType(com.cynorra.kpssarena.domain.model.FeedPost.TYPE_MATCH_WIN);
        dto.setOpponentUsername(opponentUsername);
        dto.setCreatedAt(System.currentTimeMillis());
        firestoreSource.createFeedPost(dto);
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

    @Override
    public Task<List<Match>> getOpenWaitingMatches() {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = currentUser != null ? currentUser.getUid() : "";
        return firestoreSource.getOpenWaitingMatches(20)
                .continueWith(DIRECT_EXECUTOR, task -> {
                    List<Match> list = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            MatchDto dto = doc.toObject(MatchDto.class);
                            if (dto != null && !myUid.equals(dto.getPlayer1Uid())) {
                                list.add(mapDtoToMatch(doc.getId(), dto));
                            }
                        }
                    }
                    return list;
                });
    }

    @Override
    public Task<String> createExamDuel(String examType, com.cynorra.kpssarena.domain.model.ScoreResult result) {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception(context.getString(R.string.error_no_session)));
        }
        String myUid = currentUser.getUid();
        return resolveCurrentUsername().continueWithTask(nameTask -> {
            String myUsername = nameTask.getResult();
            com.cynorra.kpssarena.domain.model.ExamType ex = com.cynorra.kpssarena.domain.model.ExamType.fromString(examType);
            String exTitle = ex.getTitle();
            String roomTitle = context.getString(R.string.match_exam_duel_title, myUsername, exTitle);

            MatchDto matchDto = new MatchDto();
            matchDto.setRoomTitle(roomTitle);
            matchDto.setMatchType("ASYNC_EXAM");
            matchDto.setExamType(ex.name());
            matchDto.setExamTitle(exTitle);
            matchDto.setPlayer1Uid(myUid);
            matchDto.setPlayer1Username(myUsername);
            matchDto.setPlayer2Uid("");
            matchDto.setPlayer2Username("");
            matchDto.setPlayers(Arrays.asList(myUid));
            matchDto.setStatus("WAITING");
            matchDto.setCreatedAt(System.currentTimeMillis());
            matchDto.setPlayer1Net(result.getNetScore());
            matchDto.setPlayer1Correct(result.getCorrectCount());
            matchDto.setPlayer1Wrong(result.getWrongCount());
            matchDto.setPlayer1Empty(result.getEmptyCount());
            matchDto.setPlayer1DurationSeconds(result.getTotalTimeUsed() / 1000);
            matchDto.setTimePerQuestion(0);
            Map<String, Integer> scores = new HashMap<>();
            scores.put(myUid, result.getTotalScore());
            matchDto.setScores(scores);

            return firestoreSource.createMatch(matchDto)
                    .continueWith(task -> {
                        if (!task.isSuccessful() || task.getResult() == null) {
                            throw task.getException() != null ? task.getException() : new Exception(context.getString(R.string.error_match_create_failed));
                        }
                        String matchId = task.getResult().getId();
                        analytics.logEvent("match_exam_duel_created", null);
                        return matchId;
                    });
        });
    }

    @Override
    public Task<Match> acceptExamDuel(String matchId, com.cynorra.kpssarena.domain.model.ScoreResult opponentResult) {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception(context.getString(R.string.error_no_session)));
        }
        String myUid = currentUser.getUid();
        return resolveCurrentUsername().continueWithTask(nameTask -> {
            String myUsername = nameTask.getResult();
            return firestoreSource.getMatch(matchId).continueWithTask(getTask -> {
                if (!getTask.isSuccessful() || getTask.getResult() == null || !getTask.getResult().exists()) {
                    return Tasks.forException(new Exception("Meydan okuma bulunamadı"));
                }
                MatchDto current = getTask.getResult().toObject(MatchDto.class);
                if (current == null) {
                    return Tasks.forException(new Exception("Meydan okuma verisi geçersiz"));
                }
                if (!"WAITING".equals(current.getStatus())) {
                    return Tasks.forException(new Exception("Bu meydan okuma artık müsait değil"));
                }
                if (myUid.equals(current.getPlayer1Uid())) {
                    return Tasks.forException(new Exception(context.getString(R.string.error_cannot_challenge_self)));
                }

                double p1Net = current.getPlayer1Net();
                double p2Net = opponentResult.getNetScore();
                int p1Time = current.getPlayer1DurationSeconds();
                int p2Time = opponentResult.getTotalTimeUsed() / 1000;

                String winnerUid;
                if (p1Net > p2Net) {
                    winnerUid = current.getPlayer1Uid();
                } else if (p2Net > p1Net) {
                    winnerUid = myUid;
                } else {
                    if (p1Time < p2Time && p1Time > 0) {
                        winnerUid = current.getPlayer1Uid();
                    } else if (p2Time < p1Time && p2Time > 0) {
                        winnerUid = myUid;
                    } else {
                        winnerUid = null;
                    }
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("player2Uid", myUid);
                updates.put("player2Username", myUsername);
                updates.put("player2Net", p2Net);
                updates.put("player2Correct", opponentResult.getCorrectCount());
                updates.put("player2Wrong", opponentResult.getWrongCount());
                updates.put("player2Empty", opponentResult.getEmptyCount());
                updates.put("player2DurationSeconds", p2Time);
                updates.put("players", Arrays.asList(current.getPlayer1Uid(), myUid));
                updates.put("status", "COMPLETED");
                updates.put("winnerUid", winnerUid);
                updates.put("finishedAt", System.currentTimeMillis());
                updates.put("scores." + myUid, opponentResult.getTotalScore());

                return firestoreSource.updateMatch(matchId, updates).continueWithTask(uTask -> {
                    if (winnerUid != null && winnerUid.equals(myUid)) {
                        postMatchWinFeedItem(myUid, myUsername, current.getPlayer1Username());
                    } else if (winnerUid != null && winnerUid.equals(current.getPlayer1Uid())) {
                        postMatchWinFeedItem(current.getPlayer1Uid(), current.getPlayer1Username(), myUsername);
                    }
                    analytics.logEvent("match_exam_duel_completed", null);
                    return getMatch(matchId);
                });
            });
        });
    }

    @Override
    public Task<Void> cancelMatch(String matchId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "CANCELLED");
        return firestoreSource.updateMatch(matchId, updates);
    }

    @Override
    public Task<String> resolveCurrentUsername() {
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return Tasks.forResult("KPSS Adayı");
        }
        if (user.getDisplayName() != null && !user.getDisplayName().trim().isEmpty()) {
            return Tasks.forResult(user.getDisplayName().trim());
        }
        String uid = user.getUid();
        return firestoreSource.getUserProfile(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                String username = task.getResult().getString("username");
                if (username != null && !username.trim().isEmpty()) {
                    return username.trim();
                }
            }
            if (user.getEmail() != null && user.getEmail().contains("@")) {
                return user.getEmail().substring(0, user.getEmail().indexOf('@'));
            }
            return "KPSS Adayı #" + (uid.length() > 4 ? uid.substring(0, 4).toUpperCase() : uid);
        });
    }
}
