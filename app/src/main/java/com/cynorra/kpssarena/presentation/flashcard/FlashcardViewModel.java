package com.cynorra.kpssarena.presentation.flashcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FlashcardViewModel extends BaseViewModel {
    private static final int CARD_COUNT = 30;
    private final TestRepository testRepository;
    private List<Question> cards = new ArrayList<>();
    private final MutableLiveData<Question> currentCard = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> answerShown = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> masteredCount = new MutableLiveData<>(0);

    @Inject
    public FlashcardViewModel(TestRepository testRepository) {
        this.testRepository = testRepository;
        loadCards(null);
    }

    public void loadCards(String category) {
        setLoading(true);
        testRepository.getPracticeQuestions(category, CARD_COUNT)
                .addOnSuccessListener(list -> {
                    setLoading(false);
                    cards = list != null ? new ArrayList<>(list) : new ArrayList<>();
                    currentIndex.setValue(0);
                    masteredCount.setValue(0);
                    answerShown.setValue(false);
                    currentCard.setValue(cards.isEmpty() ? null : cards.get(0));
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    cards = new ArrayList<>();
                    currentCard.setValue(null);
                });
    }

    public void showAnswer() {
        answerShown.setValue(true);
    }

    /** Spaced Repetition: user mastered this card -> move forward & increment mastered counter. */
    public void markKnown() {
        Integer m = masteredCount.getValue();
        masteredCount.setValue((m != null ? m : 0) + 1);
        nextCard();
    }

    /** Spaced Repetition: user wants to review again -> re-queue card 3 positions ahead! */
    public void markRepeat() {
        if (cards.size() > 1) {
            Integer idx = currentIndex.getValue();
            int cur = idx != null ? idx : 0;
            if (cur >= 0 && cur < cards.size()) {
                Question q = cards.get(cur);
                int insertAt = Math.min(cards.size(), cur + 4);
                cards.add(insertAt, q);
            }
        }
        nextCard();
    }

    public void nextCard() {
        if (cards.isEmpty()) return;
        Integer idx = currentIndex.getValue();
        int next = ((idx != null ? idx : 0) + 1) % cards.size();
        currentIndex.setValue(next);
        answerShown.setValue(false);
        currentCard.setValue(cards.get(next));
    }

    public int getTotalCards() { return cards.size(); }
    public LiveData<Question> getCurrentCard() { return currentCard; }
    public LiveData<Integer> getCurrentIndex() { return currentIndex; }
    public LiveData<Boolean> getAnswerShown() { return answerShown; }
    public LiveData<Integer> getMasteredCount() { return masteredCount; }
}
