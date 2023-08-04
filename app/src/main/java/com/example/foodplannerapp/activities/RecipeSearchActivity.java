package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 13th June 2023
 * RecipeSearchActivity.java
 * Description - Recipe Search Activity of Java Android App 'FoodPlannerApp'
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.adapters.RecipeRVAdapter;
import com.example.foodplannerapp.models.Ingredient;
import com.example.foodplannerapp.models.Recipe;
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

public class RecipeSearchActivity extends BaseMenuActivity implements RecipeRVAdapter.RecipeClickInterface {
    // declare variables
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
    private Query query;

    // activity onCreate method to be executed when activity is launched
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
        Button mFilterIngredientsButton = findViewById(R.id.BtnIngredientsFilter);
        Button mFilterCuisineButton = findViewById(R.id.BtnCuisineFilter);
        Button mFilterSuitabilityButton = findViewById(R.id.BtnSuitabilityFilter);
        Button clearSearch = findViewById(R.id.clearSearch);

        // add items from resource cuisine array to local cuisine array
        cuisineArray = getResources().getStringArray(R.array.cuisine_array);
        // add items from resource cuisine array to local cuisine array
        suitabilityArray = getResources().getStringArray(R.array.dietary_requirements);

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
        originalRecipesList = new ArrayList<>();
        cuisineFilteredRecipesList = new ArrayList<>();
        ingredientsFilteredRecipesList = new ArrayList<>();
        suitabilityFilteredRecipesList = new ArrayList<>();
        nameFilteredRecipesList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet_Search);

        // create Firebase database 'Recipes' reference
        databaseReferenceRecipes = firebaseDatabase.getReference("Recipes");
        // set the query to filter the database reference to recipes that match the logged in users ID
        query = databaseReferenceRecipes.orderByChild("userID");
        // create Firebase database 'Ingredients' reference
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference()
                .child("Ingredients");

        // recipe RV adapter config
        recipeRVAdapter = new RecipeRVAdapter(originalRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // retrieve and display recipes
        getAllRecipes();

        // show filter alert dialog window
        mFilterIngredientsButton.setOnClickListener(v -> searchByIngredients());
        // show filter alert dialog window
        mFilterSuitabilityButton.setOnClickListener(v -> searchBySuitability());
        // show filter alert dialog window
        mFilterCuisineButton.setOnClickListener(v -> searchByCuisine());
        // clear search button on click listener
        clearSearch.setOnClickListener(v -> resetRecipesList());
    }

    // activity onStart method to be executed when activity is started
    protected void onStart() {
        super.onStart();
        // if the ingredients database reference is not null
        if(ingredientsDBRef != null){
            // create ingredients db reference value events listener
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){// if a data snapshot is retrieved from database using query
                        ingredientsList = new ArrayList<>();// create new ArrayList
                        // store db ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ingredientsList.add(ds.getValue(Ingredient.class));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RecipeSearchActivity.this, error.getMessage(),
                            Toast.LENGTH_SHORT).show();// show toast error
                }
            });
        }

        // @Reference - https://www.youtube.com/watch?v=PmqYd-AdmC0
        // Reference description - tutorial on how to integrate a search bar with a recycler view
        // recipe search bar functionality
        if (searchView != null){// set a search bar query text listener
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {// if text is submitted
                    // hide current search info help text
                    appliedSearchInfoTV.setVisibility(View.GONE);
                    appliedSearchInfoCV.setVisibility(View.GONE);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {// if text changes
                    // hide current search info help text
                    appliedSearchInfoTV.setVisibility(View.GONE);
                    appliedSearchInfoCV.setVisibility(View.GONE);
                    searchByName(newText);// search for recipes using text provided by user
                    return true;
                }
            });
            searchView.setOnCloseListener(() -> {// if the search bar is closed
                searchView.setQuery("", false);
                searchView.clearFocus();
                resetRecipesList();// reset recipes list
                return false;
            });
        }
    }

    // if there are matching recipes hide the no matching recipe text
    private void isMatchingResults(){
        noMatchingSearchResultsIcon.setVisibility(View.GONE);
        noMatchingSearchTextOne.setVisibility(View.GONE);
        noMatchingSearchTextTwo.setVisibility(View.GONE);
    }

    // if there is no matching results show the no matching results text
    private void noMatchingResults(){
        noMatchingSearchResultsIcon.setVisibility(View.VISIBLE);
        noMatchingSearchTextOne.setVisibility(View.VISIBLE);
        noMatchingSearchTextTwo.setVisibility(View.VISIBLE);
    }

    // search by ingredients functionality
    private void searchByIngredients(){
        searchView.setQuery("", false);// clear the search bar
        searchView.clearFocus();// clear the search bar focus
        // add all ingredients from database to arraylist
        ArrayList<Ingredient> allIngredientsList = new ArrayList<>(ingredientsList);
        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);
        // pass all ingredients list to show dialog method
        showIngredientsDialog(allIngredientsList);
    }

    // get all user owned and public recipes from Firebase Realtime Database
    private void getAllRecipes() {
        originalRecipesList.clear();// clear the recipe arraylist
        // add value event listener to firebase query
        databaseReferenceRecipes.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                // notify the recycler view adapter the data has changed
                recipeRVAdapter.notifyDataSetChanged();
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                // notify the recycler view adapter the data has changed
                recipeRVAdapter.notifyDataSetChanged();
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                // notify the recycler view adapter the data has changed
                recipeRVAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // database query value event listener
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // get the current users ID
                        String userID = Objects.requireNonNull(FirebaseAuth.getInstance()
                                .getCurrentUser()).getUid();
                        // only store recipes that belong to the current user or are set to public
                        if(Objects.equals(issue.child("userID").getValue(), userID)
                                && Objects.equals(issue.child("recipePublic")
                                .getValue(), true)){
                            originalRecipesList.add(issue.getValue(Recipe.class));// add the recipe to ArrayList
                            // notify the recycler view adapter the data has changed
                            recipeRVAdapter.notifyDataSetChanged();
                            // get recipes that are owned by the user and are not public
                        } else if(Objects.equals(issue.child("userID").getValue(), userID)
                                && Objects.equals(issue.child("recipePublic")
                                .getValue(), false)) {
                            originalRecipesList.add(issue.getValue(Recipe.class));// add the recipe to ArrayList
                            recipeRVAdapter.notifyDataSetChanged();
                            // get recipes that are owned by the user and are public
                        } else if(!Objects.equals(issue.child("userID").getValue(), userID)
                                && Objects.equals(issue.child("recipePublic")
                                .getValue(), true)) {
                            originalRecipesList.add(issue.getValue(Recipe.class));// add the recipe to ArrayList
                            // notify the recycler view adapter the data has changed
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

    // show the ingredients search dialog with the ingredients retrieved from Firebase Realtime database
    private void showIngredientsDialog(ArrayList<Ingredient> allIngredientsList){
        // create string arraylist to store all ingredients names
        ArrayList<String> ingredientsStringArrayList = new ArrayList<>();
        // store all ingredients objects to string arraylist
        for(Ingredient object : allIngredientsList){
            ingredientsStringArrayList.add(object.getIngredientName());
        }
        // store string ArrayList of string to object array
        Object[] objectsIngredientsArray = ingredientsStringArrayList.toArray();
        // convert array of ingredient objects to string array
        String[] stringIngredientsArray = Arrays.stream(objectsIngredientsArray)
                .map(Object::toString)
                .toArray(String[]::new);
        // create arraylist to store selected ingredients in alert dialog
        final ArrayList<Object> selectedItems = new ArrayList<>();

        // @Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // @Reference 2 - https://www.youtube.com/watch?v=4GdbCl-47wE
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // construct and configure alert dialog
        @SuppressLint("SetTextI18n") AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Ingredients")// set the title
                .setMultiChoiceItems(stringIngredientsArray, null,
                        (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedItems.add(indexSelected);// add the selected ingredient index
                    } else if (selectedItems.contains(indexSelected)) {
                        selectedItems.remove(Integer.valueOf(indexSelected));// remove the selected ingredient index
                    }
                }).setPositiveButton("Search", (dialog, id) -> {// if the search button is clicked
                    // set the loading progress bar to visible
                    loadingPB.setVisibility(View.VISIBLE);
                    // run filterRecipes method and pass selected ingredients (indexes)
                    filterRecipesByIngredients(selectedItems);
                }).setNegativeButton("Cancel", (dialog, id) -> {
                }).create();
        // show the alert dialog
        alertDialog.show();
    }

    // search by cuisine functionality
    private void searchByCuisine(){
        // create arraylist to store selected ingredients in alert dialog
        final ArrayList<Object> selectedCuisine = new ArrayList<>();

        // @Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // @Reference 2 - https://www.youtube.com/watch?v=4GdbCl-47wE
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // construct and configure alert dialog
        AlertDialog alertDialogCuisine = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Cuisine")// set title
                .setMultiChoiceItems(cuisineArray, null,
                        (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedCuisine.add(indexSelected);// add selected cuisine item index to array
                    } else if (selectedCuisine.contains(indexSelected)) {
                        selectedCuisine.remove(Integer.valueOf(indexSelected));// remove selected cuisine item index from array
                    }
                }).setPositiveButton("Search", (dialog, id) -> {// if search button is clicked
                    // set the loading progress bar to visible
                    loadingPB.setVisibility(View.VISIBLE);
                    // run filterRecipes method and pass selected ingredients (indexes)
                    filterRecipesByCuisine(selectedCuisine);
                }).setNegativeButton("Cancel", (dialog, id) -> {
                }).create();
        // show the alert dialog
        alertDialogCuisine.show();
    }

    // search by suitability functionality
    private void searchBySuitability(){
        // create arraylist to store selected ingredients in alert dialog
        final ArrayList<Object> selectedSuitability = new ArrayList<>();

        // @Reference - https://www.geeksforgeeks.org/how-to-implement-multiselect-dropdown-in-android/
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // @Reference 2 - https://www.youtube.com/watch?v=4GdbCl-47wE
        // Reference description - tutorial on how to implement a MultiSelect DropDown in Android
        // construct and configure alert dialog
        AlertDialog alertDialogSuitability = new AlertDialog.Builder(this)
                .setTitle("Search Recipes By Suitability")// set title
                .setMultiChoiceItems(suitabilityArray, null,
                        (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedSuitability.add(indexSelected);// add selected cuisine item index to array
                    } else if (selectedSuitability.contains(indexSelected)) {
                        selectedSuitability.remove(Integer.valueOf(indexSelected));// remove selected cuisine item index from array
                    }
                }).setPositiveButton("Search", (dialog, id) -> {// if search button is clicked
                    // set the loading progress bar to visible
                    loadingPB.setVisibility(View.VISIBLE);
                    // run filterRecipes method and pass selected ingredients (indexes)
                    filterRecipesBySuitability(selectedSuitability);
                }).setNegativeButton("Cancel", (dialog, id) -> {
                }).create();
        // show the alert dialog
        alertDialogSuitability.show();
    }

    // filter the recipes by the selected ingredients
    @SuppressLint("SetTextI18n")
    private void filterRecipesByIngredients(ArrayList<Object> selectedItems){
        ArrayList<String> filteredIngredients = new ArrayList<>();

        // for every object in the selected ingredients array
        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());// get the index value
            // get the ingredient name using the selected index
            String ingredientName = ingredientsList.get(index).getIngredientName();
            // add the ingredient name to the filteredIngredients arraylist
            filteredIngredients.add(ingredientName);
        }
        // show applied search filters text
        appliedSearchInfoTV.setVisibility(View.VISIBLE);
        appliedSearchInfoCV.setVisibility(View.VISIBLE);
        appliedSearchInfoTV.setText("Applied Ingredients Search Filters:\n" + filteredIngredients);

        // arraylist to store recipes with matching
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected ingredient names then
        // store them to matchingRecipesList arraylist
        for(Recipe recipe : originalRecipesList){
            for(String ingredient : filteredIngredients){
                // if the ingredient name is present in any of the stored recipes
                if(recipe.getRecipeIngredients().toLowerCase().contains(ingredient.toLowerCase())){
                    matchingRecipesList.add(recipe);// then add the recipe to the matching recipes list
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
        // if there are no matching recipes then display no matching recipe text on screen
        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }
        // recipe RV adapter config - display matching recipes in Recycler View
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    // filter the recipes by the selected cuisine
    @SuppressLint("SetTextI18n")
    private void filterRecipesByCuisine(ArrayList<Object> selectedItems){
        ArrayList<String> filteredCuisine = new ArrayList<>();

        // for every object in the selected cuisine array
        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());// get the index value
            // get the cuisine name using the selected index
            String cuisineName = cuisineArray[index];
            // add the cuisine name to the filteredCuisine arraylist
            filteredCuisine.add(cuisineName);
        }

        // show applied search filters text
        appliedSearchInfoTV.setVisibility(View.VISIBLE);
        appliedSearchInfoCV.setVisibility(View.VISIBLE);
        appliedSearchInfoTV.setText("Applied Cuisine Search Filters:\n" + filteredCuisine);

        // arraylist to store recipes with matching
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected cuisine names then
        // store them to matchingRecipesList arraylist
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

        // if there are no matching recipes then show no matching recipes text
        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // recipe RV adapter config - display matching recipes in Recycler View
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    // filter the recipes by the selected suitability
    @SuppressLint("SetTextI18n")
    private void filterRecipesBySuitability(ArrayList<Object> selectedItems){
        ArrayList<String> filteredSuitability = new ArrayList<>();

        // for every object in the selected suitability array
        for(Object object : selectedItems){
            int index = Integer.parseInt(object.toString());// get the index value
            // get the suitability name using the selected index
            String suitabilityName = suitabilityArray[index];
            // add the suitability name to the filteredSuitability arraylist
            filteredSuitability.add(suitabilityName);
        }

        // show applied search filters text
        appliedSearchInfoTV.setVisibility(View.VISIBLE);
        appliedSearchInfoCV.setVisibility(View.VISIBLE);
        appliedSearchInfoTV.setText("Applied Suitability Search Filters:\n" + filteredSuitability);

        // arraylist to store recipes with matching
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();

        // loop through the recipes and if they contain any of the selected suitability then
        // store them to matchingRecipesList arraylist
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

        // if there are no matching recipes then show no matching recipes text
        if(matchingRecipesList.size() == 0){
            noMatchingResults();
        }else{
            isMatchingResults();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // recipe RV adapter config - display matching recipes in Recycler View
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this,
                this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    // @Reference - https://www.youtube.com/watch?v=PmqYd-AdmC0
    // Reference description - tutorial on how to integrate a search bar with a recycler view
    private void searchByName(String str) {
        ArrayList<Recipe> matchingRecipesList = new ArrayList<>();
        if (!str.equals("")){// if the query is not blank then add recipes with a matching name to ArrayList
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

    // method for resetting ArrayList
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

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to display a bottom sheet dialog for a clicked item in Recycler View
    // if a recipe is clicked
    @Override
    public void onRecipeClick(int position) {
        // based on filtered recipe list set recipe click position
        switch (currentSearch) {// get position based on how the recipe list is currently filtered
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

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to display a bottom sheet dialog for a clicked item in Recycler View
    // bottom sheet dialog builder and functionality
    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(Recipe recipe){
        // create a bottom sheet dialog object
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // create a view object and set the layouts to be inflated
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_recipe_search,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();// show the bottom sheet dialog

        // get the bottom sheet dialog elements by ID and assign to local variables
        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeSuitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView recipeIngredientsTV = layout.findViewById(R.id.idTVIngredients);
        TextView recipeCuisineTV = layout.findViewById(R.id.idTVCuisine);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        TextView recipeServesTV = layout.findViewById(R.id.idTVServes);
        TextView recipePrepTimeTV = layout.findViewById(R.id.idTVPreparationTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        // set text in bottom sheet dialog using selected recipe values
        recipeNameTV.setText(recipe.getRecipeName());
        recipeDescTV.setText(recipe.getRecipeDescription());
        recipeCookingTimeTV.setText(recipe.getRecipeCookingTime());
        recipeServesTV.setText(recipe.getRecipeServings());
        recipePrepTimeTV.setText(recipe.getRecipeCookingTime());
        Picasso.get().load(recipe.getRecipeImg()).into(recipeIV);

        // @Reference - https://developer.android.com/reference/android/text/style/StyleSpan
        // Reference description - Android guide/documentation on StyleSpan
        // @Reference 2 - https://developer.android.com/reference/android/text/SpannableString
        // Reference description - Android guide/documentation on Spannable
        // set recipe description and use spannable to partially style the text view
        String descriptionLabel = "Description: ";
        String description = descriptionLabel + recipe.getRecipeDescription();
        SpannableString content_description = new SpannableString(description);
        content_description.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                descriptionLabel.length(), 0);
        recipeDescTV.setText(content_description);
        // set recipe description and use spannable to partially style the text view
        String CuisineLabel = "Cuisine: ";
        String cuisine = CuisineLabel + recipe.getRecipeCuisine();
        SpannableString content_cuisine = new SpannableString(cuisine);
        content_cuisine.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                CuisineLabel.length(), 0);
        recipeCuisineTV.setText(content_cuisine);
        // set recipe description and use spannable to partially style the text view
        String suitabilityLabel = "Suitable For: ";
        String suitability = suitabilityLabel + recipe.getRecipeSuitedFor();
        SpannableString content_suitability = new SpannableString(suitability);
        content_suitability.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                suitabilityLabel.length(), 0);
        recipeSuitedForTV.setText(content_suitability);
        // set recipe description and use spannable to partially style the text view
        String ingredientsLabel = "Ingredients: ";
        String ingredients = ingredientsLabel + recipe.getRecipeIngredients();
        SpannableString content_ingredients = new SpannableString(ingredients);
        content_ingredients.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                ingredientsLabel.length(), 0);
        recipeIngredientsTV.setText(content_ingredients);

        // if the bottom sheet dialog view details recipe button is clicked then direct user to
        // view recipe page and pass selected recipe object
        // view recipe details button on click listener
        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecipeSearchActivity.this, ViewRecipeActivity.class);
            i.putExtra("recipe", recipe);
            startActivity(i);
        });
    }
}