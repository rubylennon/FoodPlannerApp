/*
 * @Author: Ruby Lennon (x19128355)
 * 13th June 2023
 * RecipeSearchActivity.java
 * Description - Recipe Search Activity of Java Android App 'FoodPlannerApp'
 */

// @Ref 1 - Custom Alert dialog plus send data to activity - https://www.youtube.com/watch?v=qCcsE1_yTCI
// @Ref 2 - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
// @Ref 3 - Search Bar + RecyclerView+Firebase Realtime Database easy Steps - https://www.youtube.com/watch?v=PmqYd-AdmC0

package com.example.firebasecrudapplication;

// imports

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeSearchActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    // RECIPE VARIABLES START
    private ArrayList<RecipeRVModal> recipesList;
//    private ArrayList<RecipeRVModal> updatedRecipesList;
    private DatabaseReference databaseReferenceRecipes;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    // RECIPE VARIABLES END

    // INGREDIENTS VARIABLES START
    private ArrayList<Ingredient> ingredientsList;
    private DatabaseReference ingredientsDBRef;
    // INGREDIENTS VARIABLES END
    private String[] cuisineArray;
    private String[] suitabilityArray;
    private SearchView searchView;
    private ImageView noMatchingSearchResultsIcon;
    private TextView noMatchingSearchTextOne,
            noMatchingSearchTextTwo;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipes);

        // set the actionbar title to Recipes
        setTitle("Recipe Search");

        searchView = findViewById(R.id.searchView);
        noMatchingSearchResultsIcon = findViewById(R.id.noSearchResultsIV);
        noMatchingSearchTextOne = findViewById(R.id.no_matching_results);
        noMatchingSearchTextTwo = findViewById(R.id.no_matching_results_help);

        isMatchingResults();

        // SHARE CODE START
        // declare variables
        loadingPB = findViewById(R.id.idPBLoading);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // SHARE CODE END

        // RECIPES CODE START
        // declare variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        databaseReferenceRecipes = firebaseDatabase.getReference("Recipes");
        recipesList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);

        // recipe RV adapter config
        recipeRVAdapter = new RecipeRVAdapter(recipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // retrieve and display recipes
        getAllRecipes();

        // RECIPES CODE END

        // declare variables
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");

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
                    Toast.makeText(RecipeSearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        // SEARCH CODE END

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
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

        recipesList.clear();
        databaseReferenceRecipes.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipesList.add(snapshot.getValue(RecipeRVModal.class));
                recipeRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Ingredients")
                .setMultiChoiceItems(stringIngredientsArray, null, (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedItems.add(indexSelected);
                    } else if (selectedItems.contains(indexSelected)) {
                        selectedItems.remove(Integer.valueOf(indexSelected));
                    }
                }).setPositiveButton("Search", (dialog, id) -> {
                    //  Your code when user clicked on OK

                    // print selected items array
                    Log.d("selectedCheckbox", selectedItems.toString());

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
                .setMultiChoiceItems(cuisineArray, null, (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedCuisine.add(indexSelected);
                    } else if (selectedCuisine.contains(indexSelected)) {
                        selectedCuisine.remove(Integer.valueOf(indexSelected));
                    }
                }).setPositiveButton("Search", (dialog, id) -> {
                    //  Your code when user clicked on OK

                    // print selected items array
                    Log.d("selectedCheckbox", selectedCuisine.toString());

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
                .setMultiChoiceItems(suitabilityArray, null, (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedSuitability.add(indexSelected);
                    } else if (selectedSuitability.contains(indexSelected)) {
                        selectedSuitability.remove(Integer.valueOf(indexSelected));
                    }
                }).setPositiveButton("Search", (dialog, id) -> {
                    //  Your code when user clicked on OK

                    // print selected items array
                    Log.d("selectedCheckbox", selectedSuitability.toString());

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

    private void filterRecipesByIngredients(ArrayList<Object> selectedItems){

        ArrayList<String> filteredIngredients = new ArrayList<>();

        for(Object object : selectedItems){
            Log.d("selectedItems 2", object.toString());
        }

        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());
            String ingredientName = ingredientsList.get(index).getIngredientName();
            filteredIngredients.add(ingredientName);
        }

        for(String object : filteredIngredients){
            Log.d("filteredIngredients", object);
        }

        // arraylist to store recipes with matching
        ArrayList<RecipeRVModal> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredients then store them to arraylist
        for(RecipeRVModal recipe : recipesList){
            for(String ingredient : filteredIngredients){
                if(recipe.getRecipeIngredients().toLowerCase().contains(ingredient.toLowerCase())){
                    matchingRecipesList.add(recipe);
                    break;
                }
            }
        }

        for(RecipeRVModal object : matchingRecipesList){
            Log.d("matchingRecipesList", object.getRecipeName());
        }

        Log.d("matchingRecipesList Size", String.valueOf(matchingRecipesList.size()));

        // print all recipes ingredients
        for(RecipeRVModal rL : recipesList){
            Log.d("recipesList", rL.getRecipeIngredients());
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    private void filterRecipesByCuisine(ArrayList<Object> selectedItems){
        ArrayList<String> filteredCuisine = new ArrayList<>();

        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());
            String cuisineName = cuisineArray[index];
            filteredCuisine.add(cuisineName);
        }

        for(String object : filteredCuisine){
            Log.d("filteredCuisine", object);
        }

        // arraylist to store recipes with matching
        ArrayList<RecipeRVModal> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredients then store them to arraylist
        for(RecipeRVModal recipe : recipesList){
            for(String cuisine : filteredCuisine){
                if(recipe.getRecipeCuisine().toLowerCase().contains(cuisine.toLowerCase())){
                    matchingRecipesList.add(recipe);
                    break;
                }
            }
        }

        for(RecipeRVModal object : matchingRecipesList){
            Log.d("matchingRecipesList", object.getRecipeName());
        }

        Log.d("matchingRecipesList Size", String.valueOf(matchingRecipesList.size()));

        // print all recipes ingredients
        for(RecipeRVModal rL : recipesList){
            Log.d("recipesList", rL.getRecipeName());
        }

        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    private void filterRecipesBySuitability(ArrayList<Object> selectedItems){
        ArrayList<String> filteredSuitability = new ArrayList<>();

        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());
            String suitabilityName = suitabilityArray[index];
            filteredSuitability.add(suitabilityName);
        }

        for(String object : filteredSuitability){
            Log.d("filteredSuitability", object);
        }

        // arraylist to store recipes with matching
        ArrayList<RecipeRVModal> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredients then store them to arraylist
        for(RecipeRVModal recipe : recipesList){
            for(String suitability : filteredSuitability){
                if(recipe.getRecipeSuitedFor().toLowerCase().contains(suitability.toLowerCase())){
                    matchingRecipesList.add(recipe);
                    break;
                }
            }
        }

        for(RecipeRVModal object : matchingRecipesList){
            Log.d("matchingRecipesList", object.getRecipeName());
        }

        Log.d("matchingRecipesList Size", String.valueOf(matchingRecipesList.size()));

        // print all recipes ingredients
        for(RecipeRVModal rL : recipesList){
            Log.d("recipesList", rL.getRecipeName());
        }

        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    private void search(String str) {
        ArrayList<RecipeRVModal> matchingRecipes = new ArrayList<>();

        if (!str.equals("")){
            for (RecipeRVModal recipe : recipesList){
                if(recipe.getRecipeName().toLowerCase().contains(str.toLowerCase())){
                    matchingRecipes.add(recipe);
                }
            }

            if(matchingRecipes.size() == 0){
                noMatchingResults();
            }else{
                isMatchingResults();
            }

            RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
            recipeRVAdapter = new RecipeRVAdapter(matchingRecipes, this, this);
            recipeRV.setLayoutManager(new LinearLayoutManager(this));
            recipeRV.setAdapter(recipeRVAdapter);
        }

    }

    private void resetRecipesList(){
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(recipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        Toast.makeText(RecipeSearchActivity.this, "Search Cleared", Toast.LENGTH_SHORT).show();

        if(!recipesList.isEmpty()){
            isMatchingResults();
        }else{
            noMatchingResults();
        }
    }

    @Override
    public void onRecipeClick(int position) {
        displayBottomSheet(recipesList.get(position));
    }

    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(RecipeRVModal recipeRVModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeSuitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button editBtn = layout.findViewById(R.id.idBtnEdit);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        recipeNameTV.setText(recipeRVModal.getRecipeName());
        recipeDescTV.setText(recipeRVModal.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + recipeRVModal.getRecipeSuitedFor());
        recipeCookingTimeTV.setText("Cooking Time: " + recipeRVModal.getRecipeCookingTime());
        Picasso.get().load(recipeRVModal.getRecipeImg()).into(recipeIV);

        editBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecipeSearchActivity.this, EditRecipeActivity.class);
            i.putExtra("recipe", recipeRVModal);
            startActivity(i);
        });

        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecipeSearchActivity.this, ViewRecipeActivity.class);
            i.putExtra("recipe", recipeRVModal);
            startActivity(i);
        });

    }
}