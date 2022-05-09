package com.example.cse110_lab5;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SuccessScenarioTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void successScenarioTest() {
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
                                        2),
                                1),
                        isDisplayed()));
        materialCheckBox2.perform(click());

        ViewInteraction materialCheckBox3 = onView(
                allOf(withId(R.id.selected),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.exhibit_list),
                                        3),
                                1),
                        isDisplayed()));
        materialCheckBox3.perform(click());

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
                allOf(withId(R.id.exhibit_planned), withText("Gorillas"),
                        withParent(withParent(withId(R.id.planned_exhibits))),
                        isDisplayed()));
        textView.check(matches(withText("Gorillas")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.exhibit_planned), withText("Lions"),
                        withParent(withParent(withId(R.id.planned_exhibits))),
                        isDisplayed()));
        textView2.check(matches(withText("Lions")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.exhibit_planned), withText("Elephant Odyssey"),
                        withParent(withParent(withId(R.id.planned_exhibits))),
                        isDisplayed()));
        textView3.check(matches(withText("Elephant Odyssey")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.total), withText("Total: 3"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView4.check(matches(withText("Total: 3")));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.nav_bttn), withText("Navigate"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.Exhibit_Name), withText("Gorillas"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView5.check(matches(withText("Gorillas")));

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
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.next_bttn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.Exhibit_Name), withText("Entrance and Exit Gate"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView6.check(matches(withText("Entrance and Exit Gate")));
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
