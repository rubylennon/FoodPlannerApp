package com.example.firebasecrudapplication.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th April 2023
 * EditRecipeActivity.java
 * Description - Edit Recipe Activity for updating existing recipes
 */

// @REF 1 - https://developer.android.com/develop/ui/views/components/spinner
// @REF 2 - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
// @REF 3 - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html
// @REF 4 - https://www.youtube.com/watch?v=-Gvpf8tXpbc

//imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasecrudapplication.R;
import com.example.firebasecrudapplication.models.Recipe;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
public class EditRecipeActivity extends AppCompatActivity {
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
    private AlertDialog dialog;
    private LinearLayout layout;
    private final ArrayList<String> ingredientList = new ArrayList<>();
    AtomicReference<Boolean> editPageInitialLoad = new AtomicReference<>(true);
    private FirebaseAuth mAuth;

    // on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // use the edit recipe activity layout
        setContentView(R.layout.activity_edit_recipe);

        // set the actionbar title
        setTitle("Edit Recipe");

        // initialise variables
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
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

        Button addIngredient = findViewById(R.id.add);
        layout = findViewById(R.id.container);

        // build the add ingredients alert dialog
        buildDialogAddIngredient();

        // when the add ingredient button is clicked show the add ingredients dialog
        addIngredient.setOnClickListener(v -> dialog.show());

        // update and delete buttons
        Button updateRecipeBtn = findViewById(R.id.idBtnUpdateRecipe);
        Button deleteRecipeBtn = findViewById(R.id.idBtnDeleteRecipe);

        // get the parcelable recipe object received when the edit recipe page was opened
        Recipe recipe = getIntent().getParcelableExtra("recipe");

        // if a recipe is returned from the database
        if (recipe != null){
            recipeNameEdt.setText(recipe.getRecipeName());
            recipeCookingTimeEdt.setText(recipe.getRecipeCookingTime());
            recipePrepTimeEdt.setText(recipe.getRecipePrepTime());
            recipeServingsEdt.setText(recipe.getRecipeServings());
            recipeSuitabilityEdt.setText(recipe.getRecipeSuitedFor());
            String previouslySelectedSuitability = recipe.getRecipeSuitedFor();
            previouslySelectedSuitabilityArray = previouslySelectedSuitability.split(",");
            recipeCuisineEdt.setText(recipe.getRecipeCuisine());
            String previouslySelectedCuisine = recipe.getRecipeCuisine();
            previouslySelectedCuisineArray = previouslySelectedCuisine.split(",");
            recipeImgEdt.setText(recipe.getRecipeImg());
            recipeLinkEdt.setText(recipe.getRecipeLink());
            recipeDescEdt.setText(recipe.getRecipeDescription());
            recipeMethodEdt.setText(recipe.getRecipeMethod());
            String previouslySelectedIngredients = recipe.getRecipeIngredients();
            String[] previouslySelectedIngredientsArray =
                    previouslySelectedIngredients.split(",");

            // add previously added ingredients to view (saved ingredients)
            for(String ingredient : previouslySelectedIngredientsArray){
                addCard(ingredient);
            }

            // after adding previously added ingredients to view set editPageInitialLoad to false
            editPageInitialLoad.set(false);

            recipePublicEdt.setChecked(recipe.getRecipePublic().equals(true));
            recipeID = recipe.getRecipeID();
        }

        // update the firebase database reference to the Recipes object
        databaseReference = firebaseDatabase.getReference("Recipes").child(recipeID);

