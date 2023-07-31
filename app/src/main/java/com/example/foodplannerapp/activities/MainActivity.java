package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * MainActivity.java
 * Description - Main Activity of Java Android App 'FoodPlannerApp'
 */

// @REF 1 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
// Ref Description - User Authentication and CRUD Operation with Firebase Realtime Database in Android

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends BaseMenuActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    private ArrayList<Recipe> recipeArrayList;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private Query query;
    private ImageView noMatchingSearchResultsIcon;
    private TextView noMatchingSearchTextOne,
            noMatchingSearchTextTwo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the activity layout
        setContentView(R.layout.activity_main);

        // set the actionbar title to Recipes
        setTitle("My Recipes");

        // locate layout elements by ID and assign to variables
        // declare variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);
        noMatchingSearchResultsIcon = findViewById(R.id.noSearchResultsIV);
        noMatchingSearchTextOne = findViewById(R.id.no_matching_results);
        noMatchingSearchTextTwo = findViewById(R.id.no_matching_results_help);
        loadingPB = findViewById(R.id.idPBLoading);
        ImageButton addFAB = findViewById(R.id.idAddFAB);

        // declare and initialise recipe Recycler View
        recipeRVAdapter = new RecipeRVAdapter(recipeArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // firebase variables
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");
        // set the query to filter the database reference to recipes that match the logged in users ID
        query = databaseReference.orderByChild("userID").equalTo(userID);

        // if the add floating actions button is clicked then direct user to add recipe page
        addFAB.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                AddRecipeActivity.class)));

        // hide the alert that appears if the user has no recipes
        hideNoRecipesAlert();

        // get all user recipes from firebase database
        getAllUserRecipes();
    }

    // get all user recipe from Firebase Realtime Database
    private void getAllUserRecipes() {
        // clear the recipe arraylist
        recipeArrayList.clear();
        // add value event listener to firebase query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // hide the loading bar
                loadingPB.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // add the retrieved recipes to an ArrayList
                        recipeArrayList.add(issue.getValue(Recipe.class));
                        recipeRVAdapter.notifyDataSetChanged();
                        // if there are no recipes returned then show the no recipes notice
                        if(recipeArrayList.isEmpty()){
                            showNoRecipesAlert();
                        }else{
                            hideNoRecipesAlert();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // if a recipe is clicked
    @Override
    public void onRecipeClick(int position) {
        // then display the bottom sheet dialog for that recipe
        displayBottomSheet(recipeArrayList.get(position));
    }

    // bottom sheet dialog builder and functionality
    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(Recipe recipe){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_user_recipe,
                bottomSheetRL);
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
        Button editBtn = layout.findViewById(R.id.idBtnEdit);
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

        // if the bottom sheet dialog edit recipe button is clicked then direct user to edit recipe
        // page and pass selected recipe object
        editBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, EditRecipeActivity.class);
            i.putExtra("recipe", recipe);
            startActivity(i);
        });

        // if the bottom sheet dialog view details recipe button is clicked then direct user to
        // view recipe page and pass selected recipe object
        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ViewRecipeActivity.class);
            i.putExtra("recipe", recipe);
            startActivity(i);
        });

    }

    // method for hiding notice that there are no recipes
    private void hideNoRecipesAlert(){
        noMatchingSearchResultsIcon.setVisibility(View.GONE);
        noMatchingSearchTextOne.setVisibility(View.GONE);
        noMatchingSearchTextTwo.setVisibility(View.GONE);
    }

    // method for displaying notice that there are no recipes
    private void showNoRecipesAlert(){
        noMatchingSearchResultsIcon.setVisibility(View.VISIBLE);
        noMatchingSearchTextOne.setVisibility(View.VISIBLE);
        noMatchingSearchTextTwo.setVisibility(View.VISIBLE);
    }
}