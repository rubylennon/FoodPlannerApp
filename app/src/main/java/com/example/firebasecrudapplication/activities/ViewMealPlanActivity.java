package com.example.firebasecrudapplication.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasecrudapplication.R;
import com.example.firebasecrudapplication.models.MealPlanIngredient;
import com.example.firebasecrudapplication.models.MealPlanRVModal;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ViewMealPlanActivity extends AppCompatActivity {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private DatabaseReference databaseReferenceIngredients,
            ingredientsDBRef;
    private String mealPlanID,
            mealPlanDate;
    private MealPlanRVModal mealPlanRVModal;
    private MealPlanIngredient mealPlanIngredient;
    private LinearLayout layout;
    private final AtomicReference<Boolean> initialLoad = new AtomicReference<>(true);
    private ArrayList<MealPlanIngredient> ingredientsList;
    private FirebaseAuth mAuth;

    public ViewMealPlanActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set activity layout
        setContentView(R.layout.activity_view_meal_plan);

        // initialise variables
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        TextInputEditText recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        TextInputEditText recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        TextInputEditText recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        TextInputEditText recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        TextInputEditText recipeCuisineEdt = findViewById(R.id.idEdtRecipeCuisine);
        TextInputEditText recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        TextInputEditText recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        TextInputEditText recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        Button viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        mealPlanRVModal = getIntent().getParcelableExtra("MealPlan");
        layout = findViewById(R.id.shopping_List_item);

        // populate the layout fields with the recipe details from the database
        if (mealPlanRVModal != null) {
            mealPlanDate = mealPlanRVModal.getDateShort();
            recipeNameEdt.setText(mealPlanRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(mealPlanRVModal.getRecipeCookingTime());
            recipeServingsEdt.setText(mealPlanRVModal.getRecipeServings());
            recipeSuitedForEdt.setText(mealPlanRVModal.getRecipeSuitedFor());
            recipeCuisineEdt.setText(mealPlanRVModal.getRecipeCuisine());
            recipeDescEdt.setText(mealPlanRVModal.getRecipeDescription());
            recipeMethodEdt.setText(mealPlanRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(mealPlanRVModal.getRecipeIngredients());
            recipePublicEdt.setChecked(mealPlanRVModal.getRecipePublic().equals(true));
            mealPlanID = mealPlanRVModal.getMealPlanID();
        }

        // set the actionbar title
        setTitle("Meal Details - " + mealPlanDate);

        // set input fields to non focusable
        recipeNameEdt.setFocusable(false);
        recipeCookingTimeEdt.setFocusable(false);
        recipeServingsEdt.setFocusable(false);
        recipeSuitedForEdt.setFocusable(false);
        recipeCuisineEdt.setFocusable(false);
        recipeDescEdt.setFocusable(false);
        recipeMethodEdt.setFocusable(false);
        recipeIngredientsEdt.setFocusable(false);

        // disable input fields
        recipeNameEdt.setEnabled(false);
        recipeCookingTimeEdt.setEnabled(false);
        recipeServingsEdt.setEnabled(false);
        recipeSuitedForEdt.setEnabled(false);
        recipeCuisineEdt.setEnabled(false);
        recipeDescEdt.setEnabled(false);
        recipeMethodEdt.setEnabled(false);
        recipeIngredientsEdt.setEnabled(false);

        // disable input fields cursor visibility
        recipeNameEdt.setCursorVisible(false);
        recipeCookingTimeEdt.setCursorVisible(false);
        recipeServingsEdt.setCursorVisible(false);
        recipeSuitedForEdt.setCursorVisible(false);
        recipeCuisineEdt.setCursorVisible(false);
        recipeDescEdt.setCursorVisible(false);
        recipeMethodEdt.setCursorVisible(false);
        recipeIngredientsEdt.setCursorVisible(false);

        // set switch button to non clickable
        recipePublicEdt.setClickable(false);

        // meal plan ingredients child reference
        ingredientsDBRef = firebaseDatabase.getReference("Meal Plans").child(mealPlanID)
                .child("ingredients");

        // view recipe source page in browser using recipe link
        viewSourceRecipe.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mealPlanRVModal.getRecipeLink()));
            startActivity(i);
        });

        // meal plan ingredients child reference
        databaseReferenceIngredients = firebaseDatabase.getReference("Meal Plans")
                .child(mealPlanID).child("ingredients");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(ingredientsDBRef != null){
            // create ingredients db reference value events listener
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ingredientsList = new ArrayList<>();
                        // store db ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            mealPlanIngredient = ds.getValue(MealPlanIngredient.class);
                            assert mealPlanIngredient != null;
                            mealPlanIngredient.key = ds.getKey();
                            ingredientsList.add(mealPlanIngredient);
                        }

                        if(initialLoad.get()){
                            for(MealPlanIngredient ingredientListItem : ingredientsList){
                                addCard(ingredientListItem.getIngredient(),
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
        // SEARCH CODE END
    }

    // add ingredient cards to layout
    private void addCard(String ingredient, String purchased, String key) {
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

        // if the ingredient checkbox is checked then set the ingredients purchased value to true
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(checkBox.isChecked()){
                Toast.makeText(ViewMealPlanActivity.this, "Shopping List Updated",
                        Toast.LENGTH_SHORT).show();
                databaseReferenceIngredients.child(key).child("purchased")
                        .setValue("true");
            }else{// if the ingredient checkbox is checked then set the ingredients purchased value
                // to false
                Toast.makeText(ViewMealPlanActivity.this, "Shopping List Updated",
                        Toast.LENGTH_SHORT).show();
                databaseReferenceIngredients.child(key).child("purchased")
                        .setValue("false");
            }
        });

        // add the ingredient card to the layout
        layout.addView(view);
    }

    // settings menu start
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
                Intent i1 = new Intent(ViewMealPlanActivity.this,
                        AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(ViewMealPlanActivity.this,
                        MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(ViewMealPlanActivity.this,
                        PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(ViewMealPlanActivity.this,
                        IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(ViewMealPlanActivity.this,
                        RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(ViewMealPlanActivity.this,
                        MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(ViewMealPlanActivity.this,
                        EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(ViewMealPlanActivity.this,
                        LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu end
}