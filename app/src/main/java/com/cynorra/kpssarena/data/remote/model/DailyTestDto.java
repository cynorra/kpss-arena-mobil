package com.cynorra.kpssarena.data.remote.model;
import java.util.List;
public class DailyTestDto {
    private List<QuestionDto> questions;
    private boolean active;
    private long publishedAt;
    public DailyTestDto() {}
    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public long getPublishedAt() { return publishedAt; }
    public void setPublishedAt(long publishedAt) { this.publishedAt = publishedAt; }
    public static class QuestionDto {
        private String id;
        private String text;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correct;
        private String category;
        private String explanation;
        public QuestionDto() {}
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getOptionA() { return optionA; }
        public void setOptionA(String optionA) { this.optionA = optionA; }
        public String getOptionB() { return optionB; }
        public void setOptionB(String optionB) { this.optionB = optionB; }
        public String getOptionC() { return optionC; }
        public void setOptionC(String optionC) { this.optionC = optionC; }
        public String getOptionD() { return optionD; }
        public void setOptionD(String optionD) { this.optionD = optionD; }
        public String getCorrect() { return correct; }
        public void setCorrect(String correct) { this.correct = correct; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }
}
