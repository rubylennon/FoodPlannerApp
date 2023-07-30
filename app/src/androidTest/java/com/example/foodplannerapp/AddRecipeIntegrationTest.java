package com.example.foodplannerapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.foodplannerapp.activities.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddRecipeIntegrationTest {
    String email,
            password,
            recipeName,
            recipeCookingTime,
            recipePreparationTime,
            recipeServings,
            recipeImageURL,
            recipeURL,
            recipeSuitability,
            recipeCuisine,
            recipeDescription,
            recipeCookingMethod,
            recipeIngredientsFull,
            recipeIngredientOne,
            recipeIngredientTwo;

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    private String getResource(int id) {
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();

        return targetContext.getString(id);
    }

    @Before
    public void setUp(){
        email = getResource(R.string.emailTest);
        password = getResource(R.string.passwordTest);
        recipeName = getResource(R.string.recipeNameTest);
        recipeCookingTime = getResource(R.string.recipeCookingTimeTest);
        recipePreparationTime = getResource(R.string.recipePreparationTimeTest);
        recipeServings = getResource(R.string.recipeServingsTest);
        recipeImageURL = getResource(R.string.recipeImageLinkTest);
        recipeURL = getResource(R.string.recipeURLTest);
        recipeSuitability = getResource(R.string.recipeSuitabilityTest);
        recipeCuisine = getResource(R.string.recipeCuisineTest);
        recipeDescription = getResource(R.string.recipeDescriptionTest);
        recipeCookingMethod = getResource(R.string.recipeCookingMethodTest);
        recipeIngredientsFull = getResource(R.string.recipeIngredientsFullTest);
        recipeIngredientOne = getResource(R.string.recipeIngredientsOneTest);
        recipeIngredientTwo = getResource(R.string.recipeIngredientsTwoTest);
    }

    @Test
    public void test1_AddRecipeAndAssertRecipeDetails(){
        System.out.println("Login Test");
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

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.idBtnLogin), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        SystemClock.sleep(3000);

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction materialTextView = onView(
                allOf(withId(androidx.core.R.id.title), withText("Add Recipe"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.idEdtRecipeName),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText(recipeName), closeSoftKeyboard());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.idEdtRecipeCookingTime),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText(recipeCookingTime), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.idEdtRecipePrepTime),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(replaceText(recipePreparationTime), closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.idEdtRecipeServings),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText(recipeServings), closeSoftKeyboard());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.selectSuitabilityTV),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                4)));
        materialTextView2.perform(scrollTo(), click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withId(com.google.android.material.R.id.select_dialog_listview),
                        childAtPosition(
                                withId(com.google.android.material.R.id.contentPanel),
                                0)))
                .atPosition(3);
        appCompatCheckedTextView.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(allOf(withId(com.google.android.material.R.id.select_dialog_listview),
                        childAtPosition(
                                withId(com.google.android.material.R.id.contentPanel),
                                0)))
                .atPosition(6);
        appCompatCheckedTextView3.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(allOf(withId(com.google.android.material.R.id.select_dialog_listview),
                        childAtPosition(
                                withId(com.google.android.material.R.id.contentPanel),
                                0)))
                .atPosition(8);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction materialTextView3 = onView(
                allOf(withId(R.id.selectCuisineTV),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                5)));
        materialTextView3.perform(scrollTo(), click());

        DataInteraction appCompatCheckedTextView4 = onData(anything())
                .inAdapterView(allOf(withId(com.google.android.material.R.id.select_dialog_listview),
                        childAtPosition(
                                withId(com.google.android.material.R.id.contentPanel),
                                0)))
                .atPosition(14);
        appCompatCheckedTextView4.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.idEdtRecipeImageLink),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText7.perform(replaceText(recipeImageURL), closeSoftKeyboard());

        ViewInteraction textInputEditText11 = onView(
                allOf(withId(R.id.idEdtRecipeLink),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText11.perform(replaceText(recipeURL), closeSoftKeyboard());

        ViewInteraction textInputEditText12 = onView(
                allOf(withId(R.id.idEdtRecipeDesc),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText12.perform(replaceText(recipeDescription), closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.idEdtRecipeMethod)).perform(ViewActions.scrollTo());

        ViewInteraction textInputEditText13 = onView(
                allOf(withId(R.id.idEdtRecipeMethod),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.google.android.material.textfield.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText13.perform(replaceText(recipeCookingMethod), closeSoftKeyboard());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.add), withText("Add Ingredient"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                11)));
        materialButton7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.nameEdit),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(recipeIngredientOne), closeSoftKeyboard());

        ViewInteraction materialButton8 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton8.perform(scrollTo(), click());

        ViewInteraction materialButton9 = onView(
                allOf(withId(R.id.add), withText("Add Ingredient"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                11)));
        materialButton9.perform(scrollTo(), click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.nameEdit),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText(recipeIngredientTwo), closeSoftKeyboard());

        ViewInteraction materialButton10 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton10.perform(scrollTo(), click());

        ViewInteraction materialButton11 = onView(
                allOf(withId(R.id.idBtnAddRecipe), withText("Add Recipe"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                13)));
        materialButton11.perform(scrollTo(), click());

        SystemClock.sleep(2000);

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.idRVRecipes),
                        childAtPosition(
                                withId(R.id.idRLHome),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction materialButton12 = onView(
                allOf(withId(R.id.idBtnViewDetails), withText("View Details"),
                        childAtPosition(
                                allOf(withId(R.id.idBtnGroup1),
                                        childAtPosition(
                                                withId(R.id.idRLBSheet),
                                                9)),
                                1),
                        isDisplayed()));
        materialButton12.perform(click());

        SystemClock.sleep(2000);

        ViewInteraction editText = onView(
                allOf(withId(R.id.idEdtRecipeName), withText(recipeName),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText.check(matches(withText(recipeName)));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.idEdtRecipeCookingTime), withText(recipeCookingTime),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText3.check(matches(withText(recipeCookingTime)));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.idEdtRecipePrepTime), withText(recipePreparationTime),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText4.check(matches(withText(recipePreparationTime)));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.idEdtRecipeServings), withText(recipeServings),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText5.check(matches(withText(recipeServings)));

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.idEdtRecipeSuitedFor), withText(recipeSuitability),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText6.check(matches(withText(recipeSuitability)));

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.idEdtRecipeCuisine), withText(recipeCuisine),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText7.check(matches(withText(recipeCuisine)));

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.idEdtRecipeDesc), withText(recipeDescription),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText8.check(matches(withText(recipeDescription)));

        ViewInteraction editText9 = onView(
                allOf(withId(R.id.idEdtRecipeMethod), withText(recipeCookingMethod),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText9.check(matches(withText(recipeCookingMethod)));

        ViewInteraction editText10 = onView(
                allOf(withId(R.id.idEdtRecipeIngredients), withText(recipeIngredientsFull),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        editText10.check(matches(withText(recipeIngredientsFull)));

        Espresso.onView(ViewMatchers.withId(R.id.idPublicSwitch)).perform(ViewActions.scrollTo());

        ViewInteraction switch_3 = onView(
                allOf(withId(R.id.idPublicSwitch), withText("Recipe Public?"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        switch_3.check(matches(isDisplayed()));

        ViewInteraction overflowMenuButton2 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        ViewInteraction materialTextView6 = onView(
                allOf(withId(androidx.core.R.id.title), withText("My Recipes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView6.perform(click());

        ViewInteraction recyclerView4 = onView(
                allOf(withId(R.id.idRVRecipes),
                        childAtPosition(
                                withId(R.id.idRLHome),
                                0)));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.idBtnEdit), withText("Edit Recipe"),
                        childAtPosition(
                                allOf(withId(R.id.idBtnGroup1),
                                        childAtPosition(
                                                withId(R.id.idRLBSheet),
                                                9)),
                                0),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction materialButton16 = onView(
                allOf(withId(R.id.idBtnDeleteRecipe), withText("Delete \nRecipe"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        13),
                                0)));
        materialButton16.perform(scrollTo(), click());

        ViewInteraction materialButton17 = onView(
                allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton17.perform(scrollTo(), click());
    }

    @Test
    public void test2_Logout(){
        ViewInteraction overflowMenuButton4 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton4.perform(click());

        ViewInteraction materialTextView4 = onView(
                allOf(withId(androidx.core.R.id.title), withText("Log Out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView4.perform(click());
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
