package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1st July 2023
 * ViewMealPlanActivity.java
 * Description - Activity for viewing Meal Plan meal details
 */

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.models.Meal;
import com.example.foodplannerapp.models.MealIngredient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ViewMealPlanActivity extends BaseMenuActivity {
    // declare variables
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private DatabaseReference databaseReferenceIngredients,
            ingredientsDBRef;
    private String mealPlanID,
            mealPlanDate;
    private Meal meal;
    private MealIngredient mealIngredient;
    private LinearLayout shoppingListContainer;
    private final AtomicReference<Boolean> initialLoad = new AtomicReference<>(true);
    private ArrayList<MealIngredient> ingredientsList;

    public ViewMealPlanActivity() {
    }

    // on create method to be executed when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set activity layout
        setContentView(R.layout.activity_view_meal_plan);

        // get parcelable extra using meal object passed to activity intent
        meal = getIntent().getParcelableExtra("MealPlan");

        // initialise variables
        TextInputEditText recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        TextInputEditText recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        TextInputEditText recipePrepTimeEdt = findViewById(R.id.idEdtRecipePrepTime);
        TextInputEditText recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        TextInputEditText recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        TextInputEditText recipeCuisineEdt = findViewById(R.id.idEdtRecipeCuisine);
        TextInputEditText recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        TextInputEditText recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        TextInputEditText recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        Button viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        shoppingListContainer = findViewById(R.id.shopping_List_item);

        // get instance of Firebase realtime database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
        // Reference description - tutorial on how to retrieve and display Firebase data objects
        // populate the layout fields with the meal details from the database
        if (meal != null) {
            mealPlanDate = meal.getDateShort();
            recipeNameEdt.setText(meal.getRecipeName());
            recipeCookingTimeEdt.setText(meal.getRecipeCookingTime());
            recipePrepTimeEdt.setText(meal.getRecipePrepTime());
            recipeServingsEdt.setText(meal.getRecipeServings());
            recipeSuitedForEdt.setText(meal.getRecipeSuitedFor());
            recipeCuisineEdt.setText(meal.getRecipeCuisine());
            recipeDescEdt.setText(meal.getRecipeDescription());
            recipeMethodEdt.setText(meal.getRecipeMethod());
            recipeIngredientsEdt.setText(meal.getRecipeIngredients());
            recipePublicEdt.setChecked(meal.getRecipePublic().equals(true));
            mealPlanID = meal.getMealPlanID();
        }

        // set the actionbar title
        setTitle("Meal Details - " + mealPlanDate);

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

        // create meal plan ingredients database reference required for updating meal ingredient values in shopping list
        ingredientsDBRef = firebaseDatabase.getReference("Meal Plans").child(mealPlanID)
                .child("ingredients");

        // view recipe source page in browser using recipe link
        viewSourceRecipe.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(meal.getRecipeLink()));
            startActivity(i);// launch browser using URL
        });

        // meal plan ingredients child reference
        databaseReferenceIngredients = firebaseDatabase.getReference("Meal Plans")
                .child(mealPlanID).child("ingredients");
    }

    // on create method to be executed when activity is started
    @Override
    protected void onStart() {
        super.onStart();
        // if the database reference is not null
        if(ingredientsDBRef != null){
            // create ingredients db reference value events listener
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ingredientsList = new ArrayList<>();
                        // store db meal ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            mealIngredient = ds.getValue(MealIngredient.class);
                            assert mealIngredient != null;
                            mealIngredient.key = ds.getKey();// set the meal ingredient key to the database key
                            ingredientsList.add(mealIngredient);// store the meal ingredient to the ArrayList
                        }
                        // if the page is being loaded for the first time add...
                        if(initialLoad.get()){
                            for(MealIngredient ingredientListItem : ingredientsList){
                                //...add the ingredients to the shopping list container
                                // pass the ingredient name, purchase value and key to the addIngredientCard method
                                addIngredientCard(ingredientListItem.getIngredient(),
                                        ingredientListItem.getPurchased(),
                                        ingredientListItem.getKey());
                            }
                        }
                        // set the indicator used to indicate whether the page is being loaded for
                        // the first time to false
                        initialLoad.set(false);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewMealPlanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // @Reference - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html
    // Reference description - tutorial on how to add dynamic views in Android
    // add ingredient cards to layout accepts (ingredient name, purchased value, key)
    private void addIngredientCard(String ingredient, String purchased, String key) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater()
                .inflate(R.layout.shopping_list_item, null);

        // find the layout element by ID and assign to variables
        TextView nameView = view.findViewById(R.id.name);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        // set the ingredient card text to the ingredient name
        nameView.setText(ingredient);

        // if a ingredients is noted as purchased in the database then check the checkbox
        if(purchased.equals("true")){
            checkBox.setChecked(true);
        }

        // ingredient card checkbox checking functionality for updating purchased value in the database
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(checkBox.isChecked()){
                Toast.makeText(ViewMealPlanActivity.this, "Shopping List Updated",
                        Toast.LENGTH_SHORT).show();
                databaseReferenceIngredients.child(key).child("purchased")
                        .setValue("true");// set the meal ingredients purchased value to true
            }else{// if the ingredient checkbox is checked then set the ingredients purchased value in the database
                // to false
                Toast.makeText(ViewMealPlanActivity.this, "Shopping List Updated",
                        Toast.LENGTH_SHORT).show();
                databaseReferenceIngredients.child(key).child("purchased")
                        .setValue("false");// set the meal ingredients purchased value to false in the database
            }
        });

        // add the ingredient card to the layout
        shoppingListContainer.addView(view);
    }
}