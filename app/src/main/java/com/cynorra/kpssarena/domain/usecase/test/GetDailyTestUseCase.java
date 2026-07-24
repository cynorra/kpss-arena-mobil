package com.cynorra.kpssarena.domain.usecase.test;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;
public class GetDailyTestUseCase {
    private final TestRepository testRepository;
    @Inject
    public GetDailyTestUseCase(TestRepository testRepository) {
        this.testRepository = testRepository;
    }
    public Task<DailyTest> execute() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        return testRepository.getDailyTest(today);
    }
    public Task<DailyTest> execute(String date) {
        return testRepository.getDailyTest(date);
    }
    public Task<Boolean> hasCompletedToday(String uid) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return testRepository.hasCompletedTodayTest(today, uid);
    }
}
