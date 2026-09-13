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
                    cards = list != null ? list : new ArrayList<>();
                    currentIndex.setValue(0);
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
}
