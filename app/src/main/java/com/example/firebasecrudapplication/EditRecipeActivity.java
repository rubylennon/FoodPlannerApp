package com.example.firebasecrudapplication;

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
    // update and delete buttons
    private Button updateRecipeBtn,
            deleteRecipeBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String recipeID;
    private RecipeRVModal recipeRVModal;
    private TextView recipeSuitabilityEdt;
    private TextView recipeCuisineEdt;

    private boolean[] selectedCuisine;
    private final ArrayList<Integer> cuisineList = new ArrayList<>();
    private String[] cuisineArray;
    private boolean[] selectedSuitability;
    private final ArrayList<Integer> suitabilityList = new ArrayList<>();
    private String[] suitabilityArray;
    private String previouslySelectedCuisine;
    private String[] previouslySelectedCuisineArray;
    private int[] previouslySelectedCuisineArrayInt;

    // on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // use the edit recipe activity layout
        setContentView(R.layout.activity_edit_recipe);

        // set the actionbar title
        setTitle("Edit Recipe");

        // initialise variables
        firebaseDatabase = FirebaseDatabase.getInstance();
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
        updateRecipeBtn = findViewById(R.id.idBtnUpdateRecipe);
        deleteRecipeBtn = findViewById(R.id.idBtnDeleteRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        recipeRVModal = getIntent().getParcelableExtra("recipe");

        // if a recipe is returned from the database
        if (recipeRVModal != null){
            recipeNameEdt.setText(recipeRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(recipeRVModal.getRecipeCookingTime());
            recipeServingsEdt.setText(recipeRVModal.getRecipeServings());
            recipeSuitabilityEdt.setText(recipeRVModal.getRecipeSuitedFor());
            recipeCuisineEdt.setText(recipeRVModal.getRecipeCuisine());
            previouslySelectedCuisine = recipeRVModal.getRecipeCuisine();
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
                Log.d("matching Index", String.valueOf(index));
                selectedCuisine[index] = true;
            }
        };

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
                        Log.d("matching Index TWO", String.valueOf(index));
                        cuisineList.add(index);
                    }
                }

                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position  in lang list
                    cuisineList.add(i);
                    Log.d("index i", String.valueOf(i));
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
                        Log.d("matching Index TWO", String.valueOf(index));
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

        updateRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                String recipeName = recipeNameEdt.getText().toString();
                String recipeCookingTime = recipeCookingTimeEdt.getText().toString();
                String recipeServings = recipeServingsEdt.getText().toString();
                String recipeSuitedFor = recipeSuitabilityEdt.getText().toString();
                String recipeCuisine = recipeCuisineEdt.getText().toString();
                String recipeImg = recipeImgEdt.getText().toString();
                String recipeLink = recipeLinkEdt.getText().toString();
                String recipeDesc = recipeDescEdt.getText().toString();
                String recipeMethod = recipeMethodEdt.getText().toString();
                String recipeIngredients = recipeIngredientsEdt.getText().toString();
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

            }
        });

        deleteRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe();
            }
        });
    }

    private void deleteRecipe(){
        databaseReference.removeValue();
        Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));

    }
}