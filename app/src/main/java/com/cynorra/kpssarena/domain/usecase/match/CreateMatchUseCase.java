package com.cynorra.kpssarena.domain.usecase.match;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.google.android.gms.tasks.Task;
import javax.inject.Inject;
public class CreateMatchUseCase {
    private final MatchRepository matchRepository;
    @Inject
    public CreateMatchUseCase(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
    public Task<String> challengeByUsername(String username) {
        return matchRepository.challengeByUsername(username);
    }
    public Task<String> findRandomMatch() {
        return matchRepository.findRandomMatch();
    }
}
