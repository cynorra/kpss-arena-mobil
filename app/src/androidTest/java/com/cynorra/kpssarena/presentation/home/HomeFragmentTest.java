package com.cynorra.kpssarena;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void testDailyTestCard_isDisplayed() {
        onView(withId(R.id.card_daily_test))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testStartTestButton_isDisplayed() {
        onView(withId(R.id.btn_start_test))
                .check(matches(isDisplayed()))
                .check(matches(withText("Teste Başla")));
    }
    @Test
    public void testStreakCard_isDisplayed() {

        onView(withId(R.id.card_streak))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testMiniLeaderboard_isDisplayed() {
        onView(withId(R.id.rv_mini_leaderboard))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testBottomNavigation_isDisplayed() {
        onView(withId(R.id.bottom_navigation))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.homeFragment))
                .check(matches(isDisplayed()));
        onView(withId(R.id.leaderboardFragment))
                .check(matches(isDisplayed()));
        onView(withId(R.id.arenaFragment))
                .check(matches(isDisplayed()));
        onView(withId(R.id.profileFragment))
                .check(matches(isDisplayed()));
    }
}
