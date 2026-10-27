package com.cynorra.kpssarena.domain.model;
public class FeedPost {
    public static final String TYPE_TEST_RESULT = "test_result";
    public static final String TYPE_MATCH_WIN = "match_win";
    public static final String TYPE_STREAK_MILESTONE = "streak_milestone";
    private String postId;
    private String uid;
    private String username;
    private String photoUrl;
    private String type;
    private int score;
    private int secondaryValue;
    private String opponentUsername;
    private long createdAt;
    private int likeCount;
    private boolean likedByMe;
    public FeedPost() {}
    public FeedPost copy() {
        FeedPost c = new FeedPost();
        c.postId = postId;
        c.uid = uid;
        c.username = username;
        c.photoUrl = photoUrl;
        c.type = type;
        c.score = score;
        c.secondaryValue = secondaryValue;
        c.opponentUsername = opponentUsername;
        c.createdAt = createdAt;
        c.likeCount = likeCount;
        c.likedByMe = likedByMe;
        return c;
    }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getSecondaryValue() { return secondaryValue; }
    public void setSecondaryValue(int secondaryValue) { this.secondaryValue = secondaryValue; }
    public String getOpponentUsername() { return opponentUsername; }
    public void setOpponentUsername(String opponentUsername) { this.opponentUsername = opponentUsername; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }
}
