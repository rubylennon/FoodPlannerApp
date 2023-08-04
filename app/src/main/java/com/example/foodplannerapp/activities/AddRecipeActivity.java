package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th April 2023
 * AddRecipeActivity.java
 * Description - Add Recipe Activity for creating new recipes
 */

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.models.Recipe;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AddRecipeActivity extends BaseMenuActivity {
    // declare variables
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipePrepTimeEdt,
            recipeServingsEdt,
            recipeImgEdt,
            recipeLinkEdt,
            recipeDescEdt,
            recipeMethodEdt;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private ProgressBar loadingPB;
    private DatabaseReference databaseReference;
    private String recipeID,
            userID,
            ingredientsSelectionString;
    private TextView recipeCuisineEdt,
            recipeSuitabilityEdt;
    private boolean[] selectedCuisine,
            selectedSuitability;
    private final ArrayList<Integer> cuisineList = new ArrayList<>(),
            suitabilityList = new ArrayList<>();
    private String[] cuisineArray,
            suitabilityArray;
    private AlertDialog addIngredientDialog;
    private LinearLayout ingredientCardsContainer;
    private final ArrayList<String> ingredientArrayList = new ArrayList<>();

    // on create method to be executed when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the activity layout
        setContentView(R.layout.activity_add_recipe);

        // set the actionbar title
        setTitle("Add Recipe");

        // initialise variables
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipePrepTimeEdt = findViewById(R.id.idEdtRecipePrepTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeCuisineEdt = findViewById(R.id.selectCuisineTV);
        recipeSuitabilityEdt = findViewById(R.id.selectSuitabilityTV);
        recipeImgEdt = findViewById(R.id.idEdtRecipeImageLink);
        recipeLinkEdt = findViewById(R.id.idEdtRecipeLink);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        loadingPB = findViewById(R.id.idPBLoading);
        ingredientCardsContainer = findViewById(R.id.container);
        Button addRecipeBtn = findViewById(R.id.idBtnAddRecipe);
        Button addIngredientBtn = findViewById(R.id.add);

        // get the currently signed in users ID from the current Firebase Auth instance
        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // get instance of Firebase realtime database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // create Firebase database 'Recipes' reference
        databaseReference = firebaseDatabase.getReference("Recipes");

        // build alert dialog for adding ingredients to recipe
        buildDialogAddIngredient();

        // when the add ingredients button is clicked show the add ingredient dialog box
        addIngredientBtn.setOnClickListener(v -> addIngredientDialog.show());

        // SELECT CUISINE DIALOG START
        // @Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);
        // initialize selected language array
        selectedCuisine = new boolean[cuisineArray.length];
        // on click listener for cuisine dropdown field click
        recipeCuisineEdt.setOnClickListener(view -> {
            // Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);
            // set dialog title
            builder.setTitle("Select Cuisine");
            // set dialog to non cancelable
            builder.setCancelable(false);
            // set the multi-choice items for the dialog
            builder.setMultiChoiceItems(cuisineArray, selectedCuisine, (dialogInterface, i, b) -> {
                // check condition
                if (b) {
                    // when a checkbox is selected add its position to cuisine list
                    cuisineList.add(i);
                    // Sort the array list
                    Collections.sort(cuisineList);
                } else {
                    // when a checkbox is unselected remove its position from the cuisine list
                    cuisineList.remove(Integer.valueOf(i));
                }
            });
            // set the positive button action for the dialog
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                // Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                // use for loop
                for (int j = 0; j < cuisineList.size(); j++) {
                    // concatenate the array value using a string builder
                    stringBuilder.append(cuisineArray[cuisineList.get(j)]);
                    // check condition
                    if (j != cuisineList.size() - 1) {
                        // When j value not equal to cuisine list size - 1 then add a comma
                        stringBuilder.append(", ");
                    }
                }
                // set the selected cuisine text in the textView
                recipeCuisineEdt.setText(stringBuilder.toString());
            });
            // set the cancel button action for the dialog
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss the dialog
                dialogInterface.dismiss();
            });
            // set the clear all button action for the dialog
            builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                // use a for loop
                for (int j = 0; j < selectedCuisine.length; j++) {
                    // remove all selected cuisine from selected cuisine array
                    selectedCuisine[j] = false;
                    // clear the cuisine list
                    cuisineList.clear();
                    // clear text view value
                    recipeCuisineEdt.setText("");
                }
            });
            // show dialog
            builder.show();
        });
        // SELECT CUISINE DIALOG END

        // SELECT SUITABILITY DIALOG START
        // @Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // add items from resource suitability array to local suitability array
        suitabilityArray = getResources().getStringArray(R.array.dietary_requirements);
        // initialize selected language array
        selectedSuitability = new boolean[suitabilityArray.length];
        // on click listener for suitability dropdown field click
        recipeSuitabilityEdt.setOnClickListener(view -> {
            // Initialize alert dialog
            AlertDialog.Builder builder2 = new AlertDialog.Builder(AddRecipeActivity.this);
            // set title
            builder2.setTitle("Select Recipe Suitability");
            // set dialog to non cancelable
            builder2.setCancelable(false);
            // set the multi-choice items for the dialog
            builder2.setMultiChoiceItems(suitabilityArray, selectedSuitability, (dialogInterface, i, b) -> {
                // check condition
                if (b) {
                    // when checkbox is selected add the position in the suitability list
                    suitabilityList.add(i);
                    // Sort the suitability array list
                    Collections.sort(suitabilityList);
                } else {
                    // when a checkbox is unselected remove its position from suitability list
                    suitabilityList.remove(Integer.valueOf(i));
                }
            });
            // set the positive button action for the dialog
            builder2.setPositiveButton("OK", (dialogInterface, i) -> {
                // Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                // use for loop
                for (int j = 0; j < suitabilityList.size(); j++) {
                    // concatenate the array value to string using string builder
                    stringBuilder.append(suitabilityArray[suitabilityList.get(j)]);
                    // check condition
                    if (j != suitabilityList.size() - 1) {
                        // When j value is not equal to suitability list size - 1 add a comma
                        stringBuilder.append(", ");
                    }
                }
                // set text on suitability textView
                recipeSuitabilityEdt.setText(stringBuilder.toString());
            });
            // set the negative button action for the dialog
            builder2.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss dialog
                dialogInterface.dismiss();
            });
            // set the clear all button action for the dialog
            builder2.setNeutralButton("Clear All", (dialogInterface, i) -> {
                // use a for loop
                for (int j = 0; j < selectedSuitability.length; j++) {
                    // remove all selection from selected suitability array
                    selectedSuitability[j] = false;
                    // clear suitability list
                    suitabilityList.clear();
                    // clear text view value
                    recipeSuitabilityEdt.setText("");
                }
            });
            // show dialog
            builder2.show();
        });
        // SELECT SUITABILITY DIALOG END

        // add recipe button click event action
        addRecipeBtn.setOnClickListener(v -> {
            // if statements to validate that all required fields are populated before adding recipe
            if(Objects.requireNonNull(recipeNameEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add a Recipe Name", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add a Cooking Time", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipePrepTimeEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add a Preparation Time", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeServingsEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Total Servings", Toast.LENGTH_SHORT).show();
            }else if(recipeSuitabilityEdt.getText().toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please Recipe Suitability", Toast.LENGTH_SHORT).show();
            }else if(recipeCuisineEdt.getText().toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Cuisine", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeImgEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Image Link", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeLinkEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Link", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeDescEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please Recipe Description", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeMethodEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Method", Toast.LENGTH_SHORT).show();
            }else if(ingredientArrayList.isEmpty()){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Ingredients", Toast.LENGTH_SHORT).show();
            }else {// if all required fields are populated with values then execute the following
                // build a string with selected ingredients using string builder
                StringBuilder stringBuilder3 = new StringBuilder();
                // use a for loop
                for (int j = 0; j < ingredientArrayList.size(); j++) {
                    // concatenate array values to string
                    stringBuilder3.append(ingredientArrayList.get(j));
                    // check condition
                    if (j != ingredientArrayList.size() - 1) {
                        // When j value  not equal to ingredient list size - 1 add a comma
                        stringBuilder3.append(", ");
                    }
                }
                //store the string builder value to a string variable
                ingredientsSelectionString = stringBuilder3.toString();

                // set the loading bar to visible
                loadingPB.setVisibility(View.VISIBLE);

                // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
                // Reference description - tutorial on how to add an object to the Firebase Realtime Database
                // get user input text from fields
                String recipeName = recipeNameEdt.getText().toString();
                String recipeCookingTime = recipeCookingTimeEdt.getText().toString();
                String recipePrepTime = recipePrepTimeEdt.getText().toString();
                String recipeServings = recipeServingsEdt.getText().toString();
                String recipeSuitedFor = recipeSuitabilityEdt.getText().toString();
                String recipeCuisine = recipeCuisineEdt.getText().toString();
                String recipeImg = recipeImgEdt.getText().toString();
                String recipeLink = recipeLinkEdt.getText().toString();
                String recipeDesc = recipeDescEdt.getText().toString();
                String recipeMethod = recipeMethodEdt.getText().toString();
                String recipeIngredients = ingredientsSelectionString;
                Boolean recipePublic = recipePublicEdt.isChecked();
                // get the database reference value for the new recipe object
                recipeID = databaseReference.push().getKey();

                // create a recipe object to store the new recipe values
                Recipe recipe = new Recipe(recipeName,recipeCookingTime,recipePrepTime,
                        recipeServings,recipeSuitedFor,recipeCuisine,recipeImg,recipeLink,
                        recipeDesc,recipeMethod,recipeIngredients,recipePublic,recipeID,userID);

                // add the new recipe object to the Firebase Realtime database reference (Recipes)
                databaseReference.addValueEventListener(new ValueEventListener() {
                    // execute the following if the add value action is successful
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // hide the loading bar
                        loadingPB.setVisibility(View.GONE);
                        // add the recipe object to the database
                        databaseReference.child(recipeID).setValue(recipe);
                        // display a toast notification
                        Toast.makeText(AddRecipeActivity.this, "Recipe Added", Toast.LENGTH_SHORT).show();
                        // redirect the user to the MainActivity (My Recipes) screen
                        startActivity(new Intent(AddRecipeActivity.this, MainActivity.class));
                    }
                    // execute the following if the add value action fails
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddRecipeActivity.this, "Error is " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // @Reference - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html
    // Reference description - tutorial on how to add dynamic views in Android
    // Ingredient list alert dialog builder for adding ingredients to recipe
    private void buildDialogAddIngredient() {
        // initialise alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // create the new view and inflater
        View view = getLayoutInflater().inflate(R.layout.new_ingredient_dialog, null);
        // declare and assign the view elements
        final EditText name = view.findViewById(R.id.nameEdit);
        // set the builder view to view created
        builder.setView(view);
        // set the title and set the positive button action
        builder.setTitle("Enter Ingredient")
                .setPositiveButton("OK", (dialog, which) -> {
                    // if the ingredient name value is blank or not added show an alert
                    if(name.getText().toString().trim().equals("") || name.getText().toString().equals("Name")){
                        Toast.makeText(this, "Ingredient cannot be blank.", Toast.LENGTH_SHORT).show();
                        name.setText("");// clear the ingredient textview field value
                    }else if(name.getText().toString().trim().contains(",")){
                        //if the ingredient name contains a comma show and alert
                        Toast.makeText(this, "Ingredient cannot contain commas", Toast.LENGTH_SHORT).show();
                        name.setText("");// clear the ingredient textview field value
                    } else{
                        addIngredientCard(name.getText().toString());// add ingredient card with ingredient name
                        ingredientArrayList.add(name.getText().toString());// add the ingredient to the ArrayList
                        name.setText("");// clear the ingredient textview field value
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> name.setText(""));// Cancel button action
        // create the add ingredient dialog
        addIngredientDialog = builder.create();
    }

    //@Reference - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html
    //Reference description - tutorial on how to add dynamic views in Android
    // add ingredient card to screen
    private void addIngredientCard(String name) {
        // create new view for the new ingredient card to be added to Add Recipe screen
        @SuppressLint("InflateParams") final View view = getLayoutInflater()
                .inflate(R.layout.ingredient_card_rounded, null);
        // declare and assign the view variables
        TextView nameView = view.findViewById(R.id.name);
        Button delete = view.findViewById(R.id.delete);
        // set the ingredient name to the text entered by the user when adding ingredient
        nameView.setText(name);
        // delete ingredient button action
        delete.setOnClickListener(v -> {
            // remove the ingredient from the ingredients ArrayList
            ingredientArrayList.remove(name);
            // remove the ingredient card from the ingredients list
            ingredientCardsContainer.removeView(view);
        });
        // add the new card to the ingredients card container
        ingredientCardsContainer.addView(view);
    }
}