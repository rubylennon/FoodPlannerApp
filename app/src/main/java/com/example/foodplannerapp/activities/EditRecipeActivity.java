package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th April 2023
 * EditRecipeActivity.java
 * Description - Edit Recipe Activity for updating existing recipes
 */

//imports
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

// edit recipe class
public class EditRecipeActivity extends BaseMenuActivity {
    // text input variables
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipePrepTimeEdt,
            recipeServingsEdt,
            recipeImgEdt,
            recipeLinkEdt,
            recipeDescEdt,
            recipeMethodEdt;
    // switch button
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private ProgressBar loadingPB;
    private DatabaseReference databaseReference;
    private String recipeID,
            ingredientsSelectionString;
    private TextView recipeSuitabilityEdt,
            recipeCuisineEdt;
    private boolean[] selectedCuisine,
            selectedSuitability;
    private final ArrayList<Integer> cuisineList = new ArrayList<>(),
            suitabilityList = new ArrayList<>();
    private String[] cuisineArray,
            suitabilityArray,
            previouslySelectedCuisineArray,
            previouslySelectedSuitabilityArray;
    private AlertDialog addIngredientDialog;
    private LinearLayout ingredientCardsContainer;
    private final ArrayList<String> ingredientList = new ArrayList<>();
    final AtomicReference<Boolean> editPageInitialLoad = new AtomicReference<>(true);

    // on create method to be executed when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // use the edit recipe activity layout
        setContentView(R.layout.activity_edit_recipe);

        // set the actionbar title
        setTitle("Edit Recipe");

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
        Button addIngredientBtn = findViewById(R.id.add);
        Button updateRecipeBtn = findViewById(R.id.idBtnUpdateRecipe);
        Button deleteRecipeBtn = findViewById(R.id.idBtnDeleteRecipe);

        // get instance of Firebase realtime database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // build the add ingredients alert dialog
        buildDialogAddIngredient();

        // when the add ingredient button is clicked show the add ingredients dialog
        addIngredientBtn.setOnClickListener(v -> addIngredientDialog.show());

        // get the parcelable recipe object received when the edit recipe page was opened
        Recipe recipe = getIntent().getParcelableExtra("recipe");

        // if the recipe object is not null update the input text fields with the recipe data
        if (recipe != null){
            recipeNameEdt.setText(recipe.getRecipeName());
            recipeCookingTimeEdt.setText(recipe.getRecipeCookingTime());
            recipePrepTimeEdt.setText(recipe.getRecipePrepTime());
            recipeServingsEdt.setText(recipe.getRecipeServings());
            recipeSuitabilityEdt.setText(recipe.getRecipeSuitedFor());
            String previouslySelectedSuitability = recipe.getRecipeSuitedFor();
            // split the stored suitability array by comma and store in array
            previouslySelectedSuitabilityArray = previouslySelectedSuitability.split(",");
            recipeCuisineEdt.setText(recipe.getRecipeCuisine());
            String previouslySelectedCuisine = recipe.getRecipeCuisine();
            // split the stored cuisine array by comma and store in array
            previouslySelectedCuisineArray = previouslySelectedCuisine.split(",");
            recipeImgEdt.setText(recipe.getRecipeImg());
            recipeLinkEdt.setText(recipe.getRecipeLink());
            recipeDescEdt.setText(recipe.getRecipeDescription());
            recipeMethodEdt.setText(recipe.getRecipeMethod());
            String previouslySelectedIngredients = recipe.getRecipeIngredients();
            // split the stored ingredients array by comma and store in array
            String[] previouslySelectedIngredientsArray =
                    previouslySelectedIngredients.split(",");
            // add previously added ingredients to view (saved ingredients)
            for(String ingredient : previouslySelectedIngredientsArray){
                addIngredientCard(ingredient);
            }
            // after adding previously added ingredients to view set editPageInitialLoad to false
            editPageInitialLoad.set(false);
            recipePublicEdt.setChecked(recipe.getRecipePublic().equals(true));
            recipeID = recipe.getRecipeID();
        }

