package com.cynorra.kpssarena.domain.model;
public class Badge {
    public enum BadgeType {
        STREAK_7("7 Gün Serisi", "Arka arkaya 7 gün test çözdün! 🔥", "badge_streak_7"),
        STREAK_30("1 Ay Serisi", "30 gün kesintisiz test! 🏆", "badge_streak_30"),
        FIRST_TOP_10("İlk Top 10", "İlk kez ilk 10'a girdin! ⭐", "badge_top10"),
        TOP_3("Zirveye Yolculuk", "İlk 3'te yer aldın! 🥉", "badge_top3"),
        TOP_1("Şampiyon", "1. oldun! 👑", "badge_top1"),
        HUNDRED_TESTS("100 Test", "100 test tamamladın! 💯", "badge_100"),
        PERFECT_TEST("Kusursuz Test", "Tüm soruları doğru bildin! ✨", "badge_perfect"),
        SPEED_DEMON("Hız Canavarı", "Testi 5 dakikadan kısa sürede bitirdin! ⚡", "badge_speed_demon"),
        FIRST_MATCH_WIN("İlk Zafer", "İlk 1v1 maçını kazandın! ⚔️", "badge_first_win"),
        MATCH_MASTER("Arena Ustası", "10 1v1 maç kazandın! 🛡️", "badge_match_master"),
        PREMIUM("Premium Üye", "Premium'a hoş geldin! 💎", "badge_premium");
        private final String displayName;
        private final String description;
        private final String iconAsset;
        BadgeType(String displayName, String description, String iconAsset) {
            this.displayName = displayName;
            this.description = description;
            this.iconAsset = iconAsset;
        }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public String getIconAsset() { return iconAsset; }
    }
    private String badgeId;
    private String displayName;
    private String description;
    private String iconAsset;
    private long awardedAt;
    private boolean isUnlocked;

    public Badge() {}
    public Badge(BadgeType badgeType, long awardedAt) {
        this.badgeId = badgeType.name();
        this.displayName = badgeType.getDisplayName();
        this.description = badgeType.getDescription();
        this.iconAsset = badgeType.getIconAsset();
        this.awardedAt = awardedAt;
        this.isUnlocked = true;
    }
    public String getBadgeId() { return badgeId; }
    public void setBadgeId(String badgeId) { this.badgeId = badgeId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconAsset() { return iconAsset; }
    public void setIconAsset(String iconAsset) { this.iconAsset = iconAsset; }
    public long getAwardedAt() { return awardedAt; }
    public void setAwardedAt(long awardedAt) { this.awardedAt = awardedAt; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
