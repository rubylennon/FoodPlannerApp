package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewRecipeActivity extends AppCompatActivity {
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
    private Button viewSourceRecipe;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String recipeID;
    private RecipeRVModal recipeRVModal;

    public ViewRecipeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        // set the actionbar title
        setTitle("Recipe Details");

        // initialise variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeCuisineEdt = findViewById(R.id.idEdtRecipeCuisine);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        recipeRVModal = getIntent().getParcelableExtra("recipe");

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

        // assign database reference to Recipes firebase realtime database reference
        databaseReference = firebaseDatabase.getReference("Recipes").child(recipeID);

        // view recipe source page in browser using recipe link
        viewSourceRecipe.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(recipeRVModal.getRecipeLink()));
            startActivity(i);
        });
    }
}