        // SELECT CUISINE DIALOG START
        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);

        // initialize selected language array
        selectedCuisine = new boolean[cuisineArray.length];

        // booleans for indicating whether the cuisine selection has been updated since the page
        // was loaded (default false)
        AtomicReference<Boolean> cuisineSelectionUpdated = new AtomicReference<>(false);
        AtomicReference<Boolean> newSelectionInitiated = new AtomicReference<>(false);

        // if the cuisine selection has not been updated update the previously select cuisine list
        if(!cuisineSelectionUpdated.get()){
            // store previouslySelectedCuisineArray indexes to array
            for(String cuisine : previouslySelectedCuisineArray){
                cuisine = cuisine.trim();
                int index = Arrays.asList(cuisineArray).indexOf(cuisine);
                selectedCuisine[index] = true;
            }
        }

        // if the recipe cuisine button is clicked execute the following
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
                    // when checkbox selected
                    // Add position  in lang list
                    cuisineList.add(i);
                    // Sort array list
                    Collections.sort(cuisineList);
                    newSelectionInitiated.set(true);

                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    cuisineList.remove(Integer.valueOf(i));
                    newSelectionInitiated.set(true);
                }
            });

            // if the OK button is selected execute the following
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                if(!cuisineSelectionUpdated.get() && !newSelectionInitiated.get()){
                    // add previously selected cuisine to cuisine selection list
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
                    // concat array value
                    stringBuilder.append(cuisineArray[cuisineList.get(j)]);
                    // check condition
                    if (j != cuisineList.size() - 1) {
                        // When j value  not equal
                        // to lang list size - 1
                        // add comma
                        stringBuilder.append(", ");
                    }
                }

                // set text on textView
                recipeCuisineEdt.setText(stringBuilder.toString());

                // set selection updated to True
                cuisineSelectionUpdated.set(true);
            });

            // if the dialog alert Cancel button is selected execute the following
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss dialog
                dialogInterface.dismiss();
            });

            // if the dialog alert Clear All button is selected execute the following
            builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                // use for loop
                for (int j = 0; j < selectedCuisine.length; j++) {
                    // remove all selection
                    selectedCuisine[j] = false;
                    // clear language list
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
        // add items from resource cuisine array to local cuisine array
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

        // if the recipe suitability field button is clicked execute the following
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
                if(!suitabilitySelectionUpdated.get() && !newSuitabilitySelectionInitiated.get()){
                    // add previously selected cuisine to cuisine selection list
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
                    // concat array value
                    stringBuilder2.append(suitabilityArray[suitabilityList.get(j)]);
                    // check condition
                    if (j != suitabilityList.size() - 1) {
                        // When j value  not equal
                        // to lang list size - 1
                        // add comma
                        stringBuilder2.append(", ");
                    }
                }

                // set text on textView
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
            }else {

                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder4 = new AlertDialog.Builder(EditRecipeActivity.this);

                // Set the message show for the Alert time
                builder4.setMessage("Are you sure you to update this recipe?");

                // Set Alert Title
                builder4.setTitle("Update Recipe Confirmation");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder4.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder4.setPositiveButton("Yes", (dialog, which) -> {

                    loadingPB.setVisibility(View.VISIBLE);

                    // build a string with selected ingredients
                    StringBuilder stringBuilder3 = new StringBuilder();
                    // use for loop
                    for (int j = 0; j < ingredientList.size(); j++) {
                        // concat array value
                        stringBuilder3.append(ingredientList.get(j));
                        // check condition
                        if (j != ingredientList.size() - 1) {
                            // When j value  not equal
                            // to lang list size - 1
                            // add comma
                            stringBuilder3.append(", ");
                        }
                    }
                    ingredientsSelectionString = stringBuilder3.toString();

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
                            loadingPB.setVisibility(View.GONE);
                            // update the recipe object using the mapped values defined above
                            databaseReference.updateChildren(map);
                            Toast.makeText(EditRecipeActivity.this, "Recipe Updated", Toast.LENGTH_SHORT).show();
                            // redirect the user to the main activity screen (My Recipes)
                            startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(EditRecipeActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
                        }
                    });

                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder4.setNegativeButton("No", (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder4.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        // delete recipe button action
        deleteRecipeBtn.setOnClickListener(v -> {
            // Create the object of AlertDialog Builder class
            AlertDialog.Builder builder3 = new AlertDialog.Builder(EditRecipeActivity.this);

            // Set the message show for the Alert time
            builder3.setMessage("Are you sure you want to delete this recipe? This action cannot be undone.");

            // Set Alert Title
            builder3.setTitle("Delete Recipe Confirmation");

            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
            builder3.setCancelable(false);

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder3.setPositiveButton("Yes", (dialog, which) -> deleteRecipe());

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder3.setNegativeButton("No", (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder3.create();

            // Show the Alert Dialog box
            alertDialog.show();
        });
    }

    // method for deleting recipe from Firebase Realtime database
    private void deleteRecipe(){
        databaseReference.removeValue();
        Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
    }

    // Ingredient list alert dialog builder for adding ingredients to recipe
    private void buildDialogAddIngredient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.new_ingredient_dialog, null);

        // variable for storing the user input ingredient value from the alert dialog
        final EditText name = view.findViewById(R.id.nameEdit);

        // show the dialog
        builder.setView(view);
        // set the alert dialog title
        builder.setTitle("Enter Ingredient")
                // functionality if alert dialog OK is clicked
                .setPositiveButton("OK", (dialog, which) -> {
                    // if the user input ingredient is blank then execute the following
                    if(name.getText().toString().trim().equals("") || name.getText().toString().equals("Name")){
                        Toast.makeText(this, "Ingredient cannot be blank.", Toast.LENGTH_SHORT).show();
                        name.setText("");
                    }else{// if the user input ingredient is not blank then execute the following
                        // create a card view item for the new ingredient
                        addCard(name.getText().toString());
                        // add the new ingredient to the recipe ingredients list
                        ingredientList.add(name.getText().toString());
                        // clear the input field for adding new ingredients
                        name.setText("");
                    }
                })
                // if the alert dialog Cancel button is clicked then execute the following
                .setNegativeButton("Cancel", (dialog, which) -> name.setText(""));

        dialog = builder.create();
    }

    // method for adding ingredient card view items to recipe edit page
    private void addCard(String name) {
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
            layout.removeView(view);
        });

        // add the ingredient card item to the view
        layout.addView(view);
    }

    // settings menu code start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_main,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.idAddRecipe:
                Intent i1 = new Intent(EditRecipeActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(EditRecipeActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(EditRecipeActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(EditRecipeActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(EditRecipeActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(EditRecipeActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(EditRecipeActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(EditRecipeActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu code end
}