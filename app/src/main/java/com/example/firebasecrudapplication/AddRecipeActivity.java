package com.example.firebasecrudapplication;

//@Ref 1 - https://developer.android.com/develop/ui/views/components/spinner
//@Ref 2 - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
//@Ref 3 - https://codevedanam.blogspot.com/2021/04/dynamic-views-in-android.html

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class AddRecipeActivity extends AppCompatActivity {
    // variables
    private TextInputEditText recipeNameEdt;
    private TextInputEditText recipeCookingTimeEdt;
    private TextInputEditText recipeServingsEdt;
    private TextInputEditText recipeImgEdt;
    private TextInputEditText recipeLinkEdt;
    private TextInputEditText recipeDescEdt;
    private TextInputEditText recipeMethodEdt;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private ProgressBar loadingPB;
    private DatabaseReference databaseReference;
    private String recipeID;
    private String userID;
    private TextView recipeCuisineEdt;
    private boolean[] selectedCuisine;
    private final ArrayList<Integer> cuisineList = new ArrayList<>();
    private String[] cuisineArray;
    private TextView recipeSuitabilityEdt;
    private boolean[] selectedSuitability;
    private final ArrayList<Integer> suitabilityList = new ArrayList<>();
    private String[] suitabilityArray;
    private AlertDialog dialog;
    private LinearLayout layout;
    private final ArrayList<String> ingredientList = new ArrayList<>();
    private String ingredientsSelectionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // set the actionbar title
        setTitle("Add Recipe");

        // initialise variables
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeCuisineEdt = findViewById(R.id.selectCuisineTV);
        recipeSuitabilityEdt = findViewById(R.id.selectSuitabilityTV);
        recipeImgEdt = findViewById(R.id.idEdtRecipeImageLink);
        recipeLinkEdt = findViewById(R.id.idEdtRecipeLink);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        Button addRecipeBtn = findViewById(R.id.idBtnAddRecipe);
        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        loadingPB = findViewById(R.id.idPBLoading);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Recipes");
        Button addIngredient = findViewById(R.id.add);
        layout = findViewById(R.id.container);

        buildDialogAddIngredient();
        addIngredient.setOnClickListener(v -> dialog.show());

        // SELECT CUISINE DIALOG START
        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);

        // initialize selected language array
        selectedCuisine = new boolean[cuisineArray.length];

        recipeCuisineEdt.setOnClickListener(view -> {

            // Initialize alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);

            // set title
            builder.setTitle("Select Cuisine");

            // set dialog non cancelable
            builder.setCancelable(false);

            builder.setMultiChoiceItems(cuisineArray, selectedCuisine, (dialogInterface, i, b) -> {
                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position  in lang list
                    cuisineList.add(i);
                    // Sort array list
                    Collections.sort(cuisineList);
                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    cuisineList.remove(Integer.valueOf(i));
                }
            });

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
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

        recipeSuitabilityEdt.setOnClickListener(view -> {

            // Initialize alert dialog
            AlertDialog.Builder builder2 = new AlertDialog.Builder(AddRecipeActivity.this);

            // set title
            builder2.setTitle("Select Recipe Suitability");

            // set dialog non cancelable
            builder2.setCancelable(false);

            builder2.setMultiChoiceItems(suitabilityArray, selectedSuitability, (dialogInterface, i, b) -> {
                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position  in lang list
                    suitabilityList.add(i);
                    // Sort array list
                    Collections.sort(suitabilityList);
                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    suitabilityList.remove(Integer.valueOf(i));
                }
            });

            builder2.setPositiveButton("OK", (dialogInterface, i) -> {
                // Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                // use for loop
                for (int j = 0; j < suitabilityList.size(); j++) {
                    // concat array value
                    stringBuilder.append(suitabilityArray[suitabilityList.get(j)]);
                    // check condition
                    if (j != suitabilityList.size() - 1) {
                        // When j value  not equal
                        // to lang list size - 1
                        // add comma
                        stringBuilder.append(", ");
                    }
                }
                // set text on textView
                recipeSuitabilityEdt.setText(stringBuilder.toString());
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

        // add recipe button click event action
        addRecipeBtn.setOnClickListener(v -> {

            // if statements to validate that all required fields are populated before adding recipe
            if(Objects.requireNonNull(recipeNameEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add a Recipe Name", Toast.LENGTH_SHORT).show();
            }else if(Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString().equals("")){
                Toast.makeText(AddRecipeActivity.this, "Please add a Cooking Time", Toast.LENGTH_SHORT).show();
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
            }else if(ingredientList.isEmpty()){
                Toast.makeText(AddRecipeActivity.this, "Please add Recipe Ingredients", Toast.LENGTH_SHORT).show();
            }else {

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
                Log.d("stringBuilder3.toString()", stringBuilder3.toString());
                Log.d("ingredientsSelectionString", ingredientsSelectionString);

                loadingPB.setVisibility(View.VISIBLE);

                // get user input text from fields
                String recipeName = recipeNameEdt.getText().toString();
                String recipeCookingTime = recipeCookingTimeEdt.getText().toString();
                String recipeServings = recipeServingsEdt.getText().toString();
                String recipeSuitedFor = recipeSuitabilityEdt.getText().toString();
                String recipeCuisine = recipeCuisineEdt.getText().toString();
                String recipeImg = recipeImgEdt.getText().toString();
                String recipeLink = recipeLinkEdt.getText().toString();
                String recipeDesc = recipeDescEdt.getText().toString();
                String recipeMethod = recipeMethodEdt.getText().toString();
                String recipeIngredients = ingredientsSelectionString;
                Boolean recipePublic = recipePublicEdt.isChecked();
                recipeID = recipeName;
                RecipeRVModal recipeRVModal = new RecipeRVModal(recipeName,recipeCookingTime,recipeServings,recipeSuitedFor,recipeCuisine,recipeImg,recipeLink,recipeDesc,recipeMethod,recipeIngredients,recipePublic,recipeID,userID);

                // add the new recipe to the database
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingPB.setVisibility(View.GONE);
                        databaseReference.child(recipeID).setValue(recipeRVModal);
                        Toast.makeText(AddRecipeActivity.this, "Recipe Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddRecipeActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddRecipeActivity.this, "Error is " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void buildDialogAddIngredient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);

        builder.setView(view);
        builder.setTitle("Enter Ingredient")
                .setPositiveButton("OK", (dialog, which) -> {
                    addCard(name.getText().toString());
                    ingredientList.add(name.getText().toString());
                    Log.d("ingredientList", String.valueOf(ingredientList));
                    name.setText("");
                })
                .setNegativeButton("Cancel", (dialog, which) -> name.setText(""));

        dialog = builder.create();
    }

    private void addCard(String name) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.ingredient_card_rounded, null);

        TextView nameView = view.findViewById(R.id.name);
        Button delete = view.findViewById(R.id.delete);

        nameView.setText(name);

        // delete ingredient button action
        delete.setOnClickListener(v -> {
            ingredientList.remove(name);
            Log.d("ingredientList", String.valueOf(ingredientList));
            layout.removeView(view);
        });

        layout.addView(view);
    }
}