package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewMealPlanActivity extends AppCompatActivity {
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
    private String mealPlanID;
    private MealPlanRVModal mealPlanRVModal;

    public ViewMealPlanActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal_plan);

        // set the actionbar title
        setTitle("MP Recipe Details");

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
        mealPlanRVModal = getIntent().getParcelableExtra("MealPlan");

        // populate the layout fields with the recipe details from the database
        if (mealPlanRVModal != null){
            recipeNameEdt.setText(mealPlanRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(mealPlanRVModal.getRecipeCookingTime());
            recipeServingsEdt.setText(mealPlanRVModal.getRecipeServings());
            recipeSuitedForEdt.setText(mealPlanRVModal.getRecipeSuitedFor());
            recipeCuisineEdt.setText(mealPlanRVModal.getRecipeCuisine());
            recipeDescEdt.setText(mealPlanRVModal.getRecipeDescription());
            recipeMethodEdt.setText(mealPlanRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(mealPlanRVModal.getRecipeIngredients());
            recipePublicEdt.setChecked(mealPlanRVModal.getRecipePublic().equals(true));
            recipeID = mealPlanRVModal.getRecipeID();
            mealPlanID = mealPlanRVModal.getMealPlanID();
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
        databaseReference = firebaseDatabase.getReference("Meal Plans").child(mealPlanID);

        // view recipe source page in browser using recipe link
//        viewSourceRecipe.setOnClickListener(v -> {
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(mealPlanRVModal.getRecipeLink()));
//            startActivity(i);
//        });
    }
}