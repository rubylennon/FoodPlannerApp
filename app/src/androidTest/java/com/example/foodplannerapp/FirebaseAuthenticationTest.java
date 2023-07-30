package com.example.foodplannerapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.foodplannerapp.activities.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FirebaseAuthenticationTest {
    String email,
            password;

    private String getResource(int id) {
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();

        return targetContext.getString(id);
    }

    @Before
    public void setUp(){
        email = getResource(R.string.emailTwoTest);
        password = getResource(R.string.passwordTest);
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void test1_newUserTest() {
        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.idTVRegister), withText("New User? Register Here"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.idEdtUserName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILUserName),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.idEdtPwd),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPassword),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.idEdtCnfPwd),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILCnfPwd),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.idBtnRegister), withText("Register"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        materialButton.perform(click());

        SystemClock.sleep(2000);

        ViewInteraction textView = onView(
                allOf(withText("My Recipes"), isDisplayed()));
        textView.check(matches(withText("My Recipes")));

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(androidx.core.R.id.title), withText("Edit Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.idCurrentEmail), withText(email), isDisplayed()));
        editText.check(matches(withText(email)));

        ViewInteraction overflowMenuButton2 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        ViewInteraction materialTextView3 = onView(
                allOf(withId(androidx.core.R.id.title), withText("Log Out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView3.perform(click());

    }

    @Test
    public void test2_loginUserTest() {

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.idEdtUserName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILUserName),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.idEdtPwd),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPassword),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.idBtnLogin), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction overflowMenuButton3 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton3.perform(click());

        ViewInteraction materialTextView4 = onView(
                allOf(withId(androidx.core.R.id.title), withText("Edit Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView4.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.idCurrentEmail), withText(email), isDisplayed()));
        editText2.check(matches(withText(email)));

        ViewInteraction overflowMenuButton2 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        ViewInteraction materialTextView3 = onView(
                allOf(withId(androidx.core.R.id.title), withText("Log Out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView3.perform(click());

    }

    @Test
    public void test3_deleteUserTest() {

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.idEdtUserName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILUserName),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.idEdtPwd),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPassword),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.idBtnLogin), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction overflowMenuButton3 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton3.perform(click());

        ViewInteraction materialTextView4 = onView(
                allOf(withId(androidx.core.R.id.title), withText("Edit Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView4.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.idBtnDeleteAccount), withText("Delete Account"),
                        childAtPosition(
                                allOf(withId(R.id.deleteAccountLL),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                3)),
                                0)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.idEdtUserName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILUserName),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.idEdtPwd),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idTILPassword),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText7.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.idBtnLogin), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withText("Login"), isDisplayed()));
        textView2.check(matches(withText("Login")));
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
