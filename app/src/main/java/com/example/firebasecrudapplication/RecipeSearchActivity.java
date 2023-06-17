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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeSearchActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    private DatabaseReference databaseReferenceRecipes;
    private DatabaseReference databaseReferenceIngredients;
    private ArrayList<RecipeRVModal> recipeRVModalArrayList;
    private ArrayList<Ingredient> ingredientsArrayList;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private IngredientRVAdapter ingredientRVAdapter;
    private TextView tvTest;
    private AlertDialog dialog;
    private AlertDialog dialog2;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipes);

        // set the actionbar title to Recipes
        setTitle("Recipe Search");

        // declare variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        RecyclerView ingredientsRV = findViewById(R.id.idRVIngredients);
        loadingPB = findViewById(R.id.idPBLoading);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceRecipes = firebaseDatabase.getReference("Recipes");
        databaseReferenceIngredients = firebaseDatabase.getReference("Ingredients");
        recipeRVModalArrayList = new ArrayList<>();
        ingredientsArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);

        recipeRVAdapter = new RecipeRVAdapter(recipeRVModalArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        ingredientRVAdapter = new IngredientRVAdapter(ingredientsArrayList);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRV.setAdapter(ingredientRVAdapter);

        // FILTER BUTTON 1 START

        tvTest = findViewById(R.id.TvTest);
        Button filter = findViewById(R.id.BtnFilter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Text");

        //Inflate the custom_dialog view
        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        EditText eTest = view.findViewById(R.id.testInput);
        Button submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            tvTest.setText("Name : " + eTest.getText().toString());
            dialog.dismiss();
        });

        // set this view to dialog
        builder.setView(view);

        // create dialog now
        dialog = builder.create();

        filter.setOnClickListener(v -> dialog.show());

        // FILTER BUTTON 1 END

//        // FILTER BUTTON 2 START
//        Button filter2 = findViewById(R.id.BtnFilter2);
//        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
//        builder2.setTitle("Filter Recipe");
//
//        //Inflate the custom_dialog view
//        View view2 = getLayoutInflater().inflate(R.layout.custom_dialog_2, null);
//        Button submit2 = view.findViewById(R.id.btn_submit);
//        submit2.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        // set this view to dialog
//        builder2.setView(view2);
//
//        // create dialog now
//        dialog2 = builder2.create();
//
//        filter2.setOnClickListener(v -> dialog2.show());
//
//        // FILTER BUTTON 2 END

        getAllRecipes();

        getAllIngredients();

    }

    private void getAllRecipes() {

        recipeRVModalArrayList.clear();
        databaseReferenceRecipes.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipeRVModalArrayList.add(snapshot.getValue(RecipeRVModal.class));
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

    private void getAllIngredients() {

        ingredientsArrayList.clear();
        databaseReferenceIngredients.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientsArrayList.add(snapshot.getValue(Ingredient.class));
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
        displayBottomSheet(recipeRVModalArrayList.get(position));
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