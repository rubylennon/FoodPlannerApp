/*
 * @Author: Ruby Lennon (x19128355)
 * 13th June 2023
 * RecipeSearchActivity.java
 * Description - Recipe Search Activity of Java Android App 'FoodPlannerApp'
 */

// @REF: Custom Alert dialog plus send data to activity - https://www.youtube.com/watch?v=qCcsE1_yTCI

package com.example.firebasecrudapplication;

// imports

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import java.util.Collections;

public class RecipeSearchActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    // RECIPE VARIABLES START
    private ArrayList<RecipeRVModal> recipesList;
    private ArrayList<RecipeRVModal> recipesListTest;
    private DatabaseReference databaseReferenceRecipes;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    // RECIPE VARIABLES END

    // INGREDIENTS VARIABLES START
    private ArrayList<Ingredient> ingredientsList;
    private DatabaseReference ingredientsDBRef;
    private IngredientRVAdapter ingredientRVAdapter;
    private RecyclerView ingredientsRV;

    // INGREDIENTS VARIABLES END

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipes);

        // set the actionbar title to Recipes
        setTitle("Recipe Search");

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

        // INGREDIENTS CODE START
        // declare variables
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        ingredientsRV = findViewById(R.id.idRVIngredients);

        // ingredients alert dialog button
        Button mFilterButton = findViewById(R.id.BtnFilter);

         // FILTER BUTTON 2 END

        // show filter alert dialog window
        mFilterButton.setOnClickListener(v -> {
            storeIngredientsInArrayList();
        });

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

        // if the ingredients database reference is not null
        if(databaseReferenceRecipes != null){
            // create ingredients db reference value events listener
            databaseReferenceRecipes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        recipesListTest = new ArrayList<>();
                        // store db ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            recipesListTest.add(ds.getValue(RecipeRVModal.class));
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
    }

    private void storeIngredientsInArrayList(){
        // add all ingredients from database to arraylist

        // add all ingredients from database to arraylist
        ArrayList<Ingredient> allIngredientsList = new ArrayList<>(ingredientsList);

        // print all ingredients list
//        for(Ingredient object : allIngredientsList){
//            Log.d("allIngredientsList", object.getIngredientName());
//        }

        // update the ingredients recyclerview with matching ingredients
        RecipeSearchRVAdapter recipeSearchRVAdapter = new RecipeSearchRVAdapter(allIngredientsList);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(RecipeSearchActivity.this));
        ingredientsRV.setAdapter(recipeSearchRVAdapter);

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // pass all ingredients list to show dialog method
        showDialog(allIngredientsList);
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

    private void showDialog(ArrayList<Ingredient> allIngredientsList){

        // print allIngredientsList as input to showDialog
//        for(Ingredient object : allIngredientsList){
//            Log.d("allIngredientsList 2", object.getIngredientName());
//        }

        // create string arraylist to store all ingredients names
        ArrayList<String> ingredientsStringArrayList = new ArrayList<>();

        // store all ingredients objects to string arraylist
        for(Ingredient object : allIngredientsList){
            ingredientsStringArrayList.add(object.getIngredientName());
        }

        // print stored ingredients in string arraylist
//        for(String object : ingredientsStringArrayList){
//            Log.d("ingredientsStringArrayList", object);
//        }

        // store string ArrayList of string to object array
        Object[] objectsIngredientsArray = ingredientsStringArrayList.toArray();

        // Printing array of objects
//        for (Object obj : objects) {
//            Log.d("objects", obj.toString());
//        }

        // convert array of object to string array
        String[] stringIngredientsArray = Arrays.stream(objectsIngredientsArray)
                .map(Object::toString)
                .toArray(String[]::new);

        // print array of strings
//        for (String obj : stringArray) {
//            Log.d("stringArray", obj);
//        }

        // create arraylist to store selected ingredients in alert dialog
        final ArrayList selectedItems = new ArrayList();

        // construct and configure alert dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setTitle("Select Ingredients")
        .setMultiChoiceItems(stringIngredientsArray, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(indexSelected);
                } else if (selectedItems.contains(indexSelected)) {
                    selectedItems.remove(Integer.valueOf(indexSelected));
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on OK

                // print selected items array
                Log.d("selectedCheckbox", selectedItems.toString());

                // set the loading progress bar to visible
                loadingPB.setVisibility(View.VISIBLE);

                // run filterRecipes method and pass selected ingredients (indexes)
                filterRecipes(selectedItems);

                // print selected ingredients
//                for (Object obj : selectedItems) {
//                    Log.d("selectedItems", obj.toString());
//                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //todo
            }
        }).create();

        // show the alert dialog
        alertDialog.show();

    }

//    private void searchIngredients(ArrayList<String> scannedIngredientsList){
//        // create arraylist for storing matching ingredients (scanned & db stored ingredients)
//        ArrayList<Ingredient> matchingIngredientsList = new ArrayList<>();
//
//        // add all ingredients from database to arraylist
//        ArrayList<Ingredient> allIngredientsList = new ArrayList<>(ingredientsList);
//
//        // compare ingredients list and if they match add ingredient to matching arraylist
//        for(Ingredient object : allIngredientsList){
//            if(scannedIngredientsList.toString().toLowerCase().contains(object.getIngredientName().toLowerCase())){
//                matchingIngredientsList.add(object);
//            }
//        }
//
//        // update the ingredients recyclerview with matching ingredients
//        IngredientScannerRVAdapter ingredientScannerRVAdapter = new IngredientScannerRVAdapter(matchingIngredientsList);
//        ingredientsRV.setLayoutManager(new LinearLayoutManager(RecipeSearchActivity.this));
//        ingredientsRV.setAdapter(ingredientScannerRVAdapter);
//
//        // hide loading progress bar
//        loadingPB.setVisibility(View.GONE);
//    }

    private void filterRecipes(ArrayList<Object> selectedItems){

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

        // recipe RV adapter config
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        recipeRVAdapter = new RecipeRVAdapter(matchingRecipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);
    }

    private void getAllIngredients() {

        ingredientsList.clear();
        ingredientsDBRef.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientsList.add(snapshot.getValue(Ingredient.class));
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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