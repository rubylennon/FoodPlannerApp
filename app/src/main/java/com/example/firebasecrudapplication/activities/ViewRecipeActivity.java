package com.example.firebasecrudapplication.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th February 2023
 * ViewRecipeActivity.java
 * Description - Activity for viewing Recipe details
 */

//@REF 1 - https://www.youtube.com/watch?v=33BFCdL0Di0 DatePickerDialog - Android Studio Tutorial
//@REF 2 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc

// imports
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.firebasecrudapplication.fragments.DatePickerFragment;
import com.example.firebasecrudapplication.R;
import com.example.firebasecrudapplication.models.MealPlanIngredient;
import com.example.firebasecrudapplication.models.MealPlanRVModal;
import com.example.firebasecrudapplication.models.RecipeRVModal;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ViewRecipeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
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
    private RecipeRVModal recipeRVModal;
    private FirebaseAuth mAuth;

    public ViewRecipeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the activity layout
        setContentView(R.layout.activity_view_recipe);

        // set the actionbar title
        setTitle("Recipe Details");

        // initialise variables
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeCuisineEdt = findViewById(R.id.idEdtRecipeCuisine);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        Button viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        Button addRecipeToMealPlan = findViewById(R.id.idBtnAddToMealPlan);
        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mAuth = FirebaseAuth.getInstance();

        recipeRVModal = getIntent().getParcelableExtra("recipe");

        // assign database reference to Meal Plan firebase realtime database reference
        databaseReferenceMealPlan = firebaseDatabase.getReference("Meal Plans");

        // populate the layout fields with the recipe details from the database
        if (recipeRVModal != null){
            recipeNameEdt.setText(recipeRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(recipeRVModal.getRecipeCookingTime());
            recipeServingsEdt.setText(recipeRVModal.getRecipeServings());
            recipeSuitedForEdt.setText(recipeRVModal.getRecipeSuitedFor());
            recipeCuisineEdt.setText(recipeRVModal.getRecipeCuisine());
            recipeDescEdt.setText(recipeRVModal.getRecipeDescription());
            recipeMethodEdt.setText(recipeRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(recipeRVModal.getRecipeIngredients());
            recipePublicEdt.setChecked(recipeRVModal.getRecipePublic().equals(true));
            recipeID = recipeRVModal.getRecipeID();
            recipeImgEdt = recipeRVModal.getRecipeImg();
            recipeLinkEdt = recipeRVModal.getRecipeLink();
        }

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

        // view recipe source page in browser using recipe link
        viewSourceRecipe.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(recipeRVModal.getRecipeLink()));
            startActivity(i);
        });

        // view recipe source page in browser using recipe link
        addRecipeToMealPlan.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

    }

    // method for adding recipe to meal plan
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // get selected calendar dates and build calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // set values for new meal plan object
        String currentDateStringShort = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String currentDateStringLong = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String recipeName = Objects.requireNonNull(recipeNameEdt.getText()).toString();
        String recipeCookingTime = Objects.requireNonNull(recipeCookingTimeEdt.getText()).toString();
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
        String mealPlanID = databaseReferenceMealPlan.push().getKey();

        MealPlanRVModal mealPlanRVModal = new MealPlanRVModal(currentDateStringShort, mealPlanID, recipeName, recipeCookingTime, recipeServings, recipeSuitedFor, recipeCuisine, recipeImg, recipeLink, recipeDesc, recipeMethod, recipeIngredients, recipePublic, recipeID, userID, currentDateStringLong);
        assert mealPlanID != null;
        // create new meal plan object
        databaseReferenceMealPlan.child(mealPlanID).setValue(mealPlanRVModal);

        // store ingredients to ingredients child object
        for (String ingredient : ingredientsArray) {
            MealPlanIngredient mealPlanIngredient = new MealPlanIngredient(ingredient.trim(), "false");
            //databaseReferenceMealPlan.child(mealPlanID).child("ingredients").child(ingredient.trim()).setValue(false);
            databaseReferenceMealPlan.child(mealPlanID).child("ingredients").push().setValue(mealPlanIngredient);
        }

        Toast.makeText(ViewRecipeActivity.this, "Recipe Added to Meal Plan", Toast.LENGTH_SHORT).show();

        // redirect user to Meal Planner activity
        startActivity(new Intent(ViewRecipeActivity.this, MealPlanActivity.class));

    }

    // setting menu code start
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
                Intent i1 = new Intent(ViewRecipeActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(ViewRecipeActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(ViewRecipeActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(ViewRecipeActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(ViewRecipeActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(ViewRecipeActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(ViewRecipeActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(ViewRecipeActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // setting menu code end
}