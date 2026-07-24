package com.cynorra.kpssarena.domain.model;
public class Mistake {
    private String questionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String userAnswer;
    private String category;
    private String explanation;
    private String testDate;
    private boolean isRetried;
    private boolean retryCorrect;
    private long createdAt;
    public Mistake() {}
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getQuestionText() { return questionText; }

    public void setQuestionText(String questionText) { this.questionText = questionText; }
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
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getTestDate() { return testDate; }
    public void setTestDate(String testDate) { this.testDate = testDate; }
    public boolean isRetried() { return isRetried; }
    public void setRetried(boolean retried) { isRetried = retried; }
    public boolean isRetryCorrect() { return retryCorrect; }
    public void setRetryCorrect(boolean retryCorrect) { this.retryCorrect = retryCorrect; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
