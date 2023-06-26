package com.example.firebasecrudapplication;

//@Ref 1 - https://developer.android.com/develop/ui/views/components/spinner
//@Ref 2 - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/

//imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
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
public class EditRecipeActivity extends AppCompatActivity {
    // text input variables
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipeServingsEdt,
            recipeImgEdt,
            recipeLinkEdt,
            recipeDescEdt,
            recipeMethodEdt,
            recipeIngredientsEdt;
    // switch button
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private ProgressBar loadingPB;
    private DatabaseReference databaseReference;
    private String recipeID;
    private TextView recipeSuitabilityEdt;
    private TextView recipeCuisineEdt;

    private boolean[] selectedCuisine;
    private final ArrayList<Integer> cuisineList = new ArrayList<>();
    private String[] cuisineArray;
    private boolean[] selectedSuitability;
    private final ArrayList<Integer> suitabilityList = new ArrayList<>();
    private String[] suitabilityArray;
    private String[] previouslySelectedCuisineArray;
    private String[] previouslySelectedSuitabilityArray;

    // on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // use the edit recipe activity layout
        setContentView(R.layout.activity_edit_recipe);

        // set the actionbar title
        setTitle("Edit Recipe");

        // initialise variables
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeCuisineEdt = findViewById(R.id.selectCuisineTV);
        recipeSuitabilityEdt = findViewById(R.id.selectSuitabilityTV);
        recipeImgEdt = findViewById(R.id.idEdtRecipeImageLink);
        recipeLinkEdt = findViewById(R.id.idEdtRecipeLink);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        // update and delete buttons
        Button updateRecipeBtn = findViewById(R.id.idBtnUpdateRecipe);
        Button deleteRecipeBtn = findViewById(R.id.idBtnDeleteRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        RecipeRVModal recipeRVModal = getIntent().getParcelableExtra("recipe");

        // if a recipe is returned from the database
        if (recipeRVModal != null){
            recipeNameEdt.setText(recipeRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(recipeRVModal.getRecipeCookingTime());
            recipeServingsEdt.setText(recipeRVModal.getRecipeServings());
            recipeSuitabilityEdt.setText(recipeRVModal.getRecipeSuitedFor());
            String previouslySelectedSuitability = recipeRVModal.getRecipeSuitedFor();
            previouslySelectedSuitabilityArray = previouslySelectedSuitability.split(",");
            recipeCuisineEdt.setText(recipeRVModal.getRecipeCuisine());
            String previouslySelectedCuisine = recipeRVModal.getRecipeCuisine();
            previouslySelectedCuisineArray = previouslySelectedCuisine.split(",");
            recipeImgEdt.setText(recipeRVModal.getRecipeImg());
            recipeLinkEdt.setText(recipeRVModal.getRecipeLink());
            recipeDescEdt.setText(recipeRVModal.getRecipeDescription());
            recipeMethodEdt.setText(recipeRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(recipeRVModal.getRecipeIngredients());
            recipePublicEdt.setChecked(recipeRVModal.getRecipePublic().equals(true));
            recipeID = recipeRVModal.getRecipeID();
        }

        databaseReference = firebaseDatabase.getReference("Recipes").child(recipeID);

        // SELECT CUISINE DIALOG START
        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);

        // initialize selected language array
        selectedCuisine = new boolean[cuisineArray.length];

        AtomicReference<Boolean> cuisineSelectionUpdated = new AtomicReference<>(false);
        AtomicReference<Boolean> newSelectionInitiated = new AtomicReference<>(false);

        if(!cuisineSelectionUpdated.get()){
            // store previouslySelectedCuisineArray indexes to array
            for(String cuisine : previouslySelectedCuisineArray){
                cuisine = cuisine.trim();
                int index = Arrays.asList(cuisineArray).indexOf(cuisine);
                selectedCuisine[index] = true;
            }
        }

        recipeCuisineEdt.setOnClickListener(view -> {

            // Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(EditRecipeActivity.this);

            // set title
            builder.setTitle("Select Cuisine");

            // set dialog non cancelable
            builder.setCancelable(false);

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

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss dialog
                dialogInterface.dismiss();
            });
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

        AtomicReference<Boolean> suitabilitySelectionUpdated = new AtomicReference<>(false);
        AtomicReference<Boolean> newSuitabilitySelectionInitiated = new AtomicReference<>(false);

        if(!suitabilitySelectionUpdated.get()){
            for(String suitability : previouslySelectedSuitabilityArray){
                suitability = suitability.trim();
                int index = Arrays.asList(suitabilityArray).indexOf(suitability);
                selectedSuitability[index] = true;
            }
        }

        recipeSuitabilityEdt.setOnClickListener(view -> {

            // Initialize alert dialog
            AlertDialog.Builder builder2 = new AlertDialog.Builder(EditRecipeActivity.this);

            // set title
            builder2.setTitle("Select Suitability");

            // set dialog non cancelable
            builder2.setCancelable(false);

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
                    // Add position  in lang list
                    suitabilityList.add(i);
                    // Sort array list
                    Collections.sort(suitabilityList);
                    newSuitabilitySelectionInitiated.set(true);

                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    suitabilityList.remove(Integer.valueOf(i));
                    newSuitabilitySelectionInitiated.set(true);
                }
            });

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

            builder2.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // dismiss dialog
                dialogInterface.dismiss();
            });
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

        updateRecipeBtn.setOnClickListener(v -> {
            loadingPB.setVisibility(View.VISIBLE);
            String recipeName = Objects.requireNonNull(recipeNameEdt.getText()).toString();
            String recipeCookingTime = Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString();
            String recipeServings = Objects.requireNonNull(recipeServingsEdt.getText()).toString();
            String recipeSuitedFor = recipeSuitabilityEdt.getText().toString();
            String recipeCuisine = recipeCuisineEdt.getText().toString();
            String recipeImg = Objects.requireNonNull(recipeImgEdt.getText()).toString();
            String recipeLink = Objects.requireNonNull(recipeLinkEdt.getText()).toString();
            String recipeDesc = Objects.requireNonNull(recipeDescEdt.getText()).toString();
            String recipeMethod = Objects.requireNonNull(recipeMethodEdt.getText()).toString();
            String recipeIngredients = Objects.requireNonNull(recipeIngredientsEdt.getText()).toString();
            Boolean recipePublic = recipePublicEdt.isChecked();

            Map<String,Object> map = new HashMap<>();
            map.put("recipeName",recipeName);
            map.put("recipeCookingTime",recipeCookingTime);
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

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loadingPB.setVisibility(View.GONE);
                    databaseReference.updateChildren(map);
                    Toast.makeText(EditRecipeActivity.this, "Recipe Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditRecipeActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
                }
            });

        });

        deleteRecipeBtn.setOnClickListener(v -> deleteRecipe());
    }

    private void deleteRecipe(){
        databaseReference.removeValue();
        Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));

    }
}