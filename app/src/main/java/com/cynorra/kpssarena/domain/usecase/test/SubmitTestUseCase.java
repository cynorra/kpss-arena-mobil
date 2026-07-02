package com.cynorra.kpssarena.domain.usecase.test;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
public class SubmitTestUseCase {
    private final TestRepository testRepository;
    @Inject
    public SubmitTestUseCase(TestRepository testRepository) {
        this.testRepository = testRepository;

    }
    public Task<ScoreResult> execute(String uid, DailyTest test, List<Question> questions
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ne
        return testRepository.submitTest(today, uid, test, questions);
    }
    public ScoreResult calculateScore(List<Question> questions, int totalTimeUsed, int tot
        return testRepository.calculateScore(questions, totalTimeUsed, totalTimeAvailable)
    }
}
