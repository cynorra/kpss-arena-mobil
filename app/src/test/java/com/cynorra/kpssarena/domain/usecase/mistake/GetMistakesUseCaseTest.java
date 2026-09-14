package com.cynorra.kpssarena.domain.usecase.mistake;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cynorra.kpssarena.domain.model.Mistake;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class GetMistakesUseCaseTest {

    private MistakeRepository mistakeRepository;
    private GetMistakesUseCase useCase;

    @Before
    public void setUp() {
        mistakeRepository = mock(MistakeRepository.class);
        useCase = new GetMistakesUseCase(mistakeRepository);
    }

    @Test
    public void getAll_delegatesToRepository_andReturnsSameTask() {
        List<Mistake> mistakes = Collections.singletonList(mock(Mistake.class));
        Task<List<Mistake>> expected = Tasks.forResult(mistakes);
        when(mistakeRepository.getAllMistakes()).thenReturn(expected);

        Task<List<Mistake>> actual = useCase.getAll();

        verify(mistakeRepository).getAllMistakes();
        assertSame(expected, actual);
    }

    @Test
    public void getByCategory_delegatesToRepository_withGivenCategory() {
        List<Mistake> mistakes = Collections.singletonList(mock(Mistake.class));
        Task<List<Mistake>> expected = Tasks.forResult(mistakes);
        when(mistakeRepository.getMistakesByCategory("Turkce")).thenReturn(expected);

        Task<List<Mistake>> actual = useCase.getByCategory("Turkce");

        verify(mistakeRepository).getMistakesByCategory("Turkce");
        assertSame(expected, actual);
    }
}
