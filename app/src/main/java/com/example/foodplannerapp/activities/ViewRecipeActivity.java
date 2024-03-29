package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th February 2023
 * ViewRecipeActivity.java
 * Description - Activity for viewing Recipe details
 */

// imports
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.fragments.DatePickerFragment;
import com.example.foodplannerapp.models.Meal;
import com.example.foodplannerapp.models.MealIngredient;
import com.example.foodplannerapp.models.Recipe;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ViewRecipeActivity extends BaseMenuActivity implements DatePickerDialog.OnDateSetListener {
    // declare variables
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipePrepTimeEdt,
            recipeServingsEdt,
            recipeSuitedForEdt,
            recipeCuisineEdt,
            recipeDescEdt,
            recipeMethodEdt,
            recipeIngredientsEdt;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private DatabaseReference databaseReferenceMealPlan;
    private String recipeID,
            userID,
            recipeImgEdt,
            recipeLinkEdt;
    private Recipe recipe;

    public ViewRecipeActivity() {
    }

    // on create method to be executed when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the activity layout
        setContentView(R.layout.activity_view_recipe);

        // set the actionbar title
        setTitle("Recipe Details");

        // get parcelable extra using meal object passed to activity intent
        recipe = getIntent().getParcelableExtra("recipe");

        // initialise variables
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipePrepTimeEdt = findViewById(R.id.idEdtRecipePrepTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeCuisineEdt = findViewById(R.id.idEdtRecipeCuisine);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        Button viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        Button addRecipeToMealPlan = findViewById(R.id.idBtnAddToMealPlan);

        // get instance of Firebase realtime database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // get the currently signed in users ID from the current Firebase Auth instance
        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // assign database reference to Meal Plan firebase realtime database reference
        databaseReferenceMealPlan = firebaseDatabase.getReference("Meal Plans");

        // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
        // Reference description - tutorial on how to retrieve and display Firebase data objects
        // populate the layout fields with the meal details from the database
        if (recipe != null){
            recipeNameEdt.setText(recipe.getRecipeName());
            recipeCookingTimeEdt.setText(recipe.getRecipeCookingTime());
            recipePrepTimeEdt.setText(recipe.getRecipePrepTime());
            recipeServingsEdt.setText(recipe.getRecipeServings());
            recipeSuitedForEdt.setText(recipe.getRecipeSuitedFor());
            recipeCuisineEdt.setText(recipe.getRecipeCuisine());
            recipeDescEdt.setText(recipe.getRecipeDescription());
            recipeMethodEdt.setText(recipe.getRecipeMethod());
            recipeIngredientsEdt.setText(recipe.getRecipeIngredients());
            recipePublicEdt.setChecked(recipe.getRecipePublic().equals(true));
            recipeID = recipe.getRecipeID();
            recipeImgEdt = recipe.getRecipeImg();
            recipeLinkEdt = recipe.getRecipeLink();
        }

        // set input fields to non focusable
        recipeNameEdt.setFocusable(false);
        recipeCookingTimeEdt.setFocusable(false);
        recipePrepTimeEdt.setFocusable(false);
        recipeServingsEdt.setFocusable(false);
        recipeSuitedForEdt.setFocusable(false);
        recipeCuisineEdt.setFocusable(false);
        recipeDescEdt.setFocusable(false);
        recipeMethodEdt.setFocusable(false);
        recipeIngredientsEdt.setFocusable(false);
        // disable input fields
        recipeNameEdt.setEnabled(false);
        recipeCookingTimeEdt.setEnabled(false);
        recipePrepTimeEdt.setEnabled(false);
        recipeServingsEdt.setEnabled(false);
        recipeSuitedForEdt.setEnabled(false);
        recipeCuisineEdt.setEnabled(false);
        recipeDescEdt.setEnabled(false);
        recipeMethodEdt.setEnabled(false);
        recipeIngredientsEdt.setEnabled(false);
        // disable input fields cursor visibility
        recipeNameEdt.setCursorVisible(false);
        recipeCookingTimeEdt.setCursorVisible(false);
        recipePrepTimeEdt.setCursorVisible(false);
        recipeServingsEdt.setCursorVisible(false);
        recipeSuitedForEdt.setCursorVisible(false);
        recipeCuisineEdt.setCursorVisible(false);
        recipeDescEdt.setCursorVisible(false);
        recipeMethodEdt.setCursorVisible(false);
        recipeIngredientsEdt.setCursorVisible(false);
        // set switch button to non clickable
        recipePublicEdt.setClickable(false);

        // view recipe source page in browser using recipe link
        viewSourceRecipe.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(recipe.getRecipeLink()));
            startActivity(i);// launch browser using URL
        });

        // @Reference - https://www.youtube.com/watch?v=33BFCdL0Di0
        // Reference description - tutorial on how to create a date picker dialog fragment
        // add recipe to meal plan button functionality
        addRecipeToMealPlan.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

    }

    // @Reference - https://www.youtube.com/watch?v=33BFCdL0Di0
    // Reference description - tutorial on how to create a date picker dialog fragment
    // method for adding recipe to meal plan
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // get selected calendar dates and build calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        //@Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
        //Reference description - tutorial on how to add an object to the Firebase Realtime Database
        // set values for new meal plan object
        String currentDateStringShort = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String currentDateStringLong = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String recipeName = Objects.requireNonNull(recipeNameEdt.getText()).toString();
        String recipeCookingTime = Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString();
        String recipePrepTime = Objects.requireNonNull(recipePrepTimeEdt.getText()).toString();
        String recipeServings = Objects.requireNonNull(recipeServingsEdt.getText()).toString();
        String recipeSuitedFor = Objects.requireNonNull(recipeSuitedForEdt.getText()).toString();
        String recipeCuisine = Objects.requireNonNull(recipeCuisineEdt.getText()).toString();
        String recipeImg = recipeImgEdt;
        String recipeLink = recipeLinkEdt;
        String recipeDesc = Objects.requireNonNull(recipeDescEdt.getText()).toString();
        String recipeMethod = Objects.requireNonNull(recipeMethodEdt.getText()).toString();
        String recipeIngredients = Objects.requireNonNull(recipeIngredientsEdt.getText()).toString();
        String[] ingredientsArray = recipeIngredients.split(",");
        Boolean recipePublic = recipePublicEdt.isChecked();
        // get the ID for the new meal
        String mealPlanID = databaseReferenceMealPlan.push().getKey();

        // create a meal object to store the new meal values
        Meal meal = new Meal(currentDateStringShort, mealPlanID, recipeName, recipeCookingTime,
                recipePrepTime, recipeServings, recipeSuitedFor, recipeCuisine, recipeImg,
                recipeLink, recipeDesc, recipeMethod, recipeIngredients, recipePublic, recipeID,
                userID, currentDateStringLong);
        assert mealPlanID != null;
        // create new meal plan object
        databaseReferenceMealPlan.child(mealPlanID).setValue(meal);

        // store ingredients to ingredients child object of the new meal object
        for (String ingredient : ingredientsArray) {// for every recipe ingredient
            // create a new meal ingredient object
            MealIngredient mealIngredient = new MealIngredient(ingredient.trim(), "false");
            // add the new meal ingredient object as a child of the new meal created
            databaseReferenceMealPlan.child(mealPlanID).child("ingredients").push().setValue(mealIngredient);
        }
        // show toast
        Toast.makeText(ViewRecipeActivity.this, "Recipe Added to Meal Plan", Toast.LENGTH_SHORT).show();
        // redirect user to Meal Planner activity
        startActivity(new Intent(ViewRecipeActivity.this, MealPlanActivity.class));
    }
}