package com.example.firebasecrudapplication;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.firebasecrudapplication.activities.LoginActivity;
import com.example.firebasecrudapplication.activities.MainActivity;
import com.example.firebasecrudapplication.adapters.RecipeRVAdapter;
import com.example.firebasecrudapplication.models.Recipe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TEST_STRING = "This is a string";
    public static final long TEST_LONG = 12345678L;
    private RecipeRVAdapter recipeRVAdapter;
    private final ArrayList<Recipe> list = new ArrayList<>();
    private final String recipeName = "Test Name";
    private final String recipeCookingTime = "45";
    private final String recipePrepTime = "15";
    private final String recipeServings = "1";
    private final String recipeSuitedFor = "Everyone";
    private final String recipeCuisine = "American";
    private final String recipeImg = "https://img.example.com/123";
    private final String recipeLink = "https://example.com";
    private final String recipeDescription = "Test Description";
    private final String recipeMethod = "Step 1, Step 2, Step 3.";
    private final String recipeIngredients = "Ingredient 1, Ingredient 2, Ingredient 3";
    private final String recipeID = "-N_QAXvVJBdKqhQWjD2T";
    private final String userID = "Bz7rj0xdxJSRwgcoaaFjh8uWRo83";

    private final Recipe r1 = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, false,
            recipeID, userID);

    private final Recipe r2 = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, false,
            recipeID, userID);

    private final Recipe r3 = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, false,
            recipeID, userID);

    @Before
    public void createLogHistory() {
        list.add(r1);
        list.add(r2);
        list.add(r3);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        recipeRVAdapter = new RecipeRVAdapter(list, appContext, new RecipeRVAdapter.RecipeClickInterface() {
            @Override
            public void onRecipeClick(int position) {

            }
        });

        System.out.println(recipeRVAdapter.getItemCount());
    }

    @Test
    public void logHistory_ParcelableWriteRead() {
//        // Set up the Parcelable object to send and receive.
//        loginActivity.addEntry(TEST_STRING, TEST_LONG);
//
//        // Write the data.
//        Parcel parcel = Parcel.obtain();
//        mLogHistory.writeToParcel(parcel, mLogHistory.describeContents());
//
//        // After you're done with writing, you need to reset the parcel for reading.
//        parcel.setDataPosition(0);
//
//        // Read the data.
//        LogHistory createdFromParcel = LogHistory.CREATOR.createFromParcel(parcel);
//        List<Pair<String, Long>> createdFromParcelData
//                = createdFromParcel.getData();
//
//        // Verify that the received data is correct.
//        assertThat(createdFromParcelData.size()).isEqualTo(1);
//        assertThat(createdFromParcelData.get(0).first).isEqualTo(TEST_STRING);
//        assertThat(createdFromParcelData.get(0).second).isEqaulTo(TEST_LONG);
    }
}