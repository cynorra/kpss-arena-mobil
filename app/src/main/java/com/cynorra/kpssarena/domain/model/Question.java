package com.cynorra.kpssarena.domain.model;
public class Question {
    private String id;
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String category;
    private String explanation;
    private String userAnswer;
    private int timeRemaining;
    public Question() {}
    public Question(String id, String text, String optionA, String optionB,
                    String optionC, String optionD, String correctAnswer,
                    String category, String explanation) {
        this.id = id;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.explanation = explanation;
    }
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
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public int getTimeRemaining() { return timeRemaining; }
    public void setTimeRemaining(int timeRemaining) { this.timeRemaining = timeRemaining; }
    public boolean isCorrect() {
        return correctAnswer != null && correctAnswer.equals(userAnswer);
    }
    public boolean isAnswered() {
        return userAnswer != null && !userAnswer.isEmpty();
    }
}
