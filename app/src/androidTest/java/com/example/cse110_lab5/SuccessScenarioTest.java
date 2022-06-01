package com.example.cse110_lab5;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.exhibitlist.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Comprehensive system test. Goes through each essential user story to ensure basic app functionality.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SuccessScenarioTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void success_Scenario_Test() {
        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.selected),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.exhibit_list),
                                        0),
                                1),
                        isDisplayed()));
        materialCheckBox.perform(click());

        ViewInteraction materialCheckBox2 = onView(
                allOf(withId(R.id.selected),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.exhibit_list),
                                        3),
                                1),
                        isDisplayed()));
        materialCheckBox2.perform(click());

        ViewInteraction materialCheckBox3 = onView(
                allOf(withId(R.id.selected),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.exhibit_list),
                                        5),
                                1),
                        isDisplayed()));
        materialCheckBox3.perform(click());

        ViewInteraction materialCheckBox4 = onView(
                allOf(withId(R.id.selected),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.exhibit_list),
                                        6),
                                1),
                        isDisplayed()));
        materialCheckBox4.perform(click());

        ViewInteraction materialCheckBox5 = onView(
                allOf(withId(R.id.selected),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.exhibit_list),
                                        2),
                                1),
                        isDisplayed()));
        materialCheckBox5.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.plan_bttn), withText("Plan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.exhibit_planned), withText("Flamingos"),
                        withParent(withParent(withId(R.id.planned_exhibits))),
                        isDisplayed()));
        textView.check(matches(withText("Flamingos")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.total), withText("Total: 5"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView2.check(matches(withText("Total: 5")));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.nav_bttn), withText("Navigate"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.directionsSwitch), withText("Detailed View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        switch_.perform(click());

        ViewInteraction switch_2 = onView(
                allOf(withId(R.id.directionsSwitch), withText("Detailed View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        switch_2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction switch_3 = onView(
                allOf(withId(R.id.directionsSwitch), withText("Detailed View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        switch_3.perform(click());

        ViewInteraction switch_4 = onView(
                allOf(withId(R.id.directionsSwitch), withText("Detailed View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        switch_4.perform(click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton7.perform(click());

        ViewInteraction materialButton8 = onView(
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton8.perform(click());

        ViewInteraction materialButton9 = onView(
                allOf(withId(R.id.clear_plan), withText("Clear"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                10),
                        isDisplayed()));
        materialButton9.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.compact_list), withText("Selected Exhibits: \n"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView3.check(matches(withText("Selected Exhibits: \n")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
