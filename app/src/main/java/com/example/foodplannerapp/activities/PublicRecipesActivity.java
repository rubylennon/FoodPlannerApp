package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 9th July 2023
 * PublicRecipesActivity.java
 * Description - Activity for viewing public recipes
 */

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.adapters.RecipeRVAdapter;
import com.example.foodplannerapp.models.Recipe;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
// Reference description - tutorial on how to display retrieved data from Firebase realtime database in RecyclerView using adapter class
public class PublicRecipesActivity extends BaseMenuActivity implements RecipeRVAdapter.RecipeClickInterface {
    // declare variables
    private ProgressBar loadingPB;
    private ArrayList<Recipe> recipeArrayList;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private Query query;

    // activity onCreate method to be executed when activity is launched
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
        bottomSheetRL = findViewById(R.id.idRLBSheet_Public);
        recipeArrayList = new ArrayList<>();

        // get instance of Firebase realtime database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // create Firebase database 'Recipes' reference
        DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");
        // set the query to filter the database reference to public recipes
        query = databaseReference.orderByChild("recipePublic").equalTo(true);

        // recycler view
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
        // add value event listener to firebase query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                if (dataSnapshot.exists()) {// if a data snapshot is retrieved from database using query
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // add the retrieved recipes to an ArrayList
                        recipeArrayList.add(issue.getValue(Recipe.class));
                        // notify the recycler view adapter the data has changed
                        recipeRVAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to display a bottom sheet dialog for a clicked item in Recycler View
    // method for displaying clicked recipe bottom sheet dialog
    @Override
    public void onRecipeClick(int position) {
        // then display the bottom sheet dialog for that recipe
        displayBottomSheet(recipeArrayList.get(position));
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to display a bottom sheet dialog for a clicked item in Recycler View
    // recipe bottom sheet dialog builder and functionality
    @SuppressLint("SetTextI18n")
    protected void displayBottomSheet(Recipe recipe){
        // create a bottom sheet dialog object
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // create a view object and set the layouts to be inflated
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_public_recipe,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();// show the bottom sheet dialog

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

        // @Reference - https://developer.android.com/reference/android/text/style/StyleSpan
        // Reference description - Android guide/documentation on StyleSpan
        // @Reference 2 - https://developer.android.com/reference/android/text/SpannableString
        // Reference description - Android guide/documentation on Spannable
        // set recipe description and use spannable to partially style the text view
        String DescriptionLabel = "Description: ";
        String Description = DescriptionLabel + recipe.getRecipeDescription();
        SpannableString content = new SpannableString(Description);
        content.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                DescriptionLabel.length(), 0);
        recipeDescTV.setText(content);

        // if the bottom sheet dialog view details recipe button is clicked then direct user to
        // view recipe page and pass selected recipe object
        viewDetailsBtn.setOnClickListener(v -> {
            // redirect user to recipe details page
            Intent i = new Intent(PublicRecipesActivity.this, ViewRecipeActivity.class);
            i.putExtra("recipe", recipe);
            startActivity(i);// start View Recipe activity
        });
    }
}