package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private Button viewSourceRecipe;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceRecipe;
    private DatabaseReference databaseReferenceMealPlan;
    private String recipeID;
    private String mealPlanID;
    private RecipeRVModal recipeRVModal;
    private Button addRecipeToMealPlan;
    private String userID;
    private String recipeImgEdt;
    private String recipeLinkEdt;

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
        addRecipeToMealPlan = findViewById(R.id.idBtnAddToMealPlan);
        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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

        // assign database reference to Recipes firebase realtime database reference
        databaseReferenceRecipe = firebaseDatabase.getReference("Recipes").child(recipeID);

        // view recipe source page in browser using recipe link
        viewSourceRecipe.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(recipeRVModal.getRecipeLink()));
            startActivity(i);
        });

        // view recipe source page in browser using recipe link
        addRecipeToMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        Log.d("currentDateString", currentDateString);

        String recipeName = recipeNameEdt.getText().toString();
        Log.d("recipeName", recipeName);
        String recipeCookingTime = recipeCookingTimeEdt.getText().toString();
        Log.d("recipeCookingTime", recipeCookingTime);
        String recipeServings = recipeServingsEdt.getText().toString();
        Log.d("recipeServings", recipeServings);
        String recipeSuitedFor = recipeSuitedForEdt.getText().toString();
        Log.d("recipeSuitedFor", recipeSuitedFor);
        String recipeCuisine = recipeCuisineEdt.getText().toString();
        Log.d("recipeCuisine", recipeCuisine);
        String recipeImg = recipeImgEdt;
        Log.d("recipeImg", recipeImg);
        String recipeLink = recipeLinkEdt;
        Log.d("recipeLink", recipeLink);
        String recipeDesc = recipeDescEdt.getText().toString();
        Log.d("recipeDesc", recipeDesc);
        String recipeMethod = recipeMethodEdt.getText().toString();
        Log.d("recipeMethod", recipeMethod);
        String recipeIngredients = recipeIngredientsEdt.getText().toString();
        Log.d("recipeIngredients", recipeIngredients);
        Boolean recipePublic = recipePublicEdt.isChecked();
        Log.d("recipePublic", String.valueOf(recipePublic));
        recipeID = recipeName;
        mealPlanID = recipeName;
        MealPlanRVModal mealPlanRVModal = new MealPlanRVModal(currentDateString,mealPlanID,recipeName,recipeCookingTime,recipeServings,recipeSuitedFor,recipeCuisine,recipeImg,recipeLink,recipeDesc,recipeMethod,recipeIngredients,recipePublic,recipeID,userID);

        databaseReferenceMealPlan.push().setValue(mealPlanRVModal);

        // add the new recipe to the database
        databaseReferenceMealPlan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(ViewRecipeActivity.this, "Recipe Added to Meal Plan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewRecipeActivity.this, MealPlanActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewRecipeActivity.this, "Error is " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}