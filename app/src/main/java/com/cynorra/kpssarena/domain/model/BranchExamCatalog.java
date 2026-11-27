package com.cynorra.kpssarena.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the available branch tests and allows dynamic expansion.
 * When new questions/tests are added, they are immediately reflected in the
 * catalog.
 * When new questions/tests are added (locally or remotely via Firestore),
 * they are immediately reflected in the catalog.
 */
public class BranchExamCatalog {

    public static final int DEFAULT_TEST_COUNT = 5;

    private static final Map<ExamType, Integer> TEST_COUNTS = new HashMap<>();
    private static final List<BranchExam> CUSTOM_EXAMS = new ArrayList<>();

    static {
        TEST_COUNTS.put(ExamType.BRANCH_TURKCE, 5);
        TEST_COUNTS.put(ExamType.BRANCH_MATEMATIK, 5);
        TEST_COUNTS.put(ExamType.BRANCH_TARIH, 5);
        TEST_COUNTS.put(ExamType.BRANCH_COGRAFYA, 5);
        TEST_COUNTS.put(ExamType.BRANCH_VATANDASLIK, 5);
    }

    public static int getTestCount(ExamType examType) {
        if (examType == null)
            return DEFAULT_TEST_COUNT;
        Integer count = TEST_COUNTS.get(examType);
        return count != null ? count : DEFAULT_TEST_COUNT;
    }

    public static void setTestCount(ExamType examType, int count) {
        if (examType != null && count > 0) {
            TEST_COUNTS.put(examType, count);
        }
    }

    public static synchronized void registerRemoteExams(List<BranchExam> remoteList) {
        if (remoteList == null || remoteList.isEmpty())
            return;
        for (BranchExam remote : remoteList) {
            if (!containsExam(remote.getId())) {
                CUSTOM_EXAMS.add(remote);
            }
        }
    }

    public static synchronized boolean containsExam(String id) {
        if (id == null)
            return false;
        for (BranchExam exam : CUSTOM_EXAMS) {
            if (id.equals(exam.getId()))
                return true;
        }
        return false;
    }

    public static List<BranchExam> getAllBranchExams() {
        List<BranchExam> all = new ArrayList<>();
        all.addAll(getExamsForType(ExamType.BRANCH_TURKCE));
        all.addAll(getExamsForType(ExamType.BRANCH_MATEMATIK));
        all.addAll(getExamsForType(ExamType.BRANCH_TARIH));
        all.addAll(getExamsForType(ExamType.BRANCH_COGRAFYA));
        all.addAll(getExamsForType(ExamType.BRANCH_VATANDASLIK));
        synchronized (CUSTOM_EXAMS) {
            for (BranchExam custom : CUSTOM_EXAMS) {
                if (!containsInList(all, custom.getId())) {
                    all.add(custom);
                }
            }
        }
        return all;
    }

    public static List<BranchExam> getExamsForType(ExamType examType) {
        return getExamsForType(examType, getTestCount(examType));
    }

    public static List<BranchExam> getExamsForType(ExamType examType, int count) {
        List<BranchExam> list = new ArrayList<>();
        if (examType == null)
            return list;

        for (int i = 1; i <= count; i++) {
            String id = examType.name() + "_" + i;
            String title = examType.getTitle() + " #" + i;
            String subtitle = examType.getTotalQuestions() + " Soru • " + examType.getDurationMinutes() + " Dk";
            list.add(new BranchExam(
                    id,
                    examType,
                    i,
                    title,
                    subtitle,
                    examType.getTotalQuestions(),
                    examType.getDurationMinutes()));
        }

        // Append any registered custom/remote exams matching this type
        synchronized (CUSTOM_EXAMS) {
            for (BranchExam custom : CUSTOM_EXAMS) {
                if (custom.getExamType() == examType && !containsInList(list, custom.getId())) {
                    list.add(custom);
                }
            }
        }

        return list;
    }

    private static boolean containsInList(List<BranchExam> list, String id) {
        if (id == null || list == null)
            return false;
        for (BranchExam e : list) {
            if (id.equals(e.getId()))
                return true;
        }
        return false;
    }
}
