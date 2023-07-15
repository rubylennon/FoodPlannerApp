package com.example.firebasecrudapplication.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 13th June 2023
 * RecipeSearchActivity.java
 * Description - Recipe Search Activity of Java Android App 'FoodPlannerApp'
 */

// @Ref 1 - Custom Alert dialog plus send data to activity - https://www.youtube.com/watch?v=qCcsE1_yTCI
// @Ref 2 - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
// @Ref 3 - Search Bar + RecyclerView+Firebase Realtime Database easy Steps - https://www.youtube.com/watch?v=PmqYd-AdmC0

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecrudapplication.R;
import com.example.firebasecrudapplication.adapters.RecipeRVAdapter;
import com.example.firebasecrudapplication.models.Ingredient;
import com.example.firebasecrudapplication.models.Recipe;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class RecipeSearchActivity extends AppCompatActivity
        implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    private ArrayList<Recipe> originalRecipesList,
            cuisineFilteredRecipesList,
            ingredientsFilteredRecipesList,
            suitabilityFilteredRecipesList,
            nameFilteredRecipesList;
    private String currentSearch = "";
    private DatabaseReference databaseReferenceRecipes,
            ingredientsDBRef;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private ArrayList<Ingredient> ingredientsList;
    private String[] cuisineArray,
            suitabilityArray;
    private SearchView searchView;
    private ImageView noMatchingSearchResultsIcon;
    private TextView noMatchingSearchTextOne,
            noMatchingSearchTextTwo,
            appliedSearchInfoTV;
    private CardView appliedSearchInfoCV;
    private FirebaseAuth mAuth;
    private Query query;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set activity layout
        setContentView(R.layout.activity_search_recipes);

        // set the actionbar title to Recipes
        setTitle("Recipe Search");

        // find layout elements by ID and assign to class variables
        loadingPB = findViewById(R.id.idPBLoading);
        searchView = findViewById(R.id.searchView);
        noMatchingSearchResultsIcon = findViewById(R.id.noSearchResultsIV);
        noMatchingSearchTextOne = findViewById(R.id.no_matching_results);
        noMatchingSearchTextTwo = findViewById(R.id.no_matching_results_help);
        appliedSearchInfoTV = findViewById(R.id.appliedSearchInfoTV);
        appliedSearchInfoCV = findViewById(R.id.appliedSearchInfoCV);
        mAuth = FirebaseAuth.getInstance();

        // hide current search info help text
        appliedSearchInfoTV.setVisibility(View.GONE);
        appliedSearchInfoCV.setVisibility(View.GONE);

        // hide the alert that there are no search results
        isMatchingResults();

        // get and assign the current Firebase Database instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // RECIPES CODE START
        // declare variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        databaseReferenceRecipes = firebaseDatabase.getReference("Recipes");
        query = databaseReferenceRecipes.orderByChild("userID");
        originalRecipesList = new ArrayList<>();
        cuisineFilteredRecipesList = new ArrayList<>();
        ingredientsFilteredRecipesList = new ArrayList<>();
        suitabilityFilteredRecipesList = new ArrayList<>();
        nameFilteredRecipesList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet_Search);

        // recipe RV adapter config
        recipeRVAdapter = new RecipeRVAdapter(originalRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // retrieve and display recipes
        getAllRecipes();

        // RECIPES CODE END

        // declare variables
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference()
                .child("Ingredients");

        // ingredients filter alert dialog button
        Button mFilterIngredientsButton = findViewById(R.id.BtnIngredientsFilter);

        // show filter alert dialog window
        mFilterIngredientsButton.setOnClickListener(v -> searchByIngredients());

        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);

        // ingredients filter alert dialog button
        Button mFilterCuisineButton = findViewById(R.id.BtnCuisineFilter);

        // show filter alert dialog window
        mFilterCuisineButton.setOnClickListener(v -> searchByCuisine());

        // add items from resource cuisine array to local cuisine array
        suitabilityArray = getResources().getStringArray(R.array.dietary_requirements);

        // ingredients filter alert dialog button
        Button mFilterSuitabilityButton = findViewById(R.id.BtnSuitabilityFilter);

        // show filter alert dialog window
        mFilterSuitabilityButton.setOnClickListener(v -> searchBySuitability());

        // clear search button
        Button clearSearch = findViewById(R.id.clearSearch);

        // clear search button on click listener
        clearSearch.setOnClickListener(v -> resetRecipesList());

    }

    protected void onStart() {

        super.onStart();

        // if the ingredients database reference is not null
        if(ingredientsDBRef != null){
            // create ingredients db reference value events listener
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ingredientsList = new ArrayList<>();
                        // store db ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ingredientsList.add(ds.getValue(Ingredient.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RecipeSearchActivity.this, error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        // SEARCH CODE END

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    appliedSearchInfoTV.setVisibility(View.GONE);
                    appliedSearchInfoCV.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    appliedSearchInfoTV.setVisibility(View.GONE);
                    appliedSearchInfoCV.setVisibility(View.GONE);
                    searchByName(newText);
                    return true;
                }
            });

            searchView.setOnCloseListener(() -> {
                searchView.setQuery("", false);
                searchView.clearFocus();
                resetRecipesList();
                return false;
            });

        }
    }

    private void isMatchingResults(){
        noMatchingSearchResultsIcon.setVisibility(View.GONE);
        noMatchingSearchTextOne.setVisibility(View.GONE);
        noMatchingSearchTextTwo.setVisibility(View.GONE);
    }

    private void noMatchingResults(){
        noMatchingSearchResultsIcon.setVisibility(View.VISIBLE);
        noMatchingSearchTextOne.setVisibility(View.VISIBLE);
        noMatchingSearchTextTwo.setVisibility(View.VISIBLE);
    }

    private void searchByIngredients(){
        searchView.setQuery("", false);
        searchView.clearFocus();

        // add all ingredients from database to arraylist
        ArrayList<Ingredient> allIngredientsList = new ArrayList<>(ingredientsList);

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // pass all ingredients list to show dialog method
        showIngredientsDialog(allIngredientsList);
    }

    private void getAllRecipes() {

        originalRecipesList.clear();
        databaseReferenceRecipes.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {

                        String userID = Objects.requireNonNull(FirebaseAuth.getInstance()
                                .getCurrentUser()).getUid();

                        if(Objects.equals(issue.child("userID").getValue(), userID)
                                && Objects.equals(issue.child("recipePublic")
                                .getValue(), true)){
                            originalRecipesList.add(issue.getValue(Recipe.class));
                            recipeRVAdapter.notifyDataSetChanged();
                        } else if(Objects.equals(issue.child("userID").getValue(), userID)
                                && Objects.equals(issue.child("recipePublic")
                                .getValue(), false)) {
                            originalRecipesList.add(issue.getValue(Recipe.class));
                            recipeRVAdapter.notifyDataSetChanged();
                        } else if(!Objects.equals(issue.child("userID").getValue(), userID)
                                && Objects.equals(issue.child("recipePublic")
                                .getValue(), true)) {
                            originalRecipesList.add(issue.getValue(Recipe.class));
                            recipeRVAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showIngredientsDialog(ArrayList<Ingredient> allIngredientsList){

        // create string arraylist to store all ingredients names
        ArrayList<String> ingredientsStringArrayList = new ArrayList<>();

        // store all ingredients objects to string arraylist
        for(Ingredient object : allIngredientsList){
            ingredientsStringArrayList.add(object.getIngredientName());
        }

        // store string ArrayList of string to object array
        Object[] objectsIngredientsArray = ingredientsStringArrayList.toArray();

        // convert array of object to string array
        String[] stringIngredientsArray = Arrays.stream(objectsIngredientsArray)
                .map(Object::toString)
                .toArray(String[]::new);

        // create arraylist to store selected ingredients in alert dialog
        final ArrayList<Object> selectedItems = new ArrayList<>();

        // construct and configure alert dialog
        @SuppressLint("SetTextI18n") AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Ingredients")
                .setMultiChoiceItems(stringIngredientsArray, null,
                        (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedItems.add(indexSelected);
                    } else if (selectedItems.contains(indexSelected)) {
                        selectedItems.remove(Integer.valueOf(indexSelected));
                    }
                }).setPositiveButton("Search", (dialog, id) -> {
                    //  Your code when user clicked on OK

                    // set the loading progress bar to visible
                    loadingPB.setVisibility(View.VISIBLE);

                    // run filterRecipes method and pass selected ingredients (indexes)
                    filterRecipesByIngredients(selectedItems);

                }).setNegativeButton("Cancel", (dialog, id) -> {
                    //todo
                }).create();

        // show the alert dialog
        alertDialog.show();

    }

    private void searchByCuisine(){

        // create arraylist to store selected ingredients in alert dialog
        final ArrayList<Object> selectedCuisine = new ArrayList<>();

        // construct and configure alert dialog
        AlertDialog alertDialogCuisine = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Cuisine")
                .setMultiChoiceItems(cuisineArray, null,
                        (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedCuisine.add(indexSelected);
                    } else if (selectedCuisine.contains(indexSelected)) {
                        selectedCuisine.remove(Integer.valueOf(indexSelected));
                    }
                }).setPositiveButton("Search", (dialog, id) -> {
                    //  Your code when user clicked on OK

                    // set the loading progress bar to visible
                    loadingPB.setVisibility(View.VISIBLE);

                    // run filterRecipes method and pass selected ingredients (indexes)
                    filterRecipesByCuisine(selectedCuisine);

                }).setNegativeButton("Cancel", (dialog, id) -> {
                    //todo
                }).create();

        // show the alert dialog
        alertDialogCuisine.show();

    }

    private void searchBySuitability(){

        // create arraylist to store selected ingredients in alert dialog
        final ArrayList<Object> selectedSuitability = new ArrayList<>();

        // construct and configure alert dialog
        AlertDialog alertDialogSuitability = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Suitability")
                .setMultiChoiceItems(suitabilityArray, null,
                        (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedSuitability.add(indexSelected);
                    } else if (selectedSuitability.contains(indexSelected)) {
                        selectedSuitability.remove(Integer.valueOf(indexSelected));
                    }
                }).setPositiveButton("Search", (dialog, id) -> {
                    //  Your code when user clicked on OK

                    // set the loading progress bar to visible
                    loadingPB.setVisibility(View.VISIBLE);

                    // run filterRecipes method and pass selected ingredients (indexes)
                    filterRecipesBySuitability(selectedSuitability);

                }).setNegativeButton("Cancel", (dialog, id) -> {
                    //todo
                }).create();

        // show the alert dialog
        alertDialogSuitability.show();

    }

    @SuppressLint("SetTextI18n")
    private void filterRecipesByIngredients(ArrayList<Object> selectedItems){

        ArrayList<String> filteredIngredients = new ArrayList<>();

        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());
            String ingredientName = ingredientsList.get(index).getIngredientName();
            filteredIngredients.add(ingredientName);
        }

        // show applied search filters text
        appliedSearchInfoTV.setVisibility(View.VISIBLE);
        appliedSearchInfoCV.setVisibility(View.VISIBLE);
        appliedSearchInfoTV.setText("Applied Ingredients Search Filters:\n" + filteredIngredients);

        // arraylist to store recipes with matching
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredients then
        // store them to arraylist
        for(Recipe recipe : originalRecipesList){
            for(String ingredient : filteredIngredients){
                if(recipe.getRecipeIngredients().toLowerCase().contains(ingredient.toLowerCase())){
                    matchingRecipesList.add(recipe);
                    break;
                }
            }
        }

        // clear the cuisine filtered list
        ingredientsFilteredRecipesList.clear();

        // add all ingredients from matching list to filtered list
        ingredientsFilteredRecipesList.addAll(matchingRecipesList);

        // update the last search value
        currentSearch = "ingredients";

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void filterRecipesByCuisine(ArrayList<Object> selectedItems){
        ArrayList<String> filteredCuisine = new ArrayList<>();

        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());
            String cuisineName = cuisineArray[index];
            filteredCuisine.add(cuisineName);
        }

        // show applied search filters text
        appliedSearchInfoTV.setVisibility(View.VISIBLE);
        appliedSearchInfoCV.setVisibility(View.VISIBLE);
        appliedSearchInfoTV.setText("Applied Cuisine Search Filters:\n" + filteredCuisine);

        // arraylist to store recipes with matching
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredients then
        // store them to arraylist
        for(Recipe recipe : originalRecipesList){
            for(String cuisine : filteredCuisine){
                if(recipe.getRecipeCuisine().toLowerCase().contains(cuisine.toLowerCase())){
                    matchingRecipesList.add(recipe);
                    break;
                }
            }
        }

        // clear the cuisine filtered list
        cuisineFilteredRecipesList.clear();

        // add all ingredients from matching list to filtered list
        cuisineFilteredRecipesList.addAll(matchingRecipesList);

        // update the last search value
        currentSearch = "cuisine";

        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void filterRecipesBySuitability(ArrayList<Object> selectedItems){
        ArrayList<String> filteredSuitability = new ArrayList<>();

        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());
            String suitabilityName = suitabilityArray[index];
            filteredSuitability.add(suitabilityName);
        }

        // show applied search filters text
        appliedSearchInfoTV.setVisibility(View.VISIBLE);
        appliedSearchInfoCV.setVisibility(View.VISIBLE);
        appliedSearchInfoTV.setText("Applied Suitability Search Filters:\n" + filteredSuitability);

        // arraylist to store recipes with matching
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredients then
        // store them to arraylist
        for(Recipe recipe : originalRecipesList){
            for(String suitability : filteredSuitability){
                if(recipe.getRecipeSuitedFor().toLowerCase().contains(suitability.toLowerCase())){
                    matchingRecipesList.add(recipe);
                    break;
                }
            }
        }

        // clear the cuisine filtered list
        suitabilityFilteredRecipesList.clear();

        // add all ingredients from matching list to filtered list
        suitabilityFilteredRecipesList.addAll(matchingRecipesList);

        // update the last search value
        currentSearch = "suitability";

        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    private void searchByName(String str) {
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        if (!str.equals("")){
            for (Recipe recipe : originalRecipesList){
                if(recipe.getRecipeName().toLowerCase().contains(str.toLowerCase())){
                    matchingRecipesList.add(recipe);
                }
            }

            // if there are no matching results then display no search results layout elements
            if(matchingRecipesList.size() == 0){
                noMatchingResults();
            }else{
                isMatchingResults();
            }

            // clear the cuisine filtered list
            nameFilteredRecipesList.clear();

            // add all ingredients from matching list to filtered list
            nameFilteredRecipesList.addAll(matchingRecipesList);

            // update the last search value
            currentSearch = "name";

            // update the recycler view with the matching recipes list
            RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
            recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this, this);
            recipeRV.setLayoutManager(new LinearLayoutManager(this));
            recipeRV.setAdapter(recipeRVAdapter);
        }

    }

    private void resetRecipesList(){
        // clear applied search text
        appliedSearchInfoTV.setVisibility(View.GONE);
        appliedSearchInfoCV.setVisibility(View.GONE);

        // clear search bar query
        searchView.setQuery("", false);

        // reset the recipe recycler view
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(originalRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // show toast notification to show that search is cleared
        Toast.makeText(RecipeSearchActivity.this, "Search Cleared",
                Toast.LENGTH_SHORT).show();

        // if recipes list is empty then show no results icon and text else hide icon and text
        if(!originalRecipesList.isEmpty()){
            isMatchingResults();
        }else{
            noMatchingResults();
        }

        // clear last search value
        currentSearch = "";
    }

    @Override
    public void onRecipeClick(int position) {
        // based on filtered recipe list set recipe click position
        switch (currentSearch) {
            case "cuisine":
                displayBottomSheet(cuisineFilteredRecipesList.get(position));
                break;
            case "suitability":
                displayBottomSheet(suitabilityFilteredRecipesList.get(position));
                break;
            case "ingredients":
                displayBottomSheet(ingredientsFilteredRecipesList.get(position));
                break;
            case "name":
                displayBottomSheet(nameFilteredRecipesList.get(position));
                break;
            case "":
                displayBottomSheet(originalRecipesList.get(position));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(Recipe recipe){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_recipe_search,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeSuitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView recipeIngredientsTV = layout.findViewById(R.id.idTVIngredients);
        TextView recipeCuisineTV = layout.findViewById(R.id.idTVCuisine);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        recipeNameTV.setText(recipe.getRecipeName());
        recipeDescTV.setText(recipe.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + recipe.getRecipeSuitedFor());
        recipeIngredientsTV.setText("Ingredients: " + recipe.getRecipeIngredients());
        recipeCuisineTV.setText("Cuisine: " + recipe.getRecipeCuisine());
        recipeCookingTimeTV.setText("Cooking Time: " + recipe.getRecipeCookingTime());
        Picasso.get().load(recipe.getRecipeImg()).into(recipeIV);

        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecipeSearchActivity.this, ViewRecipeActivity.class);
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
                Intent i1 = new Intent(RecipeSearchActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(RecipeSearchActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(RecipeSearchActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(RecipeSearchActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(RecipeSearchActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(RecipeSearchActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(RecipeSearchActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(RecipeSearchActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu code end
}