package com.example.firebasecrudapplication.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 9th July 2023
 * PublicRecipesActivity.java
 * Description - Activity for viewing public recipes
 */

// @REF: GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
// Ref Description - User Authentication and CRUD Operation with Firebase Realtime Database in Android

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecrudapplication.R;
import com.example.firebasecrudapplication.adapters.RecipeRVAdapter;
import com.example.firebasecrudapplication.models.Recipe;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicRecipesActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    private ArrayList<Recipe> recipeArrayList;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private FirebaseAuth mAuth;
    private Query query;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set activity layout
        setContentView(R.layout.activity_public_recipes);

        // set the actionbar title to Recipes
        setTitle("Public Recipes");

        // declare variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        loadingPB = findViewById(R.id.idPBLoading);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");
        query = databaseReference.orderByChild("recipePublic").equalTo(true);
        recipeArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet_Public);
        mAuth = FirebaseAuth.getInstance();
        recipeRVAdapter = new RecipeRVAdapter(recipeArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // retrieve and display all public recipes from Firebase Realtime Database
        getAllPublicRecipes();

    }

    // retrieve and display all public recipes from Firebase Realtime Database
    private void getAllPublicRecipes() {

        // clear recipeArrayList before adding recipes
        recipeArrayList.clear();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    Log.d("snapshot2", String.valueOf(dataSnapshot));

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        recipeArrayList.add(issue.getValue(Recipe.class));
                        recipeRVAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // method for displaying clicked recipe bottom sheet dialog
    @Override
    public void onRecipeClick(int position) {
        displayBottomSheet(recipeArrayList.get(position));
    }

    // recipe bottom sheet dialog builder and functionality
    @SuppressLint("SetTextI18n")
    protected void displayBottomSheet(Recipe recipe){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_public_recipe,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        // get the bottom sheet dialog elements by ID and assign to local variables
        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        TextView recipePrepTimeTV = layout.findViewById(R.id.idTVPreparationTime);
        TextView recipeServesTV = layout.findViewById(R.id.idTVServes);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        // set text in bottom sheet dialog using selected recipe values
        recipeNameTV.setText(recipe.getRecipeName());
        recipePrepTimeTV.setText(recipe.getRecipePrepTime() + "m");
        recipeCookingTimeTV.setText(recipe.getRecipeCookingTime() + "m");
        recipeServesTV.setText(recipe.getRecipeServings());

        // load and display the recipe image using the recipe URL
        Picasso.get().load(recipe.getRecipeImg()).into(recipeIV);

        // set recipe description and use spannable to partially style the text view
        String DescriptionLabel = "Description: ";
        String Description = DescriptionLabel + recipe.getRecipeDescription();
        SpannableString content = new SpannableString(Description);
        content.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                DescriptionLabel.length(), 0);
        recipeDescTV.setText(content);

        // view recipe details button
        viewDetailsBtn.setOnClickListener(v -> {
            // redirect user to recipe details page
            Intent i = new Intent(PublicRecipesActivity.this, ViewRecipeActivity.class);
            i.putExtra("recipe", recipe);
            startActivity(i);
        });

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
                Intent i1 = new Intent(PublicRecipesActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(PublicRecipesActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(PublicRecipesActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(PublicRecipesActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(PublicRecipesActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(PublicRecipesActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(PublicRecipesActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(PublicRecipesActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu code end
}