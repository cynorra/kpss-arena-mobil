package com.cynorra.kpssarena.data.local.entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "mistakes")
public class MistakeEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "question_id")
    private String questionId;
    @ColumnInfo(name = "question_text")
    private String questionText;
    @ColumnInfo(name = "option_a")
    private String optionA;
    @ColumnInfo(name = "option_b")
    private String optionB;

    @ColumnInfo(name = "option_c")
    private String optionC;
    @ColumnInfo(name = "option_d")
    private String optionD;
    @ColumnInfo(name = "correct_answer")
    private String correctAnswer;
    @ColumnInfo(name = "user_answer")
    private String userAnswer;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "explanation")
    private String explanation;
    @ColumnInfo(name = "test_date")
    private String testDate;
    @ColumnInfo(name = "is_retried")
    private boolean isRetried;
    @ColumnInfo(name = "retry_correct")
    private boolean retryCorrect;
    @ColumnInfo(name = "created_at")
    private long createdAt;
    // Constructors
    public MistakeEntity() {}
    public MistakeEntity(String questionId, String questionText, String optionA,
                         String optionB, String optionC, String optionD,
                         String correctAnswer, String userAnswer, String category,
                         String explanation, String testDate) {
        this.questionId = questionId;
        this.questionText = questionText;

        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
        this.category = category;
        this.explanation = explanation;
        this.testDate = testDate;
        this.isRetried = false;
        this.retryCorrect = false;
        this.createdAt = System.currentTimeMillis();
    }
    // Getters and Setters
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
