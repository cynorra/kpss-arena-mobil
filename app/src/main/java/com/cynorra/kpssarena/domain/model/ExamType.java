package com.cynorra.kpssarena.domain.model;

import java.io.Serializable;

public enum ExamType implements Serializable {
    FULL_KPSS(
            "Tam KPSS Denemesi",
            "Genel Yetenek (60) + Genel Kültür (60)",
            120,
            130,
            new String[]{"Türkçe", "Matematik", "Tarih", "Coğrafya", "Vatandaşlık", "Güncel Bilgiler"},
            new int[]{30, 30, 27, 18, 9, 6}
    ),
    GY(
            "Genel Yetenek (GY)",
            "Türkçe (30) + Matematik (30)",
            60,
            65,
            new String[]{"Türkçe", "Matematik"},
            new int[]{30, 30}
    ),
    GK(
            "Genel Kültür (GK)",
            "Tarih (27) + Coğrafya (18) + Vatandaşlık (9) + Güncel (6)",
            60,
            65,
            new String[]{"Tarih", "Coğrafya", "Vatandaşlık", "Güncel Bilgiler"},
            new int[]{27, 18, 9, 6}
    ),
    BRANCH_TURKCE(
            "Türkçe Branş Denemesi",
            "Anlam Bilgisi, Dil Bilgisi & Sözel Mantık",
            30,
            35,
            new String[]{"Türkçe"},
            new int[]{30}
    ),
    BRANCH_MATEMATIK(
            "Matematik Branş Denemesi",
            "Temel Matematik, Problemler & Geometri",
            30,
            40,
            new String[]{"Matematik"},
            new int[]{30}
    ),
    BRANCH_TARIH(
            "Tarih Branş Denemesi",
            "İlk Devletler, Selçuklu, Osmanlı & İnkılap",
            27,
            25,
            new String[]{"Tarih"},
            new int[]{27}
    ),
    BRANCH_COGRAFYA(
            "Coğrafya Branş Denemesi",
            "Fiziki, Beşeri & Ekonomik Coğrafya",
            18,
            18,
            new String[]{"Coğrafya"},
            new int[]{18}
    ),
    BRANCH_VATANDASLIK(
            "Vatandaşlık & Güncel",
            "Anayasa, Hukuk, İdare & Güncel Olaylar",
            15,
            15,
            new String[]{"Vatandaşlık", "Güncel Bilgiler"},
            new int[]{9, 6}
    );

    private final String title;
    private final String subtitle;
    private final int totalQuestions;
    private final int durationMinutes;
    private final String[] categories;
    private final int[] questionCounts;

    ExamType(String title, String subtitle, int totalQuestions, int durationMinutes,
             String[] categories, int[] questionCounts) {
        this.title = title;
        this.subtitle = subtitle;
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
        this.categories = categories;
        this.questionCounts = questionCounts;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public long getDurationMs() {
        return (long) durationMinutes * 60 * 1000L;
    }

    public String[] getCategories() {
        return categories;
    }

    public int[] getQuestionCounts() {
        return questionCounts;
    }

    public static ExamType fromString(String name) {
        if (name == null || name.isEmpty()) return FULL_KPSS;
        try {
            return ExamType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return FULL_KPSS;
        }
    }
}