        // update the firebase database reference to the Recipes object
        databaseReference = firebaseDatabase.getReference("Recipes").child(recipeID);

        // SELECT CUISINE DIALOG START
        //@Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        //Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);
        // initialize selected language array
        selectedCuisine = new boolean[cuisineArray.length];

        // booleans for indicating whether the cuisine selection has been updated since the page
        // was loaded (default false)
        AtomicReference<Boolean> cuisineSelectionUpdated = new AtomicReference<>(false);
        AtomicReference<Boolean> newSelectionInitiated = new AtomicReference<>(false);

        // if the cuisine selection has not been updated yet, update the previously selected cuisine list
        if(!cuisineSelectionUpdated.get()){
            // store previouslySelectedCuisineArray indexes to array
            for(String cuisine : previouslySelectedCuisineArray){
                cuisine = cuisine.trim();
                int index = Arrays.asList(cuisineArray).indexOf(cuisine);
                selectedCuisine[index] = true;
            }
        }

        // on click listener for cuisine dropdown field click
        recipeCuisineEdt.setOnClickListener(view -> {
            // Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(EditRecipeActivity.this);
            // set title
            builder.setTitle("Select Cuisine");
            // set dialog non cancelable
            builder.setCancelable(false);
            // build the alert dialog with the cuisine list
            builder.setMultiChoiceItems(cuisineArray, selectedCuisine, (dialogInterface, i, b) -> {
                if(!cuisineSelectionUpdated.get()){
                    // add previously selected cuisine to cuisine selection list
                    for(String cuisine : previouslySelectedCuisineArray){
                        cuisine = cuisine.trim();
                        int index = Arrays.asList(cuisineArray).indexOf(cuisine);
                        cuisineList.add(index);
                    }
                }
                // check condition
                if (b) {
                    // when a checkbox is selected add its position to cuisine list
                    cuisineList.add(i);
                    Collections.sort(cuisineList);// Sort array list
                    newSelectionInitiated.set(true);// set the is previously saved selection added boolean to true
                } else {
                    // when a checkbox is unselected remove its position from the cuisine list
                    cuisineList.remove(Integer.valueOf(i));
                    newSelectionInitiated.set(true);// set the is previously saved selection added boolean to true
                }
            });

            // if the OK button is selected execute the following
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                // if the selection is being updated for the first time since the page was loaded then...
                if(!cuisineSelectionUpdated.get() && !newSelectionInitiated.get()){
                    // ...add previously selected cuisine to cuisine selection list
                    for(String cuisine : previouslySelectedCuisineArray){
                        cuisine = cuisine.trim();
                        int index = Arrays.asList(cuisineArray).indexOf(cuisine);
                        cuisineList.add(index);
                    }
                }
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
                cuisineSelectionUpdated.set(true);// set selection updated to True
            });
            // set the cancel button action for the dialog
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss the dialog
                dialogInterface.dismiss();
            });
            // set the clear all button action for the dialog
            builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                // use for loop
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
        //@Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        //Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // add items from resource suitability array to local suitability array
        suitabilityArray = getResources().getStringArray(R.array.dietary_requirements);
        // initialize selected language array
        selectedSuitability = new boolean[suitabilityArray.length];

        // booleans for indicating whether the suitability selection has been updated since the page
        // was loaded (default false)
        AtomicReference<Boolean> suitabilitySelectionUpdated =
                new AtomicReference<>(false);
        AtomicReference<Boolean> newSuitabilitySelectionInitiated =
                new AtomicReference<>(false);

        // if the suitability selection has not been updated update the previously selected suitability list
        if(!suitabilitySelectionUpdated.get()){
            for(String suitability : previouslySelectedSuitabilityArray){
                suitability = suitability.trim();
                int index = Arrays.asList(suitabilityArray).indexOf(suitability);
                selectedSuitability[index] = true;
            }
        }
        // on click listener for suitability dropdown field click
        recipeSuitabilityEdt.setOnClickListener(view -> {
            // Initialize alert dialog
            AlertDialog.Builder builder2 = new AlertDialog.Builder(EditRecipeActivity.this);
            // set title
            builder2.setTitle("Select Suitability");
            // set dialog non cancelable
            builder2.setCancelable(false);
            // build the alert dialog for selecting the recipe suitability
            builder2.setMultiChoiceItems(suitabilityArray, selectedSuitability, (dialogInterface, i, b) -> {
                if(!suitabilitySelectionUpdated.get()){
                    // add previously selected cuisine to cuisine selection list
                    for(String suitability : previouslySelectedSuitabilityArray){
                        suitability = suitability.trim();
                        int index = Arrays.asList(suitabilityArray).indexOf(suitability);
                        suitabilityList.add(index);
                    }
                }
                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position  in suitability list
                    suitabilityList.add(i);
                    // Sort array list
                    Collections.sort(suitabilityList);
                    newSuitabilitySelectionInitiated.set(true);
                } else {
                    // when checkbox unselected
                    // Remove position from suitability list
                    suitabilityList.remove(Integer.valueOf(i));
                    newSuitabilitySelectionInitiated.set(true);
                }
            });
            // if the OK button is selected execute the following
            builder2.setPositiveButton("OK", (dialogInterface, i) -> {
                // if the selection is being updated for the first time since the page was loaded then...
                if(!suitabilitySelectionUpdated.get() && !newSuitabilitySelectionInitiated.get()){
                    // ...add previously selected cuisine to cuisine selection list
                    for(String suitability : previouslySelectedSuitabilityArray){
                        suitability = suitability.trim();
                        int index = Arrays.asList(suitabilityArray).indexOf(suitability);
                        suitabilityList.add(index);
                    }
                }
                // Initialize string builder
                StringBuilder stringBuilder2 = new StringBuilder();
                // use for loop
                for (int j = 0; j < suitabilityList.size(); j++) {
                    // concatenate the array value to string using string builder
                    stringBuilder2.append(suitabilityArray[suitabilityList.get(j)]);
                    // check condition
                    if (j != suitabilityList.size() - 1) {
                        // When j value is not equal to suitability list size - 1 add a comma
                        stringBuilder2.append(", ");
                    }
                }
                // set text on the suitability textView
                recipeSuitabilityEdt.setText(stringBuilder2.toString());
                // set selection updated to True
                suitabilitySelectionUpdated.set(true);
            });
            // if the Cancel button is selected execute the following
            builder2.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss dialog
                dialogInterface.dismiss();
            });
            // if the Clear All button is selected execute the following
            builder2.setNeutralButton("Clear All", (dialogInterface, i) -> {
                // use for loop
                for (int j = 0; j < selectedSuitability.length; j++) {
                    // remove all selection
                    selectedSuitability[j] = false;
                    // clear language list
                    suitabilityList.clear();
                    // clear text view value
                    recipeSuitabilityEdt.setText("");
                }
            });
            // show dialog
            builder2.show();
        });
        // SELECT SUITABILITY DIALOG END

        // update button functionality
        updateRecipeBtn.setOnClickListener(v -> {
            // if statements to validate that all required fields are populated before adding recipe
            if(Objects.requireNonNull(recipeNameEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add a Recipe Name", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add a Cooking Time", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipePrepTimeEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add a Preparation Time", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeServingsEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add Recipe Total Servings", Toast.LENGTH_SHORT).show();
            }else if(recipeSuitabilityEdt.getText().toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please Recipe Suitability", Toast.LENGTH_SHORT).show();
            }else if(recipeCuisineEdt.getText().toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add Recipe Cuisine", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeImgEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add Recipe Image Link", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeLinkEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add Recipe Link", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeDescEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please Recipe Description", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeMethodEdt.getText()).toString().equals("")){
                Toast.makeText(EditRecipeActivity.this, "Please add Recipe Method", Toast.LENGTH_SHORT).show();
            }else if(ingredientList.isEmpty()){
                Toast.makeText(EditRecipeActivity.this, "Please add Recipe Ingredients", Toast.LENGTH_SHORT).show();
            }else {// if all required fields are completed execute the following
                // @Reference - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
                // Reference description - tutorial on how to Create an Alert Dialog Box in Android
                // Create AlertDialog Builder class object
                AlertDialog.Builder builder4 = new AlertDialog.Builder(EditRecipeActivity.this);
                // Set the alert dialog message
                builder4.setMessage("Are you sure you to update this recipe?");
                // Set Alert Title
                builder4.setTitle("Update Recipe Confirmation");
                // set cancelable by clicking outside dialog to false
                builder4.setCancelable(false);
                // Set Yes button action
                builder4.setPositiveButton("Yes", (dialog, which) -> {
                    loadingPB.setVisibility(View.VISIBLE);// show the progress bar
                    // build a string with selected ingredients
                    StringBuilder stringBuilder3 = new StringBuilder();
                    // use for loop
                    for (int j = 0; j < ingredientList.size(); j++) {
                        // concat array value
                        stringBuilder3.append(ingredientList.get(j));
                        // check condition
                        if (j != ingredientList.size() - 1) {
                            // When j value  not equal to ingredient list size - 1 add a comma
                            stringBuilder3.append(", ");
                        }
                    }
                    //store the string builder value to a string variable
                    ingredientsSelectionString = stringBuilder3.toString();

                    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
                    // Reference description - tutorial on how to edit an object in the Firebase Realtime Database
                    // assign values to variable which will be used to update recipe object
                    String recipeName = Objects.requireNonNull(recipeNameEdt.getText()).toString();
                    String recipeCookingTime = Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString();
                    String recipePrepTime = Objects.requireNonNull(recipePrepTimeEdt.getText()).toString();
                    String recipeServings = Objects.requireNonNull(recipeServingsEdt.getText()).toString();
                    String recipeSuitedFor = recipeSuitabilityEdt.getText().toString();
                    String recipeCuisine = recipeCuisineEdt.getText().toString();
                    String recipeImg = Objects.requireNonNull(recipeImgEdt.getText()).toString();
                    String recipeLink = Objects.requireNonNull(recipeLinkEdt.getText()).toString();
                    String recipeDesc = Objects.requireNonNull(recipeDescEdt.getText()).toString();
                    String recipeMethod = Objects.requireNonNull(recipeMethodEdt.getText()).toString();
                    String recipeIngredients = Objects.requireNonNull(ingredientsSelectionString);
                    Boolean recipePublic = recipePublicEdt.isChecked();

                    // map the values to the object variables
                    Map<String,Object> map = new HashMap<>();
                    map.put("recipeName",recipeName);
                    map.put("recipeCookingTime",recipeCookingTime);
                    map.put("recipePrepTime",recipePrepTime);
                    map.put("recipeServings",recipeServings);
                    map.put("recipeSuitedFor",recipeSuitedFor);
                    map.put("recipeCuisine",recipeCuisine);
                    map.put("recipeImg",recipeImg);
                    map.put("recipeLink",recipeLink);
                    map.put("recipeDescription",recipeDesc);
                    map.put("recipeMethod",recipeMethod);
                    map.put("recipeIngredients",recipeIngredients);
                    map.put("recipePublic",recipePublic);
                    map.put("recipeID",recipeID);

                    // database reference value event lister
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // if the task is successful
                            loadingPB.setVisibility(View.GONE);// hide the progress bar
                            // update the recipe object using the mapped values defined above
                            databaseReference.updateChildren(map);
                            Toast.makeText(EditRecipeActivity.this, "Recipe Updated", Toast.LENGTH_SHORT).show();
                            // redirect the user to the main activity screen (My Recipes)
                            startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {// if the task is fails
                            loadingPB.setVisibility(View.GONE);// hide the progress loading bar
                            Toast.makeText(EditRecipeActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                // Set the No button action
                builder4.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();// If user click no then dialog box is canceled and closed
                    loadingPB.setVisibility(View.GONE);// hide the progress loading bar
                });
                // Create Alert dialog
                AlertDialog alertDialog = builder4.create();
                // Show Alert Dialog box
                alertDialog.show();
            }
        });

        // delete recipe button action
        deleteRecipeBtn.setOnClickListener(v -> {
            // @Reference - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
            // Reference description - tutorial on how to Create an Alert Dialog Box in Android
            // Create AlertDialog Builder class object
            AlertDialog.Builder builder3 = new AlertDialog.Builder(EditRecipeActivity.this);
            // Set the alert message
            builder3.setMessage("Are you sure you want to delete this recipe? This action cannot be undone.");
            // Set Alert Title
            builder3.setTitle("Delete Recipe Confirmation");
            // set cancelable by clicking outside dialog to false
            builder3.setCancelable(false);
            // Set the Yes button action
            builder3.setPositiveButton("Yes", (dialog, which) -> deleteRecipe());// delete the recipe
            // Set the No button action
            builder3.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();// If user click no then dialog box is canceled and closed
            });
            // Create the Alert dialog
            AlertDialog alertDialog = builder3.create();
            // Show the Alert Dialog box
            alertDialog.show();
        });
    }

    // method for deleting recipe from Firebase Realtime database
    private void deleteRecipe(){
        databaseReference.removeValue();// remove value from Recipes reference
        Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
        // direct user to Main Activity (My Recipes)
        startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
    }

    // @Reference - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html
    // Reference description - tutorial on how to add dynamic views in Android
    // Ingredient list alert dialog builder for adding ingredients to recipe
    private void buildDialogAddIngredient() {
        // initialise alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // create the new view and inflater
        View view = getLayoutInflater().inflate(R.layout.new_ingredient_dialog, null);
        // variable for storing the user input ingredient value from the alert dialog
        final EditText name = view.findViewById(R.id.nameEdit);
        // show the dialog
        builder.setView(view);
        // set the alert dialog title
        builder.setTitle("Enter Ingredient")
                // functionality if alert dialog OK is clicked
                .setPositiveButton("OK", (dialog, which) -> {
                    // if the ingredient name value is blank or not added show an alert
                    if(name.getText().toString().trim().equals("") || name.getText().toString().equals("Name")){
                        Toast.makeText(this, "Ingredient cannot be blank.", Toast.LENGTH_SHORT).show();
                        name.setText("");// clear the ingredient textview field value
                    }else if(name.getText().toString().trim().contains(",")){
                        Toast.makeText(this, "Ingredient cannot contain commas.", Toast.LENGTH_SHORT).show();
                        name.setText("");// clear the ingredient textview field value
                    } else{// if the user input ingredient is not blank then execute the following
                        // create a card view item for the new ingredient
                        addIngredientCard(name.getText().toString());// add ingredient card with ingredient name
                        // add the new ingredient to the recipe ingredients list
                        ingredientList.add(name.getText().toString());
                        // clear the input field for adding new ingredients
                        name.setText("");// clear the ingredient textview field value
                    }
                })
                // if the alert dialog Cancel button is clicked then execute the following
                .setNegativeButton("Cancel", (dialog, which) -> name.setText(""));// Cancel button action
        // create the add ingredient dialog
        addIngredientDialog = builder.create();
    }

    // @Reference - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html
    // Reference description - tutorial on how to add dynamic views in Android
    // add ingredient card to screen
    private void addIngredientCard(String name) {
        // create new view for the new ingredient card to be added to Add Recipe screen
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.ingredient_card_rounded, null);
        // if the edit page is being loaded for the first time then add all previously added ingredients to selected list
        if(editPageInitialLoad.get()){
            ingredientList.add(name);
        }
        // find and assign layout elements
        TextView nameView = view.findViewById(R.id.name);
        Button delete = view.findViewById(R.id.delete);
        // set the ingredient name
        nameView.setText(name);
        // delete ingredient button action
        delete.setOnClickListener(v -> {
            // remove the ingredient from the ingredientsList
            ingredientList.remove(name);
            // remove the deleted ingredient from the list
            ingredientCardsContainer.removeView(view);
        });
        // add the ingredient card item to the view
        ingredientCardsContainer.addView(view);
    }
}