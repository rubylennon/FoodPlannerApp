package com.example.firebasecrudapplication.activities;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    private ArrayList<Recipe> recipeArrayList;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private FirebaseAuth mAuth;
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
        loadingPB = findViewById(R.id.idPBLoading);
        ImageButton addFAB = findViewById(R.id.idAddFAB);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");
        query = databaseReference.orderByChild("userID").equalTo(userID);
        recipeArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);
        mAuth = FirebaseAuth.getInstance();
        recipeRVAdapter = new RecipeRVAdapter(recipeArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
        noMatchingSearchResultsIcon = findViewById(R.id.noSearchResultsIV);
        noMatchingSearchTextOne = findViewById(R.id.no_matching_results);
        noMatchingSearchTextTwo = findViewById(R.id.no_matching_results_help);

        // if the add floating actions button is clicked then direct user to add recipe page
        addFAB.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                AddRecipeActivity.class)));

        // hide the alert that appears if the user has no recipes
        hideNoRecipesAlert();

        // get all user recipes from firebase database
        getAllUserRecipes();

    }

    private void getAllUserRecipes() {

        // clear the recipe arraylist
        recipeArrayList.clear();

        // add value event listener to firebase query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
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
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_user_recipe,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        // get the bottom sheet dialog elements by ID and assign to local variables
        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeSuitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button editBtn = layout.findViewById(R.id.idBtnEdit);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        // set text in bottom sheet dialog using selected recipe values
        recipeNameTV.setText(recipe.getRecipeName());
        recipeDescTV.setText(recipe.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + recipe.getRecipeSuitedFor());
        recipeCookingTimeTV.setText("Cooking Time: " + recipe.getRecipeCookingTime());

        // load and display the recipe image using the recipe URL
        Picasso.get().load(recipe.getRecipeImg()).into(recipeIV);

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
                Intent i1 = new Intent(MainActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(MainActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(MainActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(MainActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(MainActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(MainActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu code end
